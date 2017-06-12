/* FaceImage.java
 * 
 * Stores a face image for manipulation.
 * 
 */

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Scanner;

public class FaceImage
{
	private int[][] greyscaleArray;
	private String name;
	
	private final int height = 120;
	private final int width = 128;
	
	
	// Default constructor.
	public FaceImage()
	{
		
		greyscaleArray = new int[height][width];
		name = "(null)";
	}
	
	// File load constructor.
	public FaceImage(File file, String nameExt)
	{
		greyscaleArray = new int[height][width];
		
		if (!file.exists())
		{
			System.err.printf("Could not load image: file %s not found.%n", file.getName());
			return;
		}
		
		try
		{
			loadFromFile(file, nameExt);
		}
		catch (FileNotFoundException e)
		{
		}
	}
	
	// Returns a deep copy of this image.
	public FaceImage deepCopy()
	{
		FaceImage result = new FaceImage();
		
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				result.greyscaleArray[y][x] = this.greyscaleArray[y][x];
			}
		}
		result.name = this.name;
		
		return result;
	}
	
	// Returns the greyscale value of the image at the passed position.
	public int getValue(int x, int y)
	{
		if (x < 0 || x >= width || y < 0 || y >= height) return 0;
		return greyscaleArray[y][x];
	}
	
	// Returns this image's name.
	public String getName()
	{
		return name;
	}
	
	// Loads a face image from a file.
	public void loadFromFile(File file, String nameExt)
		throws FileNotFoundException
	{
		if (!file.exists())
		{
			throw new FileNotFoundException();
		}
		
		Scanner scanner = new Scanner(file);
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				greyscaleArray[i][j] = scanner.nextInt();
			}
		}
		name = nameExt + file.getName();
	}
	
	// Prints the contents of the face image to stdout.
	public void print()
	{
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				System.out.printf("%d ", greyscaleArray[i][j]);
			}
			System.out.println();
		}
	}
	
	// Returns true iff the true prediction value of the image is known.
	public boolean trueNNOutputKnown()
	{
		return (name.charAt(0) == 'M' || name.charAt(0) == 'F');
	}
	
	// Returns the true prediction value of the image.
	public double trueNNOutput()
	{
		assert(name.charAt(0) == 'M' || name.charAt(0) == 'F');
		
		if (name.charAt(0) == 'M')
		{
			return 1.0;
		}
		else
		{
			return 0.0;
		}
	}
	
	// Returns the face image as a buffered image (for display purposes).
	public Image getBufferedImage()
	{
		BufferedImage bi = new BufferedImage(128, 120, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		
		int color;
		for (int y = 0; y < 120; y++)
		{
			for (int x = 0; x < 128; x++)
			{
				color = getValue(x, y);
				g.setColor(new Color(color, color, color));
				g.drawLine(x, y, x, y);
			}
		}
		g.setColor(new Color(255, 255, 255));
		g.drawString(getName(), 5, 15);
		
		return bi;
	}
}