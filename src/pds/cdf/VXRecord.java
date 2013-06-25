package pds.cdf;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Variable Index Record (VXR)
 * (VXRs) are used in single-file CDFs to store the file offsets of 
 * any lower level of VXRs or Variable Values Records (VVRs).
 * Defined in Section 2.7 of CDF specification.
 * 
 * @author tking
 *
 */
public class VXRecord extends Record {
	long mVXRnext; // The file offset of the next VXR. The last VXR will contain a file offset of 0
	int mNentries; // The number of index entries in this VXR. This is the maximum number of VVRs that may be indexed using this VXR.
	int mNusedEntries; // The number of index entries actually used in this VXR.
	int[] mFirst; // The index of the first entry in the array.
	int[] mLast; // The index of the last entry in the array.
	long[] mVVRList; // The file offset to beginning of VVR containing data. The nth entry in this array corresponds to the nth entry in	the First and Last fields.
	
	/**
	 * Create a VXRecord based on a base record.
	 * 
	 * @param base the base record to derive a VXRecord.
	 */
	public VXRecord(Record base) {
		mimic(base);
	}

	/**
	 * Read the contents of a VXRecord from an input stream.
	 * 
	 * @param offset the current byte offset into the stream
	 * @param in the input stream to read from.
	 * 
	 * @return the byte offset after reading the record.
	 * 
	 * @throws IOException if any reading error occurs.
	 */
	public long read(long offset, DataInputStream in) throws IOException {
		mVXRnext = in.readLong(); offset += 8; // The file offset of the next VXR. The last VXR will contain a file offset of 0;
		mNentries = in.readInt(); offset += 4; // The number of index entries in this VXR. This is the maximum number of VVRs that may be indexed using this VXR.
		mNusedEntries = in.readInt(); offset += 4; // The number of index entries actually used in this VXR.
		if(mNentries > 0) {
			mFirst = new int[mNentries]; // The index of the first entry in the array.
			for(int i = 0; i < mNentries; i++) { mFirst[i] = in.readInt(); offset += 4; }
			mLast = new int[mNentries]; // The index of the last entry in the array.
			for(int i = 0; i < mNentries; i++) { mLast[i] = in.readInt(); offset += 4; }
			mVVRList = new long[mNentries]; // The file offset to beginning of array. The nth entry in this array corresponds to the nth entry in	the First and Last fields.
			for(int i = 0; i < mNentries; i++) { mVVRList[i] = in.readLong(); offset += 8; }
		}
		
		return offset;
	}
	
	public void dump() {
		System.out.println("=================================");
		System.out.println("              VXR");
		System.out.println("=================================");
		System.out.println("VXRnext: " + mVXRnext);
		System.out.println("Nentries: " + mNentries);
		System.out.println("NusedEntries: " + mNusedEntries);
		if(mNentries > 0) {	// These values are present
			System.out.print("First:");
			for(int i = 0; i < mFirst.length; i++) System.out.print(" " + mFirst[i]); 
			System.out.println("");
			System.out.print("Last:");
			for(int i = 0; i < mLast.length; i++) System.out.print(" " + mLast[i]); 
			System.out.print("VVRList:");
			for(int i = 0; i < mVVRList.length; i++) System.out.print(" " + mVVRList[i]); 
			System.out.println("");
		}
		System.out.println("");
	}
}
