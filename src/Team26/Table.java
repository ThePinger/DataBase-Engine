package Team26;

import java.io.File;
import java.io.Serializable;
import java.util.Hashtable;

public class Table implements Serializable 
{
	private int numberOfPages;
	private String key;
	private String tableName;
	private String filePath = "data/DataBases/";
	private Hashtable<String, String> tableFormat;
	
	public Table(String name, Hashtable<String, String> format, String key, String dbName)
	{
		this.tableName = name;
		this.tableFormat = format;
		tableFormat.put("TouchDate", "java.util.Date");
		this.key = key;
		this.filePath += dbName + "/" + this.tableName + "/";
		createPath();
		this.numberOfPages = 1;
	}
	
	public void createPath()
	{
		File path = new File(this.filePath);
		path.mkdirs();
	}
}
