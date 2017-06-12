/* MatthewMartin.java
 * 
 * Executes the functionality of the neural network.
 * First, use the -train option to train the neural network with input data.
 * Next, use the -test option to predict the values of test data.
 * 
 */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class MatthewMartin
{
	public static void main(String[] args)
	{
		final int hiddenUnits = 16;
		final int trainingRounds = 10;
		final double learningRate = 0.05;
		
		int trainArg = -1;
		int testArg = -1;
		
		boolean parseSuccess = true;
		
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].toLowerCase().equals("-train") || args[i].toLowerCase().equals("train"))
			{
				trainArg = i;
				if (args.length <= i + 2) parseSuccess = false;
			}
			else if (args[i].toLowerCase().equals("-test") || args[i].toLowerCase().equals("test"))
			{
				testArg = i;
				if (args.length <= i + 1) parseSuccess = false;
			}
		}
		
		if (parseSuccess)
		{
			if (trainArg >= 0 || testArg >= 0)
			{
				NeuralNetwork nn = null;
				
				if (trainArg >= 0)
				{ // The -train option was specified.
					// Collect the training data into an image collection.
					ImageCollection maleData = new ImageCollection(args[trainArg + 1], "Male");
					ImageCollection femaleData = new ImageCollection(args[trainArg + 2], "Female");
					ImageCollection normalizedTrainingData = femaleData.duplicate(4).combine(maleData);
					
					if (normalizedTrainingData.numImages() > 0)
					{
						// Train the neural network and save the trained network to the "NeuralNetwork.data" file.
						nn = new NeuralNetwork_AllConnected(hiddenUnits);
						trainNeuralNetwork(nn, normalizedTrainingData, trainingRounds, learningRate);
						nn.saveToFile("NeuralNetwork.data");
					}
					else
					{
						System.out.println("Failed to load any training data.");
					}
				}
				if (testArg >= 0)
				{ // The -test option was specified.
					// Load the test data into an image collection.
					ImageCollection testData = new ImageCollection(args[testArg + 1], "");
					boolean success = (testData.numImages() > 0);
					
					if (nn == null)
					{
						// If the neural network was not created this run of the program, attempt to load it.
						nn = new NeuralNetwork_AllConnected(hiddenUnits);
						success = nn.loadFromFile("NeuralNetwork.data");
					}
					if (success)
					{
						// Run the test data through the neural network and print the results.
						printPredictions(nn, testData);
					}
				}
			}
			else
			{ // No -train or -test option was specified. Print usage information to the user.
				System.out.println("Please specify a -train and/or -test option.");
				System.out.println("Usage: \"java MatthewMartin -train <MaleDir> <FemaleDir>\"");
				System.out.println("       \"java MatthewMartin -test <TestDir>\"");
			}
		}
		else
		{ // Parsing Failed.
			System.out.println("Failed to parse input parameters. Please check parameter arguments.");
		}
		
		// Code to produce the neural network visualization. Not accessible through normal program usage.
		/*
		NeuralNetwork nn = new NeuralNetwork_AllConnected(hiddenUnits);
		ImageCollection femaleData = new ImageCollection("Female");
		ImageCollection maleData = new ImageCollection("Male");
		ImageCollection testData = new ImageCollection("Test");
		ImageCollection normalizedTrainingData = femaleData.duplicate(4).combine(maleData);
		trainNeuralNetwork(nn, normalizedTrainingData, trainingRounds, learningRate);
		
		crossFoldValidation(trainingRounds, 5, normalizedTrainingData, trainingRounds, learningRate);
		
		displayImages(nn.visualizeWeights());
		displayImages(femaleData.getBufferedImages());
		displayImages(maleData.getBufferedImages());
		displayImages(testData.getBufferedImages());
		*/
	}
	
	// Perform crossfold validation on training data. Requires that all images are labeled as Male or Female.
	public static int crossFoldValidation(int hiddenUnits, int numFolds, ImageCollection trainingData, int trainingRounds, double learningRate)
	{
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		ImageCollection[] folds = trainingData.getRandomFolds(numFolds, rand);
		
		System.out.printf("*** Performing %d-fold crossfold validation. %d images total.%n", numFolds, trainingData.numImages());
		System.out.printf("Seed: %d%n%n", seed);
		
		int totalCorrect = 0;
		int testFold = rand.nextInt(folds.length);
		
		NeuralNetwork nn = new NeuralNetwork_AllConnected(hiddenUnits);
		ImageCollection toTrain = new ImageCollection();
		ImageCollection toTest = null;
		
		for (int i = 0; i < folds.length; i++)
		{
			if (i == testFold)
			{
				toTest = folds[i].deepCopy();
			}
			else
			{
				toTrain = toTrain.combine(folds[i]);
			}
		}
		
		System.out.printf(
			"Fold %d used as test fold:  %d images in training fold. %d images in test fold.%n",
			testFold,
			toTrain.numImages(),
			toTest.numImages()
		);
		
		trainNeuralNetwork(nn, toTrain, trainingRounds, learningRate);
		totalCorrect += calculateTestResults(nn, toTest);
		System.out.println();
		
		
		return totalCorrect;
	}
	
	// Train the neural network. Assumes that all images in the image collection are labeled.
	public static void trainNeuralNetwork(NeuralNetwork nn, ImageCollection ic, int trainingRounds, double learningRate)
	{
		ImageCollection icClone = ic.deepCopy();
		int trainingOperations = trainingRounds * icClone.numImages();
		
		System.out.printf("Training %d rounds on %d images (%d image-trainings).%n", trainingRounds, ic.numImages(), trainingOperations);
		System.out.println(" Please wait while the neural network is trained:");
		System.out.println("|                                                |");
		System.out.print(" ");
		
		int printDots = 48;
		int printDotRoundWait = trainingOperations / printDots;
		int dotCounter = printDotRoundWait;
		
		for (int round = 0; round < trainingRounds; round++)
		{
			icClone.shuffle();
			
			FaceImage image;
			for (int i = 0; i < icClone.numImages(); i++)
			{
				image = icClone.getImage(i);
				nn.calculateOutputs(image);
				nn.backpropagateUpdate(image.trueNNOutput(), learningRate);
				
				dotCounter--;
				if (dotCounter == 0)
				{
					System.out.print("*");
					dotCounter = printDotRoundWait;
				}
			}
		}
		System.out.println();
	}
	
	// Output the neural network predictions to stdout.
	public static void printPredictions(NeuralNetwork nn, ImageCollection testSet)
	{
		int longestStr = 0;
		for (int i = 0; i < testSet.numImages(); i++)
		{
			int curr = testSet.getImage(i).getName().length();
			if (curr > longestStr) longestStr = curr;
		}
		
		for (int i = 0; i < testSet.numImages(); i++)
		{
			nn.calculateOutputs(testSet.getImage(i));
			double result = nn.getFinalOutput();
			
			System.out.printf(
				"%" + longestStr + "s    %6s    %.4f%n",
				testSet.getImage(i).getName(),
				convertPredictionDoubleToStr(result),
				calculateConfidence(result)
			);
		}
	}
	
	// Assumes that all images in the test set are labeled (M or F at start of name).
	public static int calculateTestResults(NeuralNetwork nn, ImageCollection testSet)
	{
		double[] accuracies = new double[testSet.numImages()];
		
		int numCorrect = 0;
		
		double result;
		double expected;
		double error;
		double accuracy;
		boolean isCorrect;
		for (int i = 0; i < testSet.numImages(); i++)
		{
			nn.calculateOutputs(testSet.getImage(i));
			
			result = nn.getFinalOutput();
			expected = testSet.getImage(i).trueNNOutput();
			error = getError(expected, result);
			accuracy = getAccuracy(expected, result);
			isCorrect = (error < 0.5);
			
			accuracies[i] = accuracy;
			
			System.out.printf(
				"%19s  expected: %f    result: %f    error: %f    accuracy: %f    correct? %s%n",
				"\"" + testSet.getImage(i).getName() + "\"",
				expected,
				result,
				error,
				accuracy,
				isCorrect ? "TRUE" : "FALSE"
			);
			
			if (isCorrect)
			{
				numCorrect++;
			}
		}
		
		double accuracyMean = 0.0;
		for (int i = 0; i < accuracies.length; i++)
		{
			accuracyMean += accuracies[i];
		}
		accuracyMean /= (double)accuracies.length;
		
		double accuracyStdDev = 0.0;
		for (int i = 0; i < accuracies.length; i++)
		{
			accuracyStdDev += Math.pow(accuracies[i] - accuracyMean, 2);
		}
		accuracyStdDev /= (double)accuracies.length;
		accuracyStdDev = Math.sqrt(accuracyStdDev);
		
		System.out.printf("%d / %d (%.2f%%) correct.%n", numCorrect, testSet.numImages(), 100.0 * ((float)numCorrect) / (float)testSet.numImages());
		System.out.printf("              Accuracy Mean: %.4f%n", accuracyMean);
		System.out.printf("Accuracy Standard Deviation: %.4f%n", accuracyStdDev);
		
		return numCorrect;
	}
	
	// Returns the accuracy from a true and expected value.
	public static double getAccuracy(double expected, double value)
	{
		return 1.0 - Math.abs(expected - value);
	}
	
	// Returns the error from a true and expected value.
	public static double getError(double expected, double value)
	{
		return Math.abs(expected - value);
	}
	
	// Converts a prediction from double to string.
	public static String convertPredictionDoubleToStr(double predictionValue)
	{
		if (predictionValue < 0.5)
		{
			return "FEMALE";
		}
		else if (predictionValue > 0.5)
		{
			return "MALE";
		}
		else
		{
			return "UNDECIDED";
		}
	}
	
	// Returns the confidence of a predicted value.
	public static double calculateConfidence(double predictionValue)
	{
		return Math.abs(predictionValue - 0.5) + 0.5;
	}
	
	// Displays the passsed image.
	public static void displayImage(Image img)
	{
		Image[] images = new Image[1];
		images[0] = img;
		displayImages(images);
	}
	
	// Displays the passed images.
	public static void displayImages(Image[] images)
	{
		JFrame frame = new JFrame();
		
		frame.getContentPane().add(new Display(images));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(128 * Display.getWidth(images.length), 120 * Display.getHeight(images.length));
		frame.setVisible(true);
	}
}

