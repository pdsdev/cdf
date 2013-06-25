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
public class UIRecord extends Record {
	
	long mPrevUIR = 0L;
	long mNextUIR = 0L;
	
	/**
	 * Create a AEDRecord based on a base record.
	 * 
	 * @param base the base record to derive a AEDRecord.
	 */
	public UIRecord(Record base) {
		mimic(base);
	}

	/**
	 * Read the contents of a UIRecord from an input stream.
	 * 
	 * @param offset the current byte offset into the stream
	 * @param in the input stream to read from.
	 * 
	 * @return the byte offset after reading the record.
	 * 
	 * @throws IOException if any reading error occurs.
	 */
	public long read(long offset, DataInputStream in) throws IOException {
		mPrevUIR = in.readLong(); offset += 8; // The file offset of the previous UIR. 
		mNextUIR = in.readLong(); offset += 8; // The file offset of the next UIR. 
		
		// Skip remainder - unused data
		long loffset = getSize() - 28;
		in.skip(loffset); offset += loffset;
		
		return offset;
	}	

	/**
	 * Display a description of the record.
	 */
	public void dump() {
		System.out.println("=================================");
		System.out.println("              UIR");
		System.out.println("=================================");
		System.out.println("PrevUIR: " + Constant.toHexString(mPrevUIR));
		System.out.println("NextUIR: " + Constant.toHexString(mNextUIR));
		System.out.println("");
	}
}
