/* UnitLink.java
 * 
 * Defines a link to attach two Sigmoid units. Stores the weight of the unit link.
 * 
 */

public class UnitLink
{
	public UnitLink(SigmoidUnit from, SigmoidUnit to, double weight)
	{
		this.from = from;
		this.to = to;
		this.weight = weight;
	}
	
	public SigmoidUnit from;
	public SigmoidUnit to;
	public double weight;
}