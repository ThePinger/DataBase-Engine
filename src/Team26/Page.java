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
	
	public void add(Record r)
	{
		records.add(r);
		this.numberOfRecords++;
	}
	
	public void remove(Record r)
	{
		this.numberOfRecords--;
		this.records.remove(r);
	}
	
	public Record removeFirst()
	{
		this.numberOfRecords--;
		return records.pollFirst();
	}
	
	public Record removeLast()
	{
		this.numberOfRecords--;
		return records.pollLast();
	}

	public int getSize()
	{
		return this.numberOfRecords;
	}
	
	public String getPath()
	{
		return pagePath;
	}
	
	public TreeSet<Record> getRecords()
	{
		return (TreeSet<Record>) records.clone();
	}
	
	public String toString()
	{
		String s = "";
		for(Record r : this.records)
			s += r.toString() + "\n";
		return s;
	}
}
