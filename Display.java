/* Display.java
 * 
 * Used to assist in the displaying of images.
 * 
 */

import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Display extends javax.swing.JPanel
{
	private Image[] images;
	
	// Constructors.
	public Display(Image image)
	{
		this.images = new Image[1];
		images[0] = image;
	}
	public Display(Image[] images)
	{
		this.images = images;
	}
	
	// Assists in displaying the images.
	public void paint(Graphics g)
	{
		int n = images.length;
		
		int h = getHeight(n);
		int w = getWidth(n);
		
		int index = 0;
		for (int i = 0; i < w; i++)
		{
			for (int j = 0; j < h; j++)
			{
				g.drawImage(images[index], 128 * i, 120 * j, this);
				
				index++;
				if (index >= images.length) break;
			}
			if (index >= images.length) break;
		}
	}
	
	// Returns the height of the image-display pane.
	public static int getHeight(int n)
	{
		int hMax = 8;
		if (n >= hMax)
		{
			return hMax;
		}
		else
		{
			return n;
		}
	}
	
	// Returns the widht of the image-display pane.
	public static int getWidth(int n)
	{
		int hMax = 8;
		if (n <= hMax)
		{
			return 1;
		}
		else
		{
			return ((n / getHeight(n)) + 1);
		}
	}
}