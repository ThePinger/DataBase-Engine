package Team26;

import java.io.Serializable;
import java.util.TreeSet;

public class Page implements Serializable 
{
	private TreeSet<Record> records;
	private int numberOfRecords;
	private String pagePath;
	
	public Page(String path)
	{
		this.records = new TreeSet<Record>();
		this.pagePath = path;
	}
}
