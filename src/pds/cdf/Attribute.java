package pds.cdf;

import java.util.ArrayList;

/**
 * Generic attribute information.
 * 
 * @author tking
 *
 */
public class Attribute {

	String mName = null;
	int		mDataType = 0;
	long	mStartByte = 0L;
	ArrayList<String> mValues = new ArrayList<String>();
	
	/**
	 * A container of information about an attribute. 
	 */
	public Attribute() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Display a description of each attribute.
	 */
	public void dump() {
		System.out.println("=================================");
		System.out.println("      Attribute: " + mName);
		System.out.println("=================================");
		System.out.println("DataType: " + Constant.getDataTypeName(mDataType));
		System.out.println("StartByte: " + mStartByte);
		System.out.println("values: ");
		for(String v : mValues) {
			System.out.println("   " + v);
		}
	}
	
	/**
	 * Set the name of the attribute.
	 * 
	 * @param name the Name of the attribute
	 */
	public void setName(String name) { mName = name; }
	/**
	 * Retrieve the current name of the attribute.
	 * 
	 * @return the name of the attribute
	 */
	public String getName() { return mName; }
	
	/**
	 * Set the data type of the attribute.
	 * 
	 * @param dataType the dataType of the attribute.
	 */
	public void setDataType(int dataType) { mDataType = dataType; }
	/**
	 * Retrieve the current data type of the attribute.
	 * 
	 * @return the current data type of the attribute.
	 */
	public int getDataType() { return mDataType; }
	
	/**
	 * Set the start byte of the data associated with the attribute.
	 * 
	 * @param startByte the start byte of the data associated with the attribute.
	 */
	public void setStartByte(long startByte) { mStartByte = startByte; }
	/**
	 * Retrieve the current start byte of the data associated with the attribute.
	 * 
	 * @return the start byte of the data associated with the attribute.
	 */
	public long getStartByte() { return mStartByte; }
	
	/**
	 * Set the array of values associated with the attribute.
	 * 
	 * @param values a list of values to associate with the attribute.
	 */
	public void setValues(ArrayList<String> values) { mValues.clear(); mValues.addAll(values); } 
	/**
	 * Add values to the array of values already associated with the attribute.
	 * 
	 * @param values a list of values to add to the existing list of values.
	 * 
	 * @return the list of all values associated with the attribute.
	 */
	public ArrayList<String> addValues(ArrayList<String> values) { mValues.addAll(values); return mValues; } 
	/**
	 * Get the current list of values associated with the attribute.
	 * 
	 * @return the list of values associated with the attribute.
	 */
	public ArrayList<String> getValues() { return mValues; }
	/**
	 * Retrieve all values as a space separated list.  
	 * 
	 * @return a formatted string containing all values.
	 */
	public String getValueString() { String buffer = ""; for(String v : mValues) { buffer += " " + v; } return buffer; 	}

}
