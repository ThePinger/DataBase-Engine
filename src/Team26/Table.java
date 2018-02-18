package Team26;

import java.io.Serializable;
import java.util.Hashtable;

public class Table implements Serializable 
{
	private String tableName;
	private Hashtable<String, String> tableFormat;
	private String key;
	private int numberOfPages;
	
	public Table(String name, Hashtable<String, String> format, String key)
	{
		this.tableName = name;
		this.tableFormat = format;
		tableFormat.put("TouchDate", "java.util.Date");
		this.key = key;
	}
}
