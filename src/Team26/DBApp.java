package Team26;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeSet;

public class DBApp implements Serializable
{
	private String curDB;
	private String curDBFilePath;
	private String dataBasesDataFilePath = "data/DataBases/";
	private TreeSet<String> dataBases;
	
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
			this.dataBases = (TreeSet<String>) in.readObject();
		}
		else
		{
			this.dataBases = new TreeSet<String>();
			save();
		}
	}
	
	public void save() throws FileNotFoundException, IOException
	{
		File dataBasesInfo = new File(this.dataBasesDataFilePath);
		if(!dataBasesInfo.exists()) dataBasesInfo.mkdirs();
		dataBasesInfo = new File(this.dataBasesDataFilePath + "DataBases.class");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataBasesInfo));
		out.writeObject(this.dataBases);
	}
}
