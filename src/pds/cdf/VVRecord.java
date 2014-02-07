package pds.cdf;

import java.io.DataInputStream;
import java.io.IOException;

/** 
 * Variable Value Record (VVR)
 * Variable Value Records (VVRs) are used to store one or more variable records in a single-file CDF.
 * Defined in Section 2.8 of CDF specification.
 * 
 * @author tking
 *
 */
public class VVRecord extends Record {
	
	long mDataStartByte = 0L;

	/**
	 * Create a VVRecord based on a base record.
	 * 
	 * @param base the base record to derive a VVRecord.
	 */
	public VVRecord(Record base) {
		mimic(base);
	}

	/**
	 * Read the contents of a VVRecord from an input stream.
	 * 
	 * @param offset the current byte offset into the stream
	 * @param in the input stream to read from.
	 * 
	 * @return the byte offset after reading the record.
	 * 
	 * @throws IOException if any reading error occurs.
	 */
	public long read(long offset, DataInputStream in) throws IOException {
		mDataStartByte = offset;
		long loffset = getSize() - 12;
		in.skip(loffset);
		offset += loffset;
		
		return offset;
	}	

	/**
	 * Display a description of the record.
	 */
	public void dump() {
		System.out.println("=================================");
		System.out.println("              VVR");
		System.out.println("=================================");
		System.out.println("DataStartByte: " + mDataStartByte);
		System.out.println("");
	}
}
