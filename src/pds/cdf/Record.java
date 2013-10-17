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
		System.out.println("Type: " + mType);
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
