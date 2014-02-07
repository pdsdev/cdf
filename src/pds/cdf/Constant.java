package pds.cdf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.lang.Integer;

public class Constant {

	// Flag values
	/** Dimension variance. */			static final public int FLAG_VARIANCE=0x0001;
	/** Existence of pad values. */		static final public int FLAG_PAD=0x0002;
	/** Data compression in affect. */	static final public int FLAG_COMPRESSION=0x0004;
	
	// Scope values
	/** Attribute scope is global. */	static final public int SCOPE_GLOBAL=1;
	/** Attribute scope is variable. */ static final public int SCOPE_VARIABLE=2;
	/** Attribute scope is global. */	static final public int SCOPE_GLOBAL_ASSUME=3;
	/** Attribute scope is variable. */ static final public int SCOPE_VARIABLE_ASSUME=4;
	
	// Data Types
	/** 1-byte signed integer. */				static final public int CDF_INT1=1;
	/** 2-byte signed integer (short). */		static final public int CDF_INT2=2;
	/** 4-byte signed integer (int). */			static final public int CDF_INT4=4;
	/** 8-byte signed integer (long). */		static final public int CDF_INT8=8;
	/** 1-byte unsigned integer. */				static final public int CDF_UINT1=11;
	/** 4-byte unsigned integer (short). */		static final public int CDF_UINT2=12;
	/** 8-byte unsigned integer (int). */		static final public int CDF_UINT4=14;
	/** 1-byte signed integer. */				static final public int CDF_BYTE=41;
	/** 4-byte floating point (float) */ 		static final public int CDF_REAL4=21;
	/** 8-byte floating point (double) */ 		static final public int CDF_REAL8=22;
	/** 4-byte floating point (float) */ 		static final public int CDF_FLOAT=44;
	/** 8-byte floating point (double) */ 		static final public int CDF_DOUBLE=45;
	/** 8-byte, double-precision floating-point. */ 	static final public int CDF_EPOCH=31;
	/** 2 8-byte, double-precision floating-point. */ 	static final public int CDF_EPOCH16=32;
	/** 8-byte, signed integer. */ 				static final public int CDF_TIME_TT2000=33;
	/** 1-byte, signed character (ASCII). */ 	static final public int CDF_CHAR=51;
	
	// Record Types
	/** Unused Internal Record (UIR). */					static final public int RECORD_UIR=-1;
	/** CDF Descriptor Record (CDR). */						static final public int RECORD_CDR=1;
	/** Global Descriptor Record (GDR). */					static final public int RECORD_GDR=2;
	/** rVariable Descriptor Record (RVDR). */				static final public int RECORD_RVDR=3;
	/** Attribute Descriptor Record (ADR). */				static final public int RECORD_ADR=4;
	/** Attribute g/rEntry Descriptor Record (AGREDR). */	static final public int RECORD_AGREDR=5;
	/** Variable Index Record (VXR). */						static final public int RECORD_VXR=6;
	/** Variable Values Record (VVR). */					static final public int RECORD_VVR=7;
	/** zVariable Descriptor Record (ZVDR). */				static final public int RECORD_ZVDR=8;
	/** Attribute zEntry Descriptor Record (AZEDR). */		static final public int RECORD_AZEDR=9;
	/** Compressed CDF Record (CCR). */						static final public int RECORD_CCR=10;
	/** Compression Parameters Record (CPR). */				static final public int RECORD_CPR=11;
	/** Sparseness Parameters Record (SPR). */				static final public int RECORD_SPR=12;
	/** Compressed Variable Values Record (CVVR). */		static final public int RECORD_CVVR=13;
	
	/** Compression on value. */	static final public int CompressionOn = 0xCCCC0001;
	/** Compression off value. */	static final public int CompressionOff = 0x0000FFFF;

	/** eXternal Data Representation */ 	static final public int ENCODING_NETWORK=1;
	/** Sun representation */				static final public int ENCODING_SUN=2;
	/** VAX representation */				static final public int ENCODING_VAX=3;
	/** DECstation representation */		static final public int ENCODING_DECSTATION=4;
	/** SGi representation */				static final public int ENCODING_SGi=5;
	/** Intel Windows, Mac OS Intel and Solaris Intel representation */ static final public int ENCODING_IBMPC=6;
	/** IBM RS-6000 representation*/		static final public int ENCODING_IBMRS=7;
	/** Macintosh representation */			static final public int ENCODING_MAC=9;
	/** HP 9000 series representation */	static final public int ENCODING_HP=11;
	/** NeXT representation */				static final public int ENCODING_NeXT=12;
	/** DEC Alpha/OSF1 representation */	static final public int ENCODING_ALPHAOSF1=13;
	
