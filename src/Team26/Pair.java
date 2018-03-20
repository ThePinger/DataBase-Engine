package Team26;

public class Pair 
{
	private Object obj;
	private int page;
	
	public Pair(Object obj, int page)
	{
		this.obj = obj;
		this.page = page;
	}
	
	public Object getObject()
	{
		return this.obj;
	}
	
	public int getPage()
	{
		return this.page;
	}
}
