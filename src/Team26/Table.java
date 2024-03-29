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
import java.util.TreeSet;


public class Table implements Serializable 
{
	private int numberOfPages;
	private String key;
	private String tableName;
	private String filePath = "data/DataBases/";
	private Hashtable<String, String> tableFormat;
	private Hashtable<String, Integer> brinPages; 
	
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
		this.brinPages = new Hashtable<>();
	}
	
	public void createPath()
	{
		File path = new File(this.filePath);
		path.mkdirs();
	}
	
	public void createBRINPath(String colName)
	{
		File path = new File(this.filePath + colName);
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
	
	public String getKey()
	{
		return this.key;
	}
	
	public boolean hasColumn(String colName)
	{
		return this.tableFormat.containsKey(colName);
	}
	
	public boolean hasBRINIndex(String colName)
	{
		return this.brinPages.containsKey(colName);
	}
	
	public void createBRINOnPrimaryKey(String colName) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		this.brinPages.put(colName, 1);
		String path = this.filePath + colName + "/" + colName;
		int brinSize = DBApp.getBRINSize();
		
		ArrayList<Page> pages = new ArrayList<Page>();
		for(int i = 1; i <= numberOfPages; i++)
		{
			String currentPagePath = filePath + this.tableName + i + ".class";
			File currentPageFile = new File(currentPagePath);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
			pages.add((Page) in.readObject());
			in.close();
		}
		
		int curBRINPage = 1;
		ArrayList<BRINObject> brinObjects = new ArrayList<>();
		for(int i = 0; i < pages.size(); i++)
		{
			TreeSet<Record> tmp = pages.get(i).getRecords();
			brinObjects.add(new BRINObject(tmp.first().getKey(), tmp.last().getKey(), i + 1));
			saveBRINPage(brinObjects, path + curBRINPage + ".class");
			
			if(brinObjects.size() == brinSize && i != pages.size() - 1)
			{
				curBRINPage++;
				brinObjects = new ArrayList<>();
				this.brinPages.put(colName, curBRINPage);
			}
		}
		
		//TEST
		for(int i = 0; i < brinObjects.size(); i++)
			System.out.println(brinObjects.get(i).getMin() + " " + brinObjects.get(i).getMax());
	}
	
	
	public void insert(Hashtable<String, Object> htblColNameValue) throws DBAppException, FileNotFoundException, IOException, ClassNotFoundException
	{
		Record insertion = createRecord(htblColNameValue);
		
		if(this.brinPages.containsKey(this.key))
		{
			ArrayList<BRINObject>[] bpages = new ArrayList[this.brinPages.get(this.key) + 1];
			for(int i = 1; i < bpages.length; i++)
			{
				File curBRIN = new File(this.filePath + this.key + "/" + this.key + i + ".class");
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(curBRIN));
				bpages[i] = (ArrayList<BRINObject>) in.readObject();
				in.close();
			}
			
			int referencePage = -1;
			loop : for(int i = 1; i < bpages.length; i++)
			{
				for(int j = 0; j < bpages[i].size(); j++)
				{
					BRINObject tmp = bpages[i].get(j);
				    if(this.tableFormat.get(this.key).equals("java.lang.String"))
				    {
				    		String max = (String) tmp.getMax();
				    		String newIns = (String) insertion.getKey();
				    		if(newIns.compareTo(max) < 0)
				    		{
				    			referencePage = tmp.getReferencePage();
				    			break loop;
				    		}
				    		else if(newIns.compareTo(max) == 0)
				    			throw new DBAppException("Error : Entry does not have a unique key");
				    }
				    else if(this.tableFormat.get(this.key).equals("java.lang.Integer"))
				    {
				    		int max = (Integer) tmp.getMax();
				    		int newIns = (Integer) insertion.getKey();
				    		if(newIns < max)
				    		{
				    			referencePage = tmp.getReferencePage();
				    			break loop;
				    		}
				    		else if(newIns == max)
				    			throw new DBAppException("Error : Entry does not have a unique key");
				    }
				    else
				    {
				    		double max = (Double) tmp.getMax();
				    		double newIns = (double) insertion.getKey();
				    		if(newIns < max)
				    		{
				    			referencePage = tmp.getReferencePage();
				    			break loop;
				    		}
				    		else if(newIns == max)
				    			throw new DBAppException("Error : Entry does not have a unique key");
				    }
				}
			}
			
			if(referencePage == -1)
			{
				String currentPagePath = filePath + this.tableName + this.numberOfPages + ".class";
				File currentPageFile = new File(currentPagePath);
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
				Page p = (Page) in.readObject();
				in.close();
				
				if(p.getSize() == DBApp.getMaxRecordsInPage())
				{
					this.numberOfPages++;
					Page newPage = new Page(filePath + tableName + this.numberOfPages + ".class");
					newPage.add(insertion);
					savePage(newPage);
				}
				else
				{
					p.add(insertion);
					savePage(p);
				}
				
				createBRINOnPrimaryKey(this.key);
			}
			else
			{
				String currentPagePath = filePath + this.tableName + referencePage + ".class";
				File currentPageFile = new File(currentPagePath);
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
				Page p = (Page) in.readObject();
				in.close();
				TreeSet<Record> rec = p.getRecords();
				while(!rec.isEmpty())
					if(rec.pollFirst().getKey().equals(insertion.getKey()))
						throw new DBAppException("Error : Entry does not have a unique key");
				
				p.add(insertion);
				if(p.getSize() > DBApp.getMaxRecordsInPage())
					insertion = p.removeLast();
				else insertion = null;
				savePage(p);
				
				for(int i = referencePage + 1; i <= this.numberOfPages && insertion != null; i++)
				{
					currentPagePath = filePath + this.tableName + i + ".class";
					currentPageFile = new File(currentPagePath);
					in = new ObjectInputStream(new FileInputStream(currentPageFile));
					p = (Page) in.readObject();
					in.close();
					p.add(insertion);
					if(p.getSize() > DBApp.getMaxRecordsInPage())
						insertion = p.removeLast();
					else insertion = null;
					savePage(p);
				}
				
				if(insertion != null)
				{
					this.numberOfPages++;
					Page newPage = new Page(filePath + tableName + this.numberOfPages + ".class");
					newPage.add(insertion);
					savePage(newPage);
				}
				
				createBRINOnPrimaryKey(this.key);
			}
		}
		else
		{
			
			ArrayList<Page> pages = new ArrayList<Page>();
			for(int i = 1; i <= numberOfPages; i++)
			{
				String currentPagePath = filePath + this.tableName + i + ".class";
				File currentPageFile = new File(currentPagePath);
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
				pages.add((Page) in.readObject());
				in.close();
			}
			
			if(!uniqueKey(insertion, pages))
				throw new DBAppException("Error : Entry does not have a unique key");
			
			int max = DBApp.getMaxRecordsInPage();
			int brinSize = DBApp.getBRINSize();
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
						newPage.add(current.removeLast());
						this.numberOfPages++;
						savePage(newPage);
						savePage(current);
					}
					else
					{
						insertion = current.removeLast();
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
	}
	
	public void delete(Hashtable<String, Object> htblColNameValue) throws DBAppException, FileNotFoundException, IOException, ClassNotFoundException
	{
		//Check Columns Available
		Enumeration<String> columnNames = (Enumeration<String>) htblColNameValue.keys();
		while(columnNames.hasMoreElements())
			if(!this.tableFormat.containsKey(columnNames.nextElement()))
				throw new DBAppException("Error: Column name does not exist");
		
		if(this.brinPages.containsKey(this.key))
		{
			ArrayList<BRINObject>[] bpages = new ArrayList[this.brinPages.get(this.key) + 1];
			for(int i = 1; i < bpages.length; i++)
			{
				File curBRIN = new File(this.filePath + this.key + "/" + this.key + i + ".class");
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(curBRIN));
				bpages[i] = (ArrayList<BRINObject>) in.readObject();
				in.close();
			}
			
			int referencePage = -1;
			loop : for(int i = 1; i < bpages.length; i++)
			{
				for(int j = 0; j < bpages[i].size(); j++)
				{
					BRINObject tmp = bpages[i].get(j);
				    if(this.tableFormat.get(this.key).equals("java.lang.String"))
				    {
				    		String max = (String) tmp.getMax();
				    		String newIns = (String) htblColNameValue.get(this.key);
				    		if(newIns.compareTo(max) <= 0)
				    		{
				    			referencePage = tmp.getReferencePage();
				    			break loop;
				    		}
				    }
				    else if(this.tableFormat.get(this.key).equals("java.lang.Integer"))
				    {
				    		int max = (Integer) tmp.getMax();
				    		int newIns = (Integer) htblColNameValue.get(this.key);
				    		if(newIns <= max)
				    		{
				    			referencePage = tmp.getReferencePage();
				    			break loop;
				    		}
				    }
				    else
				    {
				    		double max = (Double) tmp.getMax();
				    		double newIns = (double) htblColNameValue.get(this.key);
				    		if(newIns <= max)
				    		{
				    			referencePage = tmp.getReferencePage();
				    			break loop;
				    		}
				    }
				}
			}
			
			if(referencePage == -1)
				throw new DBAppException("Error : No Column with that ID ");
			else
			{
				boolean recordRemoved = false;
				Object recordKey = htblColNameValue.get(this.key);
				
				String currentPagePath = filePath + this.tableName + referencePage + ".class";
				File currentPageFile = new File(currentPagePath);
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
				Page p = (Page) in.readObject();
				
				TreeSet<Record> tmp = p.getRecords();
				while(!tmp.isEmpty())
					if(tmp.first().getKey().equals(recordKey))
					{
						p.remove(tmp.first());
						recordRemoved = true;
						savePage(p);
						break;
					}
					else tmp.pollFirst();
				
				if(recordRemoved)
				{
					for(int i = referencePage + 1; i <= this.numberOfPages; i++)
					{
						String prePagePath = this.filePath + this.tableName + (i - 1) + ".class";
						File prePageFile = new File(prePagePath);
						in = new ObjectInputStream(new FileInputStream(prePageFile));
						Page pre = (Page) in.readObject();
						currentPagePath = filePath + this.tableName + i + ".class";
						currentPageFile = new File(currentPagePath);
						in = new ObjectInputStream(new FileInputStream(currentPageFile));
						p = (Page) in.readObject();
						pre.add(p.removeFirst());
						savePage(pre);
						savePage(p);
					}
					
					if(p.getSize() == 0)
					{
						new File(p.getPath()).delete();
						this.numberOfPages--;
					}
					
					createBRINOnPrimaryKey(this.key);
				}
				else 
					throw new DBAppException("Error : No Column wit that ID");
			}
			
		}
		else
		{
			//Load All Pages
			ArrayList<Page> pages = new ArrayList<Page>();
			for(int i = 1; i <= numberOfPages; i++)
			{
				String currentPagePath = filePath + this.tableName + i + ".class";
				File currentPageFile = new File(currentPagePath);
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
				pages.add((Page) in.readObject());
				in.close();
			}
			
			Object recordKey = htblColNameValue.get(this.key);
			boolean recordRemoved = false;
			for(int i = 0; i < pages.size(); i++)
			{
				if(recordRemoved)
				{
					pages.get(i - 1).add(pages.get(i).removeFirst());
					savePage(pages.get(i - 1));
				}
				
				TreeSet<Record> tmp = pages.get(i).getRecords();
				while(!tmp.isEmpty() && !recordRemoved)
				{
					Record r = tmp.pollFirst();
					if(r.getKey().equals(recordKey))
					{
						pages.get(i).remove(r);
						recordRemoved = true;
					}
				}
			}
			
			if(recordRemoved) 
			{
				if(pages.get(pages.size() - 1).getSize() == 0)
				{
					File f = new File(pages.get(pages.size() - 1).getPath());
					f.delete();
					this.numberOfPages--;
				}
				else savePage(pages.get(pages.size() - 1));
			}
			
			if(!recordRemoved)
				throw new DBAppException("Error: No Column with that ID");
		}	
	}
	
	public void update(String strKey, Hashtable<String, Object> htblColNameValue) throws FileNotFoundException, IOException, ClassNotFoundException, DBAppException
	{
		boolean keyWillChange = false;
		if(htblColNameValue.containsKey(this.key))
			keyWillChange = true;
		
		//Load All Pages
		ArrayList<Page> pages = new ArrayList<Page>();
		for(int i = 1; i <= numberOfPages; i++)
		{
			String currentPagePath = filePath + this.tableName + i + ".class";
			File currentPageFile = new File(currentPagePath);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
			pages.add((Page) in.readObject());
			in.close();
		}
		
		Object newKey = htblColNameValue.get(this.key);
		for(int i = 0; i < pages.size() && keyWillChange; i++)
		{
			TreeSet<Record> records = pages.get(i).getRecords();
			while(!records.isEmpty())
				if(records.pollFirst().getKey().equals(newKey))
					throw new DBAppException("Error: Primary Key Exists");
		}
		
		Hashtable<String, Object> newHashT = new Hashtable<>();
		Enumeration<String> colNames = htblColNameValue.keys();
		Enumeration<Object> values   = htblColNameValue.elements();
		for(int i = 0; i < pages.size(); i++)
		{
			TreeSet<Record> records = pages.get(i).getRecords();
			while(!records.isEmpty())
				if((records.first().getKey() + "").equals(strKey))
				{
					newHashT = records.first().getValues();
					while(colNames.hasMoreElements())
						newHashT.put(colNames.nextElement(), values.nextElement());
					
					pages.get(i).remove(records.first());
					for(int j = i + 1; j < pages.size(); j++)
					{
						pages.get(j - 1).add(pages.get(j).removeFirst());
						savePage(pages.get(j - 1));
					}
					
					if(pages.get(pages.size() - 1).getSize() == 0)
					{
						File f = new File(pages.get(pages.size() - 1).getPath());
						f.delete();
						this.numberOfPages--;
					}
					else savePage(pages.get(pages.size() - 1));
					
					insert(newHashT);
					return;
				}
				else records.pollFirst();
		}
	}
	
	public Iterator<String> select(String strColumnName, Object[] objarrValues, String[] strarrOperators) throws FileNotFoundException, ClassNotFoundException, IOException
	{
		ArrayList<String> results = new ArrayList<String>();
		
		if(this.hasBRINIndex(strColumnName))
		{
			if(strColumnName.equals(this.key))
				results = selectUsingPrimaryIndex(strColumnName, objarrValues, strarrOperators);
			
			else
			{
				// TODO selectUsingSecondaryIndex()
			}
		}
		
		else
			results = selectWithoutIndex(strColumnName, objarrValues, strarrOperators);
		
		return results.iterator();
	}
	
	public ArrayList<String> selectWithoutIndex(String strColumnName, Object[] objarrValues, String[] strarrOperators) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ArrayList<String> results = new ArrayList<String>();
		
		// Load All Pages
		ArrayList<Page> pages = new ArrayList<Page>();
		for(int i = 1; i <= numberOfPages; i++)
		{
			String currentPagePath = filePath + this.tableName + i + ".class";
			File currentPageFile = new File(currentPagePath);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
			pages.add((Page) in.readObject());
			in.close();
		}
		
		// add records that satisfy given query to results
		for(int i = 0; i < pages.size(); i++)
		{
			Page currentPage = pages.get(i);
			int size = currentPage.getSize();
			
			for(int j = 0; j < size; j++)
			{
				Record currentRecord = currentPage.removeFirst();
				
				if(satisfiesQuery(currentRecord, objarrValues, strarrOperators, strColumnName))
					results.add(currentRecord.toString());
			}
		}
		
		return results;
	}
	
	public ArrayList<String> selectUsingPrimaryIndex(String strColumnName, Object[] objarrValues, String[] strarrOperators) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ArrayList<String> results = new ArrayList<String>();
		
		// load the BRIN for the specified column
		ArrayList<ArrayList<BRINObject>> index = new ArrayList<ArrayList<BRINObject>>();
		String indexPath = this.filePath + strColumnName + "/";
		int BRINPages = this.brinPages.get(strColumnName);
		
		for(int i = 1; i <= BRINPages; i++)
		{
			String currentPagePath = indexPath + strColumnName + i + ".class";
			File currentPageFile = new File(currentPagePath);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
			index.add((ArrayList<BRINObject>) in.readObject());
			in.close();
		}
		
		// get references to all pages that should be loaded
		ArrayList<Integer> pageReferences = new ArrayList<Integer>();
		
		for(int i = 0; i < index.size(); i++)
		{
			ArrayList<BRINObject> currentBRINPage = index.get(i);
			
			for(int j = 0; j < currentBRINPage.size(); j++)
			{
				BRINObject currentBRINObject = currentBRINPage.get(j);
				
				if(isInRange(currentBRINObject, objarrValues, strarrOperators, strColumnName))
					pageReferences.add(currentBRINObject.getReferencePage());
			}
		}
		
		// load needed pages
		ArrayList<Page> pages = new ArrayList<Page>();
		
		for(int i = 0; i < pageReferences.size(); i++)
		{
			int pageNumber = pageReferences.get(i);
			String currentPagePath = filePath + this.tableName + pageNumber + ".class";
			File currentPageFile = new File(currentPagePath);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
			pages.add((Page) in.readObject());
			in.close();
		}
		
		// add records that satisfy given query to results
		for(int i = 0; i < pages.size(); i++)
		{
			Page currentPage = pages.get(i);
			int size = currentPage.getSize();
			
			for(int j = 0; j < size; j++)
			{
				Record currentRecord = currentPage.removeFirst();
				
				if(satisfiesQuery(currentRecord, objarrValues, strarrOperators, strColumnName))
					results.add(currentRecord.toString());
			}
		}
		
		return results;
		
	}
	
	public boolean satisfiesQuery(Record record, Object[] objarrValues, String[] strarrOperators, String columnName)
	{
		
		// returns true if the given record satisfies query
		
			String type = this.tableFormat.get(columnName);
			Hashtable<String, Object> data = record.getValues();
			boolean upperBound = false, lowerBound = false;
			
			if(type.equals("java.lang.String"))
			{
				String userMin = (String) objarrValues[0];
				String userMax = (String) objarrValues[1];
				String recordValue = (String) data.get(columnName);
				
				if( (strarrOperators[0].equals(">=") && recordValue.compareTo(userMin) >= 0) ||
					(strarrOperators[0].equals(">")  && recordValue.compareTo(userMin) >  0) )
						lowerBound = true;
				
				if( (strarrOperators[1].equals("<=") && recordValue.compareTo(userMax) <= 0) ||
					(strarrOperators[1].equals("<")  && recordValue.compareTo(userMax) <  0) )
						upperBound = true;
			}
			
			if(type.equals("java.lang.Integer"))
			{
				Integer userMin = (Integer) objarrValues[0];
				Integer userMax = (Integer) objarrValues[1];
				Integer recordValue = (Integer) data.get(columnName);
				
				if( (strarrOperators[0].equals(">=") && recordValue.compareTo(userMin) >= 0) ||
					(strarrOperators[0].equals(">")  && recordValue.compareTo(userMin) >  0) )
						lowerBound = true;
					
				if( (strarrOperators[1].equals("<=") && recordValue.compareTo(userMax) <= 0) ||
					(strarrOperators[1].equals("<")  && recordValue.compareTo(userMax) <  0) )
						upperBound = true;
			}
				
			if(type.equals("java.lang.Double"))
			{
				Double userMin = (Double) objarrValues[0];
				Double userMax = (Double) objarrValues[1];
				Double recordValue = (Double) data.get(columnName);
				
				if( (strarrOperators[0].equals(">=") && recordValue.compareTo(userMin) >= 0) ||
					(strarrOperators[0].equals(">")  && recordValue.compareTo(userMin) >  0) )
						lowerBound = true;
					
				if( (strarrOperators[1].equals("<=") && recordValue.compareTo(userMax) <= 0) ||
					(strarrOperators[1].equals("<")  && recordValue.compareTo(userMax) <  0) )
						upperBound = true;
			}
				
			if(upperBound && lowerBound)
				return true;
				
			return false;
	}
	
	public boolean isInRange(BRINObject brin, Object[] objarrValues, String[] strarrOperators, String columnName)
	{
		
		// returns true if the given query is in the BRINObject's range, returns false otherwise
		
		String type = this.tableFormat.get(columnName);
		
		if(type.equals("java.lang.String"))
		{
			String userMin = (String) objarrValues[0];
			String userMax = (String) objarrValues[1];
			String brinMin = (String) brin.getMin();
			String brinMax = (String) brin.getMax();
			
			if(userMin.compareTo(brinMax) > 0 || userMax.compareTo(brinMin) < 0)
				return false;
			
			return true;
		}
		
		if(type.equals("java.lang.Integer"))
		{
			Integer userMin = (Integer) objarrValues[0];
			Integer userMax = (Integer) objarrValues[1];
			Integer brinMin = (Integer) brin.getMin();
			Integer brinMax = (Integer) brin.getMax();
			
			if(userMin.compareTo(brinMax) > 0 || userMax.compareTo(brinMin) < 0)
				return false;
			
			return true;
		}
		
		if(type.equals("java.lang.Double"))
		{
			Double userMin = (Double) objarrValues[0];
			Double userMax = (Double) objarrValues[1];
			Double brinMin = (Double) brin.getMin();
			Double brinMax = (Double) brin.getMax();
			
			if(userMin.compareTo(brinMax) > 0 || userMax.compareTo(brinMin) < 0)
				return false;
			
			return true;
		}
		
		return false;
	}
	
	public boolean uniqueKey(Record r, ArrayList<Page> p)
	{
		for(int i = 0; i < p.size(); i++)
		{
			TreeSet<Record> tmp = p.get(i).getRecords();
			while(!tmp.isEmpty())
				if(tmp.pollFirst().getKey().equals(r.getKey()))
					return false;
		}
		return true;
	}
	
	public Record createRecord(Hashtable<String, Object> htblColNameValue) throws DBAppException
	{
		Enumeration<String> columnNames = (Enumeration<String>) tableFormat.keys();
		String keyCol = this.key;
		String keyType = this.tableFormat.get(keyCol);
		
		while(columnNames.hasMoreElements()) 
		{
			String column = columnNames.nextElement();
			
			// Checks if all columns in the insertion are found in the table.
			if(!htblColNameValue.containsKey(column) && !column.equals("TouchDate"))
			{
				throw new DBAppException("Error: your insertion does not match the desired table's format.");
			}
			
			// Checks if all fields in the insertion are of the correct type.
			if(!column.equals("TouchDate"))
			{
				Object x = htblColNameValue.get(column);
				String insertionClass = ((x.getClass() + "")).split(" ")[1];
				String tableClass = this.tableFormat.get(column);
				
				if(!tableClass.equals(insertionClass))
				{
					throw new DBAppException("Error: One of the fields in your insertion does not have the right type.");
				}
			}
		}
		
		Record insertion = new Record(htblColNameValue, keyType, keyCol);
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
	
	public void saveBRINPage(ArrayList<BRINObject> obj, String path) throws FileNotFoundException, IOException
	{
		File brinPageFile = new File(path);
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(brinPageFile));
		out.writeObject(obj);
		out.flush();
		out.close();
	}
	
	public void print() throws ClassNotFoundException, IOException
	{
		for(int i = 1; i <= numberOfPages; i++)
		{
			String currentPagePath = filePath + this.tableName + i + ".class";
			File currentPageFile = new File(currentPagePath);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentPageFile));
			System.out.println(in.readObject().toString());
			in.close();
		}
	}
}