	// MSB (big-endian) encoding are: NETWORK_ENCODING, SUN_ENCODING, NeXT_ENCODING, MAC_ENCODING, SGi_ENCODING, IBMRS_ENCODING
	// All others are LSB (little-endian)
	static final String [] mEncodingName = {"", "NETWORK_ENCODING", "SUN_ENCODING", "VAX_ENCODING", "DECSTATION_ENCODING",
			"SGi_ENCODING", "IBMPC_ENCODING", "IBMRS_ENCODING", "", "MAC_ENCODING", "", "HP_ENCODING", "NeXT_ENCODING",
			"ALPHAOSF1_ENCODING", "ALPHAVMSd_ENCODING", "ALPHAVMSg_ENCODING", "ALPHAVMSi_ENCODING" };

	public Constant() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Translate an encoding value into a string.
	 * 
	 * @param encoding the CDF encoding value.
	 * 
	 * @return the name of the encoding value.
	 */
	static public String getEncodingName(int encoding) {
		if(encoding < 0 || encoding > (mEncodingName.length - 1)) return "";
		
		return mEncodingName[encoding];
	}
	

	/**
	 * Determine if the version if a valid CDF version.
	 * 
	 * A valid CDF version begins with 0xCDF.
	 * 
	 * @param version the version value.
	 * 
	 * @return true if a valid CDF version, false otherwise.
	 */
	static public boolean isValidVersion(int version) {
		if((version & 0xFFF00000) != 0xCDF00000) {
			System.out.println("File does not appear to be a well formed CDF.");
			return false;			
		}
		
		return true;
	}
	
	/**
	 * Determines if compression is on.
	 * 
	 * @param compression the compression "magic" value.
	 * 
	 * @return true of compression is on, false otherwise.
	 */
	static public boolean isCompressed(int compression) {
		if(compression == CompressionOn) return true;
		
		return false;
	}
	
	/**
	 * Translate a record type token into a name.
	 * 
	 * @param recType the record type token.
	 * 
	 * @return The acronym for the record type.
	 */
	static public String getRecTypeName(int recType)
	{
		switch(recType) {
		case RECORD_CDR: return("CDR");
		case RECORD_GDR: return("GDR");
		case RECORD_ADR: return("ADR");
		case RECORD_VXR: return("VXR");
		case RECORD_VVR: return("VVR");
		case RECORD_ZVDR: return("zVDR");
		case RECORD_AGREDR: return("AgreEDR");
		case RECORD_AZEDR: return("AzEDR");
		case RECORD_UIR: return("UIR");
		case RECORD_RVDR: return("rVDR");
		case RECORD_CCR: return("CCR");
		case RECORD_CPR: return("CPR");
		case RECORD_SPR: return("SPR");
		case RECORD_CVVR: return("CVVR");
		}
		
		return("Unknown [" + recType + "]");
	}
	
	/**
	 * Translate a data type token into a name.
	 * 
	 * @param dataType the data type token.
	 * 
	 * @return the corresponding name for the token.
	 */
	static public String getDataTypeName(int dataType)
	{
		switch(dataType) {
		case CDF_INT1: return("CDF_INT1");
		case CDF_INT2: return("CDF_INT2");
		case CDF_INT4: return("CDF_INT4");
		case CDF_INT8: return("CDF_INT8");
		case CDF_UINT1: return("CDF_UINT1");
		case CDF_UINT2: return("CDF_UINT2");
		case CDF_UINT4: return("CDF_UINT4");
		case CDF_BYTE: return("CDF_BYTE");
		case CDF_REAL4: return("CDF_REAL4");
		case CDF_REAL8: return("CDF_REAL8");
		case CDF_FLOAT: return("CDF_FLOAT");
		case CDF_DOUBLE: return("CDF_DOUBLE");
		case CDF_EPOCH: return("CDF_EPOCH");
		case CDF_EPOCH16: return("CDF_EPOCH16");
		case CDF_TIME_TT2000: return("CDF_TIME_TT2000");
		case CDF_CHAR: return("CDF_CHAR");
		}
		
		return("Unknown");
	}

