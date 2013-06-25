package pds.cdf;

import java.io.DataInputStream;
import java.io.IOException;

/** 
 * Attribute Entry Descriptor Record (AEDR)
 * An Attribute Entry Descriptor Record (AEDR) contains a description of an attribute entry. 
 * Support both types of AEDRs: AgrEDRs describing g/rEntries and AzEDRs describing zEntries.
 * Defined in Section 2.5 of CDF specification.
 * 
 * @author tking
 *
 */
public class AEDRecord extends Record {
	long mAEDRnext; // The file offset of the next AEDR. 
	int mAttrNum; // The attribute number to which this entry corresponds.
	int mDataType; // The data type of this entry.
	int mNum; // This variable's number.
	int mNumElems;  // The number of elements of the data type for this variable at each value.
	byte[] mValue = null;

	long mDataStartByte;	// Start of data 

	/**
	 * Create a AEDRecord based on a base record.
	 * 
	 * @param base the base record to derive a AEDRecord.
	 */
	public AEDRecord(Record base) {
		mimic(base);
	}

	/**
	 * Read the contents of a AEDRecord from an input stream.
	 * 
	 * @param offset the current byte offset into the stream
	 * @param in the input stream to read from.
	 * 
	 * @return the byte offset after reading the record.
	 * 
	 * @throws IOException if any reading error occurs.
	 */
	public long read(long offset, DataInputStream in) throws IOException {
		mAEDRnext = in.readLong(); offset += 8;  // The file offset of the next AEDR. 
		// if(nextItem != 0L) mMessages.add("Attribute records are fragmented. This is not allowed. A possible cause could be that the records were written incrementally. Re-writing the file may correct the problem.");
		mAttrNum = in.readInt(); offset += 4; // The attribute number to which this entry corresponds.
		mDataType = in.readInt(); offset += 4; // The data type of this entry.
		mNum = in.readInt(); offset += 4; // This variable's number.
		mNumElems = in.readInt(); offset += 4; // The number of elements of the data type for this variable at each value.
		
		in.readInt(); offset += 4; // rfuA - Reserved for future use.
		in.readInt(); offset += 4; // rfuB - Reserved for future use.
		in.readInt(); offset += 4; // rfuC - Reserved for future use.
		in.readInt(); offset += 4; // rfuD - Reserved for future use.
		in.readInt(); offset += 4; // rfuE - Reserved for future use.

		mDataStartByte = offset;
		
		int len = (int) getSize() - 56;
		mValue = new byte[len];
		in.read(mValue, 0, len); offset += len;

		return offset;
	}

	/**
	 * Display a description of the record.
	 */
	public void dump() {
		System.out.println("=================================");
		System.out.println("              AEDR");
		System.out.println("=================================");
		System.out.println("AEDRnext: " + mAEDRnext); // The file offset of the next AEDR. 
		System.out.println("AttrNum: " + mAttrNum); // The attribute number to which this entry corresponds.
		System.out.println("DataType: " + mDataType); // The data type of this entry.
		System.out.println("Num: " + mNum); // This variable's number.
		System.out.println("NumElems: " + mNumElems);  // The number of elements of the data type for this variable at each value.
		System.out.println("DataStartByte: " + mDataStartByte);	// Start of data 
		System.out.println("");		
	}
	
	/**
	 * Retrieve a list of values in a space delimited string.
	 *  
	 * @return the list of values in a space delimited string.
	 */
	public String valueToString() {
		return Constant.valueToStringList(mValue, mDataType, mNumElems);
	}
}
