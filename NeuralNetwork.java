/* NeuralNetwork.java
 * 
 * Defines the interface for neural network implementation.
 * 
 */

import java.awt.*;
import java.io.*;

public interface NeuralNetwork
{
	// Calculates the output value for each unit in the network (these values are stored in the units).
	void calculateOutputs(FaceImage face);
	// Returns the final output from the neural network.
	double getFinalOutput();
	// Updates the neural network using backpropagation.
	void backpropagateUpdate(double trueOutput, double learningRate);
	
	// Save/load the neural network to/from a file.
	boolean saveToFile(String fileName);
	boolean loadFromFile(String fileName);
	
	// Returns an image array used to visualze the trained neural network weights.
	Image[] visualizeWeights();
}