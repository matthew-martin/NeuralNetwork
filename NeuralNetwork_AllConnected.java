/* NeuralNetwork_AllConnected.java
 * 
 * Defines a totally connected neural network with a single hidden layer.
 * The number of hidden units is defined upon initialization.
 * 
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.io.*;

public class NeuralNetwork_AllConnected implements NeuralNetwork
{
	SigmoidUnit[][] inputUnits;
	SigmoidUnit[] hiddenUnits;
	SigmoidUnit outputUnit;
	
	Random rand = new Random();
	
	// Constructor.
	public NeuralNetwork_AllConnected(int numHiddenUnits)
	{
		// Defines the range for random link weight initialization.
		double epsilon = 0.01;
		
		// Initialize the input layer.
		inputUnits = new SigmoidUnit[128][120];
		for (int i = 0; i < 128; i++)
		{
			for (int j = 0; j < 120; j++)
			{
				inputUnits[i][j] = new SigmoidUnit();
			}
		}
		
		// Initialize the hidden layer, and link all input units to each hidden unit.
		// Also link each hidden unit to the output unit.
		hiddenUnits = new SigmoidUnit[numHiddenUnits];
		outputUnit = new SigmoidUnit();
		for (int hu = 0; hu < numHiddenUnits; hu++)
		{
			SigmoidUnit curr = new SigmoidUnit();
			
			for (int i = 0; i < 128; i++)
			{
				for (int j = 0; j < 120; j++)
				{
					SigmoidUnit.LinkUnits(inputUnits[i][j], curr, (rand.nextDouble() * epsilon) - (epsilon / 2.0));
				}
			}
			
			hiddenUnits[hu] = curr;
			SigmoidUnit.LinkUnits(curr, outputUnit, (rand.nextDouble() * epsilon) - (epsilon / 2.0));
		}
	}
	
	// Update the neural network to store the calculated output of each unit.
	public void calculateOutputs(FaceImage face)
	{
		// Get outputs of input layer (just the value of each pixel in the image, converted to [0.0 - 1.0]).
		for (int i = 0; i < 128; i++)
		{
			for (int j = 0; j < 120; j++)
			{
				inputUnits[i][j].setOutput((float)face.getValue(i, j) / 255.0);
			}
		}
		
		// Calculate the outputs of the hidden layer.
		for (int i = 0; i < hiddenUnits.length; i++)
		{
			hiddenUnits[i].calculateOutput();
		}
		
		// Calculate the output of the output layer.
		outputUnit.calculateOutput();
	}
	
	// Returns the output of the output layer.
	public double getFinalOutput()
	{
		return outputUnit.getOutput();
	}
	
	// Update weights in the neural networks based on the current output values, using backpropagation.
	// Assumes that calculateOutputs has already been used.
	public void backpropagateUpdate(double trueOutput, double learningRate)
	{
		// Calculate deltas for hidden and output units.
		outputUnit.calculateDelta(trueOutput);
		for (int i = 0; i < hiddenUnits.length; i++)
		{
			hiddenUnits[i].calculateDelta(trueOutput);
		}
		
		// Update weights for hidden and output units.
		outputUnit.updateWeights(learningRate);
		for (int i = 0; i < hiddenUnits.length; i++)
		{
			hiddenUnits[i].updateWeights(learningRate);
		}
	}
	
	// Saves the neural network to a file. Returns true iff the operation was successful.
	public boolean saveToFile(String fileName)
	{
		File file;
		FileWriter fileWriter;
		
		try
		{
			file = new File(fileName);
			fileWriter = new FileWriter(file);
			
			// Write the number of hidden units.
			fileWriter.write(String.format("%d%n", hiddenUnits.length));
			
			// Write the hidden unit weights.
			for (int hu = 0; hu < hiddenUnits.length; hu++)
			{
				SigmoidUnit currHu = hiddenUnits[hu];
				int linkNum = 0;
				for (int i = 0; i < 128; i++)
				{
					for (int j = 0; j < 120; j++)
					{
						fileWriter.write(String.format("%f ", currHu.inputLinks.get(linkNum).weight));
						linkNum++;
					}
				}
				fileWriter.write(String.format("%n"));
			}
			
			// Write the output unit weights.
			for (int i = 0; i < hiddenUnits.length; i++)
			{
				fileWriter.write(String.format("%f ", outputUnit.inputLinks.get(i).weight));
			}
			fileWriter.write(String.format("%n"));
			
			fileWriter.flush();
			fileWriter.close();
			
			return true;
		}
		catch (IOException e)
		{
			System.out.printf("Error saving neural network to file \"%s\".%n", fileName);
		}
		return false;
	}
	
	// Load the neural network from a file. Returns true iff the operation was successful.
	public boolean loadFromFile(String fileName)
	{
		File file;
		Scanner scanner;
		
		try
		{
			file = new File(fileName);
			scanner = new Scanner(file);
			
			// Read the number of hidden units.
			int numHu = scanner.nextInt();
			assert(numHu == hiddenUnits.length);
			
			// Read the hidden unit weights.
			for (int hu = 0; hu < hiddenUnits.length; hu++)
			{
				SigmoidUnit currHu = hiddenUnits[hu];
				int linkNum = 0;
				for (int i = 0; i < 128; i++)
				{
					for (int j = 0; j < 120; j++)
					{
						currHu.inputLinks.get(linkNum).weight = scanner.nextDouble();
						
						linkNum++;
					}
				}
			}
			
			// Read the output unit weights.
			for (int i = 0; i < hiddenUnits.length; i++)
			{
				outputUnit.inputLinks.get(i).weight = scanner.nextDouble();
			}
			
			scanner.close();
			
			return true;
		}
		catch (FileNotFoundException e)
		{
			System.out.printf("Error loading neural network from file \"%s\".%n", fileName);
		}
		return false;
	}
	
	// Returns an array of images (one for each hidden unit) that visualizes the link weights using greyscale values.
	public Image[] visualizeWeights()
	{
		BufferedImage[] result = new BufferedImage[hiddenUnits.length];
		
		for (int hu = 0; hu < hiddenUnits.length; hu++)
		{
			BufferedImage bi = new BufferedImage(128, 120, BufferedImage.TYPE_INT_RGB);
			Graphics g = bi.getGraphics();
			
			SigmoidUnit currHu = hiddenUnits[hu];
			
			double maxW = currHu.maxInputWeightMagnitude();
			
			int linkNum = 0;
			for (int i = 0; i < 128; i++)
			{
				for (int j = 0; j < 120; j++)
				{
					double w = Math.abs(currHu.inputLinks.get(linkNum).weight);
					int intensity = (int)Math.round((w / maxW) * 255.0);
					
					g.setColor(new Color(intensity, intensity, intensity));
					g.drawLine(i, j, i, j);
					
					linkNum++;
				}
			}
			
			result[hu] = bi;
		}
		
		return result;
	}
}