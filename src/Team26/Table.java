package Team26;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
	public Table(String name, Hashtable<String, String> format, String key, String dbName) throws IOException
	{
		this.tableName = name;
		this.tableFormat = format;
		tableFormat.put("TouchDate", "java.util.Date");
		this.key = key;
		this.filePath += dbName + "/" + this.tableName + "/";
		createPath();
		saveMetaData();
		this.numberOfPages = 1;
		createPage();
	}
	
	public void createPath()
	{
		File path = new File(this.filePath);
		path.mkdirs();
	}
	
	public void createPage() throws IOException
	{
		savePage(new Page(this.filePath + this.tableName + "1.class"));
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
	
	public void insert(Hashtable<String, Object> htblColNameValue) throws DBAppException, FileNotFoundException, IOException, ClassNotFoundException
	{
		Record insertion = createRecord(htblColNameValue);
		
		ArrayList<Page> pages = new ArrayList<Page>();
		for(int i = 1; i <= numberOfPages; i++)
		{
			String currentPagePath = filePath + this.tableName + i + ".class";
			File currentPageFile = new File(currentPagePath);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
			pages.add((Page) in.readObject());
			in.close();
		}
		int max = DBApp.getMaxRecordsInPage();
		// insert row into first page, checks if it exceeded the maximum and updates the other pages accordingly.
		for(int i = 0; i < pages.size(); i++)
		{
			Page current = pages.get(i);
			current.add(insertion);
			if(current.getSize() > max)
			{
				if(i == pages.size() - 1)
				{
					// create new page.
					Page newPage = new Page(filePath + tableName + (i+2) + ".class");
					newPage.add(current.getLast());
					this.numberOfPages++;
					savePage(newPage);
					savePage(current);
				}
				else
				{
					insertion = current.getLast();
					savePage(current);
				}
			}
			else
			{
				savePage(current);
				break;
			}
		}
	}
	
	public Record createRecord(Hashtable<String, Object> htblColNameValue) throws DBAppException
	{
		ArrayList<Object> recordData = new ArrayList<Object>();
		Enumeration<String> columnNames = (Enumeration<String>) tableFormat.keys();
		int keyIndex = 0;
		String keyType = null;
		
		int i = 0;
		while(columnNames.hasMoreElements()) // Checks if all columns in the insertion are found in the table.
		{
			String column = columnNames.nextElement();
			
			if(!htblColNameValue.containsKey(column) && !column.equals("TouchDate"))
			{
				throw new DBAppException("Error: your insertion does not match the desired table's format.");
			}
			
			recordData.add(htblColNameValue.get(column));
			
			if(column.equals(key))
			{
				keyIndex = i;
				keyType  = tableFormat.get(column);
			}
			i++;
		}
		
		Record insertion = new Record(recordData, keyType, keyIndex);
		return insertion;
	}
	
	public void savePage(Page p) throws IOException
	{
		File pageFile = new File(p.getPath());
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pageFile));
		out.writeObject(p);
		out.flush();
		out.close();
	}
}
