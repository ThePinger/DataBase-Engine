package Team26;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

public class DBAppTest 
{
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, DBAppException
	{
		DBApp app = new DBApp();
		String strTableName = "Student";
		Hashtable htblColNameType = new Hashtable( ); 
		htblColNameType.put("id", "java.lang.Integer"); 
		htblColNameType.put("name", "java.lang.String"); 
		htblColNameType.put("gpa", "java.lang.Double");
		app.init("Test1");
		app.createTable( strTableName, "id", htblColNameType );
		Hashtable<String, Object> htblColNameValue = new Hashtable<>( ); 
		htblColNameValue.put("id", new Integer( 3 )); 
		htblColNameValue.put("name", new String("Ahmed Noor" ) ); 
		htblColNameValue.put("gpa", new Double( 0.95 ) ); 
		app.insertIntoTable( strTableName , htblColNameValue );
		htblColNameValue.clear( );
		htblColNameValue.put("id", new Integer( 5 )); 
		htblColNameValue.put("name", new String("Mo Noor" ) ); 
		htblColNameValue.put("gpa", new Double( 0.95 ) ); 
		app.insertIntoTable( strTableName , htblColNameValue );
		htblColNameValue.clear( );
		htblColNameValue.put("id", new Integer( 1 )); 
		htblColNameValue.put("name", new String("Dalia Noor" ) ); 
		htblColNameValue.put("gpa", new Double( 1.25 ) ); 
		app.insertIntoTable( strTableName , htblColNameValue );
		htblColNameValue.clear( ); 
		htblColNameValue.put("id", new Integer( 4 )); 
		htblColNameValue.put("name", new String("John Noor"));
		htblColNameValue.put("gpa", new Double( 1.5 ) ); 
		app.insertIntoTable( strTableName , htblColNameValue );
		htblColNameValue.clear( ); 
		htblColNameValue.put("id", new Integer( 2 )); 
		htblColNameValue.put("name", new String("Zaky Noor")); 
		htblColNameValue.put("gpa", new Double( 0.88 ) ); 
		app.insertIntoTable( strTableName , htblColNameValue );
		htblColNameValue.clear( ); 
//		Hashtable<String, Object> htblColNameValue = new Hashtable<>( ); 
		htblColNameValue.put("id", new Integer( 8 )); 
//		htblColNameValue.put("name", new String("Mo Noor" ) ); 
//		htblColNameValue.put("gpa", new Double( 0.95 ) ); 
		app.updateTable(strTableName, "1", htblColNameValue);
		htblColNameValue.clear( ); 
		htblColNameValue.put("id", new Integer( 2 )); 
		htblColNameValue.put("name", new String("Zaky Noor")); 
		htblColNameValue.put("gpa", new Double( 0.88 ) ); 
		app.deleteFromTable( strTableName , htblColNameValue );
		htblColNameValue.clear( ); 
		htblColNameValue.put("id", new Integer( 5 )); 
		htblColNameValue.put("name", new String("Mo Noor" ) ); 
		htblColNameValue.put("gpa", new Double( 0.95 ) ); 
		app.deleteFromTable( strTableName , htblColNameValue );
		
		app.createBRINIndex(strTableName, "id");

		htblColNameValue.clear( ); 
		htblColNameValue.put("id", new Integer( 2 )); 
		htblColNameValue.put("name", new String("Zaky Noor")); 
		htblColNameValue.put("gpa", new Double( 0.88 ) ); 
		app.insertIntoTable( strTableName , htblColNameValue );
		
		htblColNameValue.clear( ); 
		htblColNameValue.put("id", new Integer( 2 )); 
		htblColNameValue.put("name", new String("Zaky Noor")); 
		htblColNameValue.put("gpa", new Double( 0.88 ) ); 
		app.deleteFromTable( strTableName , htblColNameValue );
		
		htblColNameValue.clear( );
		htblColNameValue.put("id", new Integer( 8 )); 
		htblColNameValue.put("name", new String("Dalia Noor" ) ); 
		htblColNameValue.put("gpa", new Double( 1.25 ) ); 
		app.deleteFromTable( strTableName , htblColNameValue );
		
		app.print("Student");
		
		Object[] values = {3,4};
		String[] operators = {">=", "<="};
		Iterator<String> i = app.selectFromTable(strTableName, "id", values, operators);
		System.out.println("(3 >= id <= 4):");
		while(i.hasNext())
			System.out.println(i.next());
		
		System.out.println();
		operators[0] = ">";
		operators[1] = "<=";
		values[0] = 3;
		values[1] = 4;
		i = app.selectFromTable(strTableName, "id", values, operators);
		System.out.println("(3 > id <= 4):");
		while(i.hasNext())
			System.out.println(i.next());
		
		System.out.println();
		operators[0] = ">=";
		operators[1] = "<";
		values[0] = 3;
		values[1] = 4;
		i = app.selectFromTable(strTableName, "id", values, operators);
		System.out.println("(3 >= id < 4):");
		while(i.hasNext())
			System.out.println(i.next());
		
		System.out.println();
		values[0] = 4;
		values[1] = 9;
		operators[0] = ">";
		operators[1] = "<";
		i = app.selectFromTable(strTableName, "id", values, operators);
		System.out.println("(4 > id < 9):");
		while(i.hasNext())
			System.out.println(i.next());
		
		System.out.println();
		values[0] = "Ahmed Noor";
		values[1] = "John Noor";
		operators[0] = ">=";
		operators[1] = "<=";
		i = app.selectFromTable(strTableName, "name", values, operators);
		System.out.println("(Ahmed Noor >= name <= John Noor):");
		while(i.hasNext())
			System.out.println(i.next());
		
		System.out.println();
		values[0] = "Ahmed Noor";
		values[1] = "Dalia Noor";
		operators[0] = ">=";
		operators[1] = "<=";
		i = app.selectFromTable(strTableName, "name", values, operators);
		System.out.println("(Ahmed Noor >= name <= Dalia Noor):");
		while(i.hasNext())
			System.out.println(i.next());
		
		System.out.println();
		values[0] = 0.8;
		values[1] = 1.25;
		operators[0] = ">=";
		operators[1] = "<=";
		i = app.selectFromTable(strTableName, "gpa", values, operators);
		System.out.println("(0.8 >= gpa <= 1.25):");
		while(i.hasNext())
			System.out.println(i.next());
	}
}
