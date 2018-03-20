package Team26;

import java.io.Serializable;
import java.util.ArrayList;

public class BRINObject implements Serializable
{
	private int referencePage;
	private Object min;
	private Object max;
	private ArrayList<Pair> denseReference;
	
	//Primary Index
	public BRINObject(Object min, Object max, int reference)
	{
		this.min = min;
		this.max = max;
		this.referencePage = reference;
	}
	
	//Secondary Index
	public BRINObject(Object min, Object max, ArrayList<Pair> reference)
	{
		this.min = min;
		this.max = max;
		this.denseReference = reference;
	}
	
	public Object getMin()
	{
		return this.min;
	}
	
	public Object getMax()
	{
		return this.max;
	}
	
	public int getReferencePage()
	{
		return this.referencePage;
	}
	
	public ArrayList<Pair> getDenseReference()
	{
		return this.denseReference;
	}
}
