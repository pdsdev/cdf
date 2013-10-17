package pds.cdf;

import java.util.ArrayList;

/**
 * Variable information.
 * 
 * @author tking
 *
 */
public class Variable {

	String mName = null;
	int		mDataType = 0;
	int		mIndex = -1;
	long	mStartByte = 0L;
	long	mRecordCount = 0L;
	int		mFlags = 0;
	int[]	mDims = null; // Zero or more contiguous dimension sizes for this zVariable
	double[] mPadValue = null; // The variable's pad value.
	
	ArrayList<Attribute> mAttributes = new ArrayList<Attribute>();
	
	public Variable() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Display a description of each variable.
	 */
	public void dump() {
		System.out.println("=================================");
		System.out.println("      Variable: " + mName);
		System.out.println("=================================");
		System.out.println("DataType: " + Constant.getDataTypeName(mDataType));
		System.out.println("Flags: " + Constant.toHexString(mFlags));
		System.out.println("Index: " + mIndex);
		System.out.println("StartByte: " + mStartByte);
		System.out.print("Dims:");
		if(mDims != null) {
			for(int d : mDims) { System.out.print(" " + d);	}
		}
		System.out.println("");
		System.out.print("PadValue:");
		if(mPadValue != null) {
			for(double v : mPadValue) { System.out.print(" " + v);	}
		}
		System.out.println("");
		System.out.println("RecordCount: " + mRecordCount);
		System.out.println("Attributes: ");
		for(Attribute a : mAttributes) {
			System.out.println("   " + a.getName() + " = " + a.getValueString());
		}
	}
	
	/**
	 * Set the name for the variable.
	 * 
	 * @param name the name for the variable.
	 */
	public void setName(String name) { mName = name; }
	/**
	 * Retrieve the name of the variable.
	 * 
	 * @return the current name of the variable.
	 */
	public String getName() { return mName; }
	
	/**
	 * Set the data type for the variable.
	 * 
	 * @param dataType the data type for the variable.
	 */
	public void setDataType(int dataType) { mDataType = dataType; }
	/**
	 * Retrieve the data type of the variable.
	 * 
	 * @return the current data type of the variable.
	 */
	public int getDataType() { return mDataType; }
	
	/**
	 * Set the flag values for the variable.
	 * 
	 * @param flags the current flag values for the variable.
	 */
	public void setFlags(int flags) { mFlags = flags; }
	/**
	 * Retrieve the flags associated with the variable.
	 * 
	 * @return the current flags of the variable.
	 */
	public int getFlags() { return mFlags; }
	
	/**
	 * Set the index of the variable.
	 * 
	 * @param index the index of the variable.
	 */
	public void setIndex(int index) { mIndex = index; }
	/**
	 * Retrieve the index of the variable.
	 * 
	 * @return the current index of the variable.
	 */
	public int getIndex() { return mIndex; }
	
	/**
	 * Set the start byte of the data associated with the variable.
	 * 
	 * @param startByte the start byte of the data associated with the variable.
	 */
	public void setStartByte(long startByte) { mStartByte = startByte; }
	/**
	 * Retrieve the start byte of the data associated with the variable.
	 * 
	 * @return the current start byte of the data associated with the variable.
	 */
	public long getStartByte() { return mStartByte; }
	
	/**
	 * Set the number of data records in the variable.
	 * 
	 * @param recordCount the number of data records in the variable.
	 */
	public void setRecordCount(long recordCount) { mRecordCount = recordCount; }
	/**
	 * Retrieve the number of data records in the varaible.
	 * 
	 * @return the number of data records in the variable.
	 */
	public long getRecordCount() { return mRecordCount; }
	
	/**
	 * Set the number and size of data dimensions. 
	 * The number of elements in the array is the number of dimensions and the value of of each element 
	 * is the size of the corresponding dimension.
	 *   
	 * @param dims an array defining the dimensions.
	 * 
	 * @return the current dimensions of the variable. Returns null if no dimensions are set.
	 */
	public int[] setDims(int[] dims) {
		if(dims == null) return null;
		
		mDims = new int[dims.length];
		for (int i = 0; i < dims.length; i++) {
			mDims[i] = dims[i];
		}
		return mDims;
	}

	/**
	 * Retrieve the dimensions of the variable.
	 * 
	 * @return the current dimensions of the variable.
	 */
	public int[] getDims() { return mDims; }
	

	/**
	 * Retrieve the value of a specified dimension of the variable.
	 * 
	 * @return the size of a dimension of the variable or -1 if an invalid dimension
	 */
	public int getDim(int n) { if(n < 0 || n >= mDims.length) { return -1; } else {return mDims[n]; } }
	

	/**
	 * Retrieve the dimensions of the variable.
	 * 
	 * @return the current dimensions of the variable.
	 */
	public int getDimCount() { if(mDims == null) { return 0; } else { return mDims.length; } }
	

	/**
	 * Set the pad values for each dimension of a variable.
	 * The number of elements in the array must match the number of dimensions and the value of of each element 
	 * is the pad value for the corresponding dimension.
	 * 
	 * @param padValue an array of pad values.
	 * 
	 * @return the array of pad values (one per dimension) or null if no pad values have been set.
	 */
	public double[] setPadValue(double[] padValue) {
		if (padValue == null) return null;
		
		mPadValue = new double[padValue.length];
		for (int i = 0; i < padValue.length; i++) {
			mPadValue[i] = padValue[i];
		}
		return mPadValue;
	}

	/**
	 * Retrieve the padd values for all dimensions of the variable.
	 * 
	 * @return an array of pad values, one for each dimension. Returns null if no pad values are set.
	 */
	double[] getPadValue() { return mPadValue; }
	
	/**
	 * Add a predefined attribute to the list of attributes.
	 * 
	 * @param attribute the predefined Attribute.
	 */
	public void addAttribute(Attribute attribute) { 
		mAttributes.add(attribute);
	}
	
	/**
	 * Define an attribute and the values associated with the attribute.
	 * 
	 * @param name the name of the attribute.
	 * @param values the array of values to associate with the attribute.
	 */
	public void setAttribute(String name, ArrayList<String> values) { 
		Attribute a = new Attribute();
		a.setName(name);
		a.setValues(values);
		mAttributes.add(a);
	}
	
	/**
	 * Retrieve an attribute with a given name.
	 * 
	 * @param name the name of the attribute.
	 * 
	 * @return a string containing the formatted value. If no attribute exists then a empty string is returned. 
	 */
	public String getAttributeValue(String name) {
		for(Attribute a : mAttributes) {
			if(a.getName().equals(name)) return a.getValueString();
		}
		
		return "";
	}
	
	/**
	 * Retrieve an attribute with a given name.
	 * 
	 * @param name the name of the attribute.
	 * @param delim the delimiter to place between values.
	 * 
	 * @return a string containing the formatted value. If no attribute exists then a empty string is returned. 
	 */
	public String getAttributeValue(String name, String delim) {
		for(Attribute a : mAttributes) {
			if(a.getName().equals(name)) return a.getValueString(delim);
		}
		
		return "";
	}
	
	/**
	 * Retrieve all attributes associated with the variable.
	 * 
	 * @return the array of all attributes associated with the variable.
	 */
	public ArrayList<Attribute> getAttributes() { return mAttributes; } 
}
