/* ImageCollection.java
 * 
 * Stores a list of face images for manipulation by the neural network.
 * 
 */

import java.io.File;
import java.util.*;
import java.awt.Image;

public class ImageCollection
{
	// Stores the list of face images.
	private List<FaceImage> images;
	
	// Default constructor.
	public ImageCollection()
	{
		images = new ArrayList<FaceImage>();
	}
	
	// Folder loading constructor.
	public ImageCollection(String folderName, String imageNameExt)
	{
		images = new ArrayList<FaceImage>();
		
		boolean success = loadImages(folderName, imageNameExt);
		if (!success)
		{
			System.out.printf("Failed to load images from directory \"%s\".%n", folderName);
		}
	}
	
	// Returns a deep copy of this image collection.
	public ImageCollection deepCopy()
	{
		ImageCollection result = new ImageCollection();
		
		for (int i = 0; i < images.size(); i++)
		{
			result.images.add(images.get(i).deepCopy());
		}
		
		return result;
	}
	
	// Loads the contents of the folder into this image collection.
	public boolean loadImages(String folderName, String imageNameExt)
	{
		File folder = new File(folderName);
		
		if (folder == null || folder.listFiles() == null) return false;
		
		int numFiles = 0;
		for (File fileEntry : folder.listFiles())
		{
			String currName = fileEntry.getName();
			if (currName.length() > 4 && currName.substring(currName.length() - 4).equals(".txt"))
			{ // Only examine ".txt" files.
				String ext = "";
				if (imageNameExt.length() > 0)
				{
					ext = imageNameExt + "/";
				}
				
				images.add(new FaceImage(fileEntry, ext));
				
				numFiles++;
			}
		}
		
		System.out.printf("Loaded %d files from directory \"%s\".%n", numFiles, folderName);
		
		return true;
	}
	
	// Returns the number of images in this collection.
	public int numImages()
	{
		return images.size();
	}
	
	// Returns the image at the passed index from this collection.
	public FaceImage getImage(int index)
	{
		if (index < 0 || index >= images.size())
		{
			return null;
		}
		else
		{
			return images.get(index);
		}
	}
	
	// Returns this collection, split into n random "folds."
	public ImageCollection[] getRandomFolds(int numFolds, Random rand)
	{
		ImageCollection[] result = new ImageCollection[numFolds];
		for (int i = 0; i < numFolds; i++)
		{
			result[i] = new ImageCollection();
		}
		
		ImageCollection temp = this.deepCopy();
		
		int curr = 0;
		while (temp.images.size() > 0)
		{
			result[curr].images.add(temp.images.remove(rand.nextInt(temp.images.size())));
			
			curr++;
			if (curr >= numFolds) curr = 0;
		}
		
		return result;
	}
	
	// Returns the fold at the passed index.
	public ImageCollection getFold(int index, int numFolds)
	{
		int numImages = images.size();
		
		int minFoldSize = numImages / numFolds;
		int foldsPlusOne = numImages % numFolds;
		
		int curr = 0;
		int foldSize = -1;
		for (int i = 0; i < numFolds; i++)
		{
			foldSize = minFoldSize;
			if (foldsPlusOne > 0)
			{
				foldSize++;
				foldsPlusOne--;
			}
			
			if (i == index) break;
			
			curr += foldSize;
		}
		
		ImageCollection result = new ImageCollection();
		for (int i = 0; i < foldSize; i++)
		{
			result.images.add(getImage(i + curr));
		}
		
		return result;
	}
	
	// Adds the images from the passed collection to this collection.
	public ImageCollection combine(ImageCollection other)
	{
		ImageCollection result = this.deepCopy();
		
		for (int i = 0; i < other.numImages(); i++)
		{
			result.images.add(other.images.get(i).deepCopy());
		}
		
		return result;
	}
	
	// Duplicates the contents of this collection n times.
	public ImageCollection duplicate(int n)
	{
		ImageCollection result = this.deepCopy();
		
		for (int i = 0; i < n - 1; i++)
		{
			result = result.combine(this);
		}
		
		return result;
	}
	
	// Randomly shuffles the order of the images in this collection.
	public void shuffle()
	{
		Random rand = new Random();
		
		List<FaceImage> newImages = new ArrayList<FaceImage>();
		while (images.size() != 0)
		{
			newImages.add(images.remove(rand.nextInt(images.size())));
		}
		images = newImages;
	}
	
	// Assumes that all image names are in the format "Test/x.txt", where x is an integer 1, 2, ...
	// Do not use in final submission (files without .txt extension will crash).
	public void sortTestCollection()
	{
		System.out.println("sortTestCollection");
		
		ImageCollection thisCopy = this.deepCopy();
		ImageCollection results = new ImageCollection();
		
		int numImages = numImages();
		
		for (int imageNum = 1; imageNum <= numImages; imageNum++)
		{
			for (int i = 0; i < thisCopy.numImages(); i++)
			{
				if (thisCopy.images.get(i).getName().equals("Test/" + imageNum + ".txt"))
				{
					results.images.add(thisCopy.images.remove(i));
					break;
				}
			}
		}
		
		assert(thisCopy.numImages() == 0);
		this.images = results.images;
	}
	
	// Returns the contents of this image collection as an array of images.
	public Image[] getBufferedImages()
	{
		Image[] result = new Image[numImages()];
		
		for (int i = 0; i < numImages(); i++)
		{
			result[i] = images.get(i).getBufferedImage();
		}
		
		return result;
	}
	
	// Prints the image names in this collection to stdout (used for debugging).
	public void debug_printImageNames()
	{
		for (int i = 0; i < images.size(); i++)
		{
			System.out.printf("\"%s\"%n", images.get(i).getName());
		}
	}
}