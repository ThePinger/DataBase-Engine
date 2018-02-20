package Team26;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeSet;

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
	
	public Record createRecord(Hashtable<String, Object> htblColNameValue) throws DBAppException
	{
		ArrayList<Object> recordData = new ArrayList<Object>();
		Enumeration<String> columnNames = (Enumeration<String>) tableFormat.keys();
		int keyIndex = 0;
		String keyType = null;
		
		while(columnNames.hasMoreElements()) // Checks if all columns in the insertion are found in the table.
		{
			String column = columnNames.nextElement();
			
			if(!htblColNameValue.containsKey(column))
			{
				throw new DBAppException("Error: your insertion does not match the desired table's format.");
			}
			
			recordData.add(htblColNameValue.get(column));
			
			if(column.equals(key))
			{
				keyIndex = recordData.size() - 1;
				keyType  = tableFormat.get(column);
			}
		}
		
		Record insertion = new Record(recordData, keyType, keyIndex);
		return insertion;
	}
	
}