	/**
	 * Translate a data type token into a name.
	 * 
	 * @param dataType the data type token.
	 * 
	 * @return the corresponding name for the token.
	 */
	static public String getDataTypePDS(int dataType)
	{
		switch(dataType) {
		case CDF_INT1: return("SignedByte");
		case CDF_INT2: return("SignedMSB2");
		case CDF_INT4: return("SignedMSB4");
		case CDF_INT8: return("SignedMSB8");
		case CDF_UINT1: return("UnsignedByte");
		case CDF_UINT2: return("UnsignedMSB2");
		case CDF_UINT4: return("UnsignedMSB8");
		case CDF_BYTE: return("UnsignedByte");
		case CDF_REAL4: return("IEEE754MSBSingle");
		case CDF_REAL8: return("IEEE754MSBDouble");
		case CDF_FLOAT: return("IEEE754MSBSingle");
		case CDF_DOUBLE: return("IEEE754MSBDouble");
		case CDF_EPOCH: return("IEEE754MSBDouble");
		case CDF_EPOCH16: return("ComplexMSB16");// Really two doubles
		case CDF_TIME_TT2000: return("IEEE754MSBDouble");
		case CDF_CHAR: return("UnsignedByte");
		}
		
		return("Unknown");
	}


	/**
	 * Retrieve the size in bytes of a data type.
	 * 
	 * @param dataType the data type token.
	 * 
	 * @return the number of bytes the data type occupies.
	 */
	static public int getDataTypeSize(int dataType)
	{
		switch(dataType) {
		case CDF_INT1: return(1);
		case CDF_INT2: return(2);
		case CDF_INT4: return(4);
		case CDF_INT8: return(8);
		case CDF_UINT1: return(1);
		case CDF_UINT2: return(2);
		case CDF_UINT4: return(4);
		case CDF_BYTE: return(1);
		case CDF_REAL4: return(4);
		case CDF_REAL8: return(8);
		case CDF_FLOAT: return(4);
		case CDF_DOUBLE: return(8);
		case CDF_EPOCH: return(8);
		case CDF_EPOCH16: return(16);	// (2) 8-byte values
		case CDF_TIME_TT2000: return(8);
		case CDF_CHAR: return(1);
		}
		
		return(0);
	}

	/**
	 * Translate a scope token into a name.
	 * 
	 * @param scope the scope token.
	 * 
	 * @return the name corresponding to the token.
	 */
	static public String getScopeName(int scope)
	{
		switch(scope) {
		case SCOPE_GLOBAL: return("Global");
		case SCOPE_VARIABLE: return("Variable");
		case SCOPE_GLOBAL_ASSUME: return("Global/Assume");
		case SCOPE_VARIABLE_ASSUME: return("Variable/Assume");
		}
		
		return("Unknown [" + scope + "]");
	}
	
	/**
	 * Generate a string which represents a value in hex notation. Leading zeros are included in the string.
	 *  
	 * @param val the value to represent.
	 * 
	 * @return the string representation the value in hex notation.
	 */
	static public String toHexString(int val)
	{
		String value = "0x";
		
		int n = Integer.numberOfLeadingZeros(val);
		if(n > 0) n = n / 4;
		for(int i = 0; i < n; i++) { value += "0"; }
		value += Integer.toHexString(val);
		
		return value;
	}
	
	/**
	 * Generate a string which represents a value in hex notation. Leading zeros are included in the string.
	 *  
	 * @param val the value to represent.
	 * 
	 * @return the string representation the value in hex notation.
	 */
	static public String toHexString(long val)
	{
		String value = "0x";
		
		int n = Long.numberOfLeadingZeros(val);
		if(n > 0) n = n / 4;
		for(int i = 0; i < n; i++) { value += "0"; }
		value += Long.toHexString(val);
		
		return value;
	}

	/**
	 * Transform a value into a string representation.
	 * 
	 * A binary array of bytes are parsed based on the given data type. 
	 * Only the value of the first element in the buffer is transformed.
	 * 
	 * @param buffer the bytes containing the value.
	 * @param dataType the data type of the value.
	 * 
	 * @return the string with a representation of the first value in the data array.
	 */
	static public String valueToString(byte[] buffer, int dataType) {
		return valueToString(buffer, dataType, 0);
	}
	
