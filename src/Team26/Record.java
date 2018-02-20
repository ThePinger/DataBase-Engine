package Team26;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Record implements Serializable, Comparable<Record>
{
	private ArrayList<Object> recordData;
	private String keyType;
	private int keyIndex;
	
	public Record(ArrayList<Object> data, String keyType, int keyIndex)
	{
		this.recordData = data;
		this.recordData.add(new Date());
		this.keyType = keyType;
		this.keyIndex = keyIndex;
	}

	@Override
	public int compareTo(Record o) 
	{
		// returns a negative value if this record should be stored before record o
		// returns a positive value if record o should be stored before this record
		// returns 0 if both records do not have the same key type or their keys are identical
		
		if(this.keyType.equals("java.lang.Integer") && o.keyType.equals("java.lang.Integer"))
		{
			Integer thisKey = (Integer) this.recordData.get(this.keyIndex);
			Integer oKey = (Integer) o.recordData.get(o.keyIndex);
			
			return thisKey.compareTo(oKey);
		}
		if(this.keyType.equals("java.lang.Double") && o.keyType.equals("java.lang.Double"))
		{
			Double thisKey = (Double) this.recordData.get(this.keyIndex);
			Double oKey = (Double) o.recordData.get(o.keyIndex);
			
			return thisKey.compareTo(oKey);
		}
		if(this.keyType.equals("java.lang.String") && o.keyType.equals("java.lang.String"))
		{
			String thisKey = (String) this.recordData.get(this.keyIndex);
			String oKey = (String) o.recordData.get(o.keyIndex);
			
			return thisKey.compareTo(oKey);
		}
		
		return 0;
	}
	
	public Object getKey()
	{
		return recordData.get(this.keyIndex);
	}
}
