package Team26;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;


public class Table implements Serializable 
{
	private int numberOfPages;
	private String key;
	private String tableName;
	private String filePath = "data/DataBases/";
	private Hashtable<String, String> tableFormat;
	
	public Table(String name, Hashtable<String, String> format, String key, String dbName) throws FileNotFoundException
	{
		this.tableName = name;
		this.tableFormat = format;
		tableFormat.put("TouchDate", "java.util.Date");
		this.key = key;
		this.filePath += dbName + "/" + this.tableName + "/";
		createPath();
		saveMetaData();
		this.numberOfPages = 1;
	}
	
	public void createPath()
	{
		File path = new File(this.filePath);
		path.mkdirs();
	}
	
	public void saveMetaData() throws FileNotFoundException
	{
		File path = new File(this.filePath + "metadata.csv");
		PrintWriter pw = new PrintWriter(path);
		Enumeration<String> colName = this.tableFormat.keys();
		Iterator<String> colType =  this.tableFormat.values().iterator();
		while(colType.hasNext())
		{
			String colname = colName.nextElement();
			if(colname.equals(this.key))
				pw.println(this.tableName + "," + colname + "," + colType.next() + "," + true + "," + false);
			else
				pw.println(this.tableName + "," + colname + "," + colType.next() + "," + false + "," + false);
		}
		
		pw.flush();
		pw.close();
	}
}