	/**
	 * Transform all values into a space separated string representation.
	 * 
	 * A binary array of bytes are parsed based on the given data type. 
	 * Only the value of the first element in the buffer is transformed.
	 * 
	 * @param buffer the bytes containing the value.
	 * @param dataType the data type of the value.
	 * 
	 * @return the string with a representation of the first value in the data array.
	 */
	static public String valueToStringList(byte[] buffer, int dataType, int numElems) {
		String text = "";
		
		ArrayList<String> values = valueToArrayList(buffer, dataType, numElems);
		for(String v : values) {
			text = " " + v;
		}
		return text;
	}
	
	/**
	 * Transform a value into a string representation.
	 * 
	 * A binary array of bytes are parsed based on the given data type.
	 * The value of with the given element index is transformed.
	 * 
	 * @param buffer the bytes containing the value.
	 * @param dataType the data type of the value.
	 * @param elem the index of the element to transform.
	 * 
	 * @return the string with a representation of the value.
	 */
	static public String valueToString(byte[] buffer, int dataType, int elem) {
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		
		switch(dataType) {
		case CDF_INT1: return("" + buffer[elem]); 
		case CDF_INT2: return("" + bb.getShort(elem));
		case CDF_INT4: return("" + bb.getInt(elem));
		case CDF_INT8: return("" + bb.getLong(elem));
		case CDF_UINT1: return("" + buffer[elem]);
		case CDF_UINT2: return("" + bb.getInt(elem));
		case CDF_UINT4: return("" + bb.getInt(elem));
		case CDF_BYTE: return("" + buffer[elem]);
		case CDF_REAL4: return("" + bb.getFloat(elem));
		case CDF_REAL8: return("" + bb.getDouble(elem));
		case CDF_FLOAT: return("" + bb.getFloat(elem));
		case CDF_DOUBLE: return("" + bb.getDouble(elem));
		case CDF_EPOCH: return("" + bb.getDouble(elem));
		case CDF_EPOCH16: return(bb.getDouble((elem*2)) + ":" + bb.getInt((elem*2)+1));	// (2) 8-byte values
		case CDF_TIME_TT2000: return("" + bb.getDouble(elem));
		case CDF_CHAR: return(new String(buffer).trim());
		}
		
		return "";
	}
	
	/**
	 * Transform a set of values into a string representation.
	 * 
	 * A binary array of bytes are parsed based on the given data type.
	 * The value of each element, up to the number of elements given, is transformed.
	 * 
	 * @param buffer the bytes containing the value.
	 * @param dataType the data type of the value.
	 * @param numElems the number of elements to transform.
	 * 
	 * @return the list of strings representing the value of each element.
	 */
	static public ArrayList<String> valueToArrayList(byte[] buffer, int dataType, int numElems) {
		ArrayList<String> values = new ArrayList<String>();

		if(dataType == CDF_CHAR) {	// The numElems is the length of the string 
			values.add(new String(buffer).trim());
		} else {	// Array of values
			ByteBuffer bb = ByteBuffer.wrap(buffer);

			for(int i = 0; i < numElems; i++) {
				switch(dataType) {
				case CDF_INT1: values.add("" + buffer[i]); break;
				case CDF_INT2: values.add("" + bb.getShort(i)); break;
				case CDF_INT4: values.add("" + bb.getInt(i)); break;
				case CDF_INT8: values.add("" + bb.getLong(i)); break;
				case CDF_UINT1: values.add("" + buffer[i]); break;
				case CDF_UINT2: values.add("" + bb.getShort(i)); break;
				case CDF_UINT4: values.add("" + bb.getInt(i)); break;
				case CDF_BYTE: values.add("" + buffer[i]); break;
				case CDF_REAL4: values.add("" + bb.getFloat(i)); break;
				case CDF_REAL8: values.add("" + bb.getDouble(i)); break;
				case CDF_FLOAT: values.add("" + bb.getFloat(i)); break;
				case CDF_DOUBLE: values.add("" + bb.getDouble(i)); break;
				case CDF_EPOCH: values.add("" + bb.getDouble(i)); break;
				case CDF_EPOCH16: values.add(bb.getDouble((i*2)) + ":" + bb.getInt((i*2)+1)); break;	// (2) 8-byte values
				case CDF_TIME_TT2000: values.add("" + bb.getDouble(i)); break;
				}
			}
		}
		
		return values;
	}
	
}
