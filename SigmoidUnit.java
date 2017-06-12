/* SigmoidUnit.java
 * 
 * Defines a single Sigmoid unit. Sigmoid units can be linked with UnitLinks.
 * Once linked into a network, outputs and delta values can be calculated.
 * 
 */

import java.util.*;

public class SigmoidUnit
{
	/*** Member Variables ***/
	// A list of all inputs to this units.
	public List<UnitLink> inputLinks;
	// A list of all outputs from this unit. 
	public List<UnitLink> outputLinks;
	
	// The output of this unit. Updated when calculateOutput() is called.
	public double output;
	// The delta value for this unit. Used by the backpropagation algorithm.
	public double delta;
	
	
	/*** Member Functions ***/
	// Default constructor.
	public SigmoidUnit()
	{
		output = 0.0;
		delta = 0.0;
		
		inputLinks = new ArrayList<UnitLink>();
		outputLinks = new ArrayList<UnitLink>();
	}
	
	// Returns the number of input links.
	public int numInputLinks()
	{
		return inputLinks.size();
	}
	
	// Returns the number of output links.
	public int numOutputLinks()
	{
		return outputLinks.size();
	}
	
	// Calculate the delta value for this unit.
	// Assumes that the delta of future units, and the output of this unit, have already been calculated.
	// The true output argument will only be used for output units.
	public void calculateDelta(double trueOutput)
	{
		double estimatedOuputError;
		if (numOutputLinks() == 0)
		{ // This node is in the output layer.
			estimatedOuputError = trueOutput - output;
		}
		else
		{
			estimatedOuputError = 0.0;
			for (int i = 0; i < numOutputLinks(); i++)
			{
				estimatedOuputError += outputLinks.get(i).weight * outputLinks.get(i).to.getDelta();
			}
		}
		delta = output * (1.0 - output) * estimatedOuputError;
	}
	
	// Returns the delta value.
	public double getDelta()
	{
		return delta;
	}
	
	// Update the weights of each input-link to this unit. Assumes that calculateDelta has already been called on this.
	public void updateWeights(double learningRate)
	{
		// Update each connecting input weight.
		for (int i = 0; i < numInputLinks(); i++)
		{
			inputLinks.get(i).weight +=
				(learningRate * this.getDelta() * inputLinks.get(i).from.getOutput());
		}
	}
	
	// Calculare the output for this unit. Assumes that the output of previous units has already been calculated.
	public void calculateOutput()
	{
		double net = 0.0;
		for (int i = 0; i < numInputLinks(); i++)
		{
			net += (inputLinks.get(i).weight * inputLinks.get(i).from.getOutput());
		}
		
		output = 1.0 / (1.0 + Math.exp(-net));
	}
	
	// Returns the output value.
	public double getOutput()
	{
		return output;
	}
	
	// Sets the output of this unit. Only used for units in the input layer.
	// Other units will have outputs that are calculated rather than "set."
	public void setOutput(double _output)
	{
		// Ensure that only input units can have outputs artifically set.
		assert(numInputLinks() == 0);
		
		output = _output;
	}
	
	// Returns the maximum input weight magnitude.
	public double maxInputWeightMagnitude()
	{
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < inputLinks.size(); i++)
		{
			if (inputLinks.get(i).weight < min)
			{
				min = inputLinks.get(i).weight;
			}
			if (inputLinks.get(i).weight > max)
			{
				max = inputLinks.get(i).weight;
			}
		}
		
		return Math.max(Math.abs(min), Math.abs(max));
	}
	
	
	// Link the output of u1 to the input of u2, with the initial weight value w.
	public static void LinkUnits(SigmoidUnit u1, SigmoidUnit u2, double w)
	{
		UnitLink link = new UnitLink(u1, u2, w);
		
		u1.outputLinks.add(link);
		u2.inputLinks.add(link);
	}
	
}