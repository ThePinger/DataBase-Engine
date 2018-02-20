package Team26;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;

public class DBApp implements Serializable
{
	private static int maxRecordsInPage = 0; // will be read from config file upon initialization.
	private String curDB;
	private String curDBFilePath;
	private String dataBasesDataFilePath = "data/DataBases/";
	private HashSet<String> dataBases;
	private HashMap<String, Table>  dataBaseTables;
	private Properties dataBaseProperties;
	
	public DBApp() throws FileNotFoundException, IOException, ClassNotFoundException
	{
		loadDB();	
	}
	
	@SuppressWarnings("unchecked")
	public void loadDB() throws FileNotFoundException, IOException, ClassNotFoundException
	{
		File savedDataBases = new File(this.dataBasesDataFilePath + "DataBases.class");
		if(savedDataBases.exists())
		{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(savedDataBases));
			this.dataBases = (HashSet<String>) in.readObject();
			in.close();
		}
		else
		{
			this.dataBases = new HashSet<String>();
			saveDB();
		}
	}
	
	//Saves HashSet of DB's
	public void saveDB() throws FileNotFoundException, IOException
	{
		File dataBasesInfo = new File(this.dataBasesDataFilePath);
		if(!dataBasesInfo.exists()) dataBasesInfo.mkdirs();
		dataBasesInfo = new File(this.dataBasesDataFilePath + "DataBases.class");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataBasesInfo));
		out.writeObject(this.dataBases);
		out.flush();
		out.close();
	}
	
	public void init(String dataBase) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		readConfigFile();
		this.curDB = dataBase;
		this.curDBFilePath = "data/DataBases/" + this.curDB + "/";
		if(this.dataBases.contains(dataBase)) loadTables();
		else
		{
			//Creates DB directory
			//Creates a HashMap of tables and saves it
			this.dataBases.add(dataBase);
			saveDB();
			this.dataBaseTables = new HashMap<>();
			saveTables();
		}
		
	}
	
	//Loads The Tables in Current DB
	@SuppressWarnings("unchecked")
	public void loadTables() throws FileNotFoundException, IOException, ClassNotFoundException
	{
		File tablesFile = new File(this.curDBFilePath + "Tables.class");
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(tablesFile));
		this.dataBaseTables = (HashMap<String, Table>) in.readObject();
		in.close();
	}
	
	//Saves Tables in DB .class File
	public void saveTables() throws FileNotFoundException, IOException
	{
		File dataBaseDir = new File(this.curDBFilePath);
		if(!dataBaseDir.exists()) dataBaseDir.mkdirs();
		dataBaseDir = new File(this.curDBFilePath + "Tables.class");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataBaseDir));
		out.writeObject(this.dataBaseTables);
		out.flush();
		out.close();
	}
	
	public void createTable(String strTableName, String strClusteringKeyColumn, Hashtable<String,String> htblColNameType ) throws DBAppException, FileNotFoundException, IOException
	{
		if(this.dataBaseTables.containsKey(strTableName))
		{
			System.out.println("You Have a Table with the Same name");
			return;
		}
		
		this.dataBaseTables.put(strTableName, new Table(strTableName, htblColNameType, strClusteringKeyColumn, this.curDB));
		saveTables();
	}
	
	public void insertIntoTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException, FileNotFoundException, ClassNotFoundException, IOException
	{
		if(! dataBaseTables.containsKey(strTableName))
		{
			throw new DBAppException("Error: The table you are trying to insert into does not exist.");
		}
		
		Table targetTable = dataBaseTables.get(strTableName);
		targetTable.insert(htblColNameValue);
	}
	
	public void readConfigFile() throws IOException
	{
		FileInputStream in = new FileInputStream("config/DBApp.config");
		dataBaseProperties = new Properties();
		dataBaseProperties.load(in);
		in.close();
		maxRecordsInPage = Integer.parseInt((String)dataBaseProperties.get("MaximumRowsCountinPage"));
	}
	
	public static int getMaxRecordsInPage()
	{
		return maxRecordsInPage;
	}
}
