package Team26;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

public class Record implements Serializable, Comparable<Record>
{
	private Hashtable<String,Object> recordData;
	private String keyType;
	private String keyCol;
	
	public Record(Hashtable<String,Object> data, String keyType, String keyCol)
	{
		this.recordData = data;
		this.recordData.put("TouchDate", new Date());
		this.keyType = keyType;
		this.keyCol = keyCol;
	}

	@Override
	public int compareTo(Record o) 
	{
		// returns a negative value if this record should be stored before record o
		// returns a positive value if record o should be stored before this record
		// returns 0 if both records do not have the same key type or their keys are identical
		
		if(this.keyType.equals("java.lang.Integer") && o.keyType.equals("java.lang.Integer"))
		{
			Integer thisKey = (Integer) this.recordData.get(this.keyCol);
			Integer oKey = (Integer) o.recordData.get(o.keyCol);
			
			return thisKey.compareTo(oKey);
		}
		if(this.keyType.equals("java.lang.Double") && o.keyType.equals("java.lang.Double"))
		{
			Double thisKey = (Double) this.recordData.get(this.keyCol);
			Double oKey = (Double) o.recordData.get(o.keyCol);
			
			return thisKey.compareTo(oKey);
		}
		if(this.keyType.equals("java.lang.String") && o.keyType.equals("java.lang.String"))
		{
			String thisKey = (String) this.recordData.get(this.keyCol);
			String oKey = (String) o.recordData.get(o.keyCol);
			
			return thisKey.compareTo(oKey);
		}
		
		return 0;
	}
	
	public Object getKey()
	{
		return recordData.get(this.keyCol);
	}
	
	public Hashtable<String, Object> getValues()
	{
		return (Hashtable<String, Object>) this.recordData.clone();
	}
	
	public String toString()
	{
		String s = "";
		Enumeration<Object> values = this.recordData.elements();
		while(values.hasMoreElements())
			s += values.nextElement() + " ";
		return s;
	}
}
