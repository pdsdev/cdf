/**
 * 
 */
package pds.cdf;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * The stub record all CDF records are based on.
 * 
 * @author tking
 *
 */
public class Record {
	long mOffset = 0L;
	long mSize = 0L;
	int mType = 0;
	
	public Record() {	
	}
	
	/**
	 * Create an instance with a byte offset defined. 
	 * 
	 * @param offset the byte offset for the record.
	 */
	public Record(long offset) {
		mOffset = offset;
	}
	
	/**
	 * Read the contents of a Record from an input stream.
	 * 
	 * @param in the input stream to read from.
	 * 
	 * @return the byte offset after reading the record.
	 * 
	 * @throws IOException if any reading error occurs.
	 */
	public long read(DataInputStream in) throws IOException {
		long offset = mOffset;
		mSize = in.readLong(); offset += 8;
		mType = in.readInt(); offset += 4;
		
		return offset;
	}
	
	/**
	 * Read a data value based on the passed data type.
	 * 
	 * @param in the input stream to read from.
	 * @param dataType	the data type of the value.
	 * 
	 * @return a double containing the value.
	 * 
	 * @throws IOException if any reading error occurs.
	 */
	public double readDataValue(DataInputStream in, int dataType) throws IOException {
		double value = 0.0;

		switch(dataType) {
			case Constant.CDF_INT1: value = in.readByte(); break; 
			case Constant.CDF_INT2: value = in.readShort(); break; 
			case Constant.CDF_INT4: value = in.readInt(); break;
			case Constant.CDF_INT8: value = in.readLong(); break; 
			case Constant.CDF_UINT1: value = in.readByte(); break; 
			case Constant.CDF_UINT2: value = in.readShort(); break;
			case Constant.CDF_UINT4: value = in.readInt(); break;
			case Constant.CDF_BYTE: value = in.readByte(); break;
			case Constant.CDF_REAL4: value = in.readFloat(); break; 
			case Constant.CDF_REAL8: value = in.readDouble(); break;
			case Constant.CDF_FLOAT: value = in.readFloat(); break;
			case Constant.CDF_DOUBLE: value = in.readDouble(); break; 
			case Constant.CDF_EPOCH: value = in.readDouble(); break;
			case Constant.CDF_EPOCH16: value = in.readDouble(); value = in.readDouble(); break; // Does not return full value
			case Constant.CDF_TIME_TT2000: value = in.readLong(); break; // Does not return full value
			case Constant.CDF_CHAR: value = in.readByte(); break;
		}

		return value;
	}
	
	/**
	 * Read a data value based on the passed data type.
	 * 
	 * @param in the input stream to read from.
	 * @param dataType	the data type of the value.
	 * @param length	the number of characters to read.
	 * 
	 * @return a String containing the value.
	 * 
	 * @throws IOException if any reading error occurs.
	 */
	public String readStringValue(DataInputStream in, int dataType, int length) throws IOException {
		String value = "";

		switch(dataType) {
			case Constant.CDF_CHAR: {
				byte bString[] = new byte[length];
				for(int i = 0; i < length; i++) {
					bString[i] = in.readByte();
				}
				value = new String(bString, "US-ASCII");
				break;
			}
		}

		return value;
	}
	
	/**
	 * Copy of base information from one Record to this record.
	 * 
	 * @param base the Record to copy from.
	 */
	public void mimic(Record base) {
		mOffset = base.mOffset;
		mSize = base.mSize;
		mType = base.mType;
	}
	
	/**
	 * Display a description of the record.
	 */
	public void dump() {
		System.out.println("=================================");
		System.out.println("              Record");
		System.out.println("=================================");
		System.out.println("Offset: " + mOffset);
		System.out.println("Size: " + mSize);
		System.out.println("Type: " + mType + " (" + Constant.getRecTypeName(mType) + ")");
	}
	
	/**
	 * Set the byte offset for the record.
	 * 
	 * @param offset the byte offset.
	 */
	public void setOffset(long offset) { mOffset = offset; }
	
	/**
	 * Retrieve the byte offset.
	 * 
	 * @return the current byte offset.
	 */
	public long getOffset() { return mOffset; }
	
	/**
	 * Set the size in bytes for the record.
	 * 
	 * @param size the size in bytes for the record.
	 */
	public void setSize(long size) { mSize = size; }
	/**
	 * Retrieve the size in bytes for the record.
	 * 
	 * @return the current size in bytes defined for the record. 
	 */
	public long getSize() { return mSize; }
	
	/**
	 * Set the type of record.
	 * 
	 *  See {@link Constant} for available types.
	 *  
	 * @param type the record type.
	 */
	public void setType(int type) { mType = type; }
	/**
	 * Get the record type.
	 * 
	 * @return the current record type.
	 */
	public int getType() { return mType; }
}
