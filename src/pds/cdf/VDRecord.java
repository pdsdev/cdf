package pds.cdf;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Variable Descriptor Record (VDR).
 * A VDR contains a description of a variable in a CDF. 
 * Handles both types of VDRs: rVDRs describing rVariables and zVDRs describing zVariables.
 * Defined in Section 2.6 of CDF specification.
 * 
 * @author tking
 *
 */
public class VDRecord extends Record {
	long mVDRnext; // The file offset of the next VDR. 
	int mDataType; // The data type of this entry.
	int mMaxRec; // The maximum record number written to this variable.
	long mVXRHead; // The file offset of the first Variable Index Record (VXR).
	long mVXRTail; // The file offset of the last VXR.
	int mFlags; // flags // Boolean flags, one per bit, describing some aspect of this variable.
	int mSRecords; // Type of sparse records.
	int mNumElems; // The number of elements of the data type for this variable at each value.
	int mNum; // This variable's number.
	long mCPRorSPRoffset; // CPR/SPR offset depending on bits set in 'Flags' and compression used.
	int mBlockingFactor; // Blocking factor for this variable.
	String mName; // The name of this attribute.
	int mZNumDims; // The number of dimensions for this zVariable. This field will not be present if this is an rVDR (rVariable).
	int[] mZDimSize; // Zero or more contiguous dimension sizes for this zVariable
	int[] mDimVarys; // Zero or more contiguous dimension variances.
	double[] mPadValue; // The variable's pad value.

	// Passed from GDR for rVDR
	int mRNumDims; // The number of dimensions for this zVariable. This field will not be present if this is an rVDR (rVariable).
	
	/**
	 * Create a VDRecord based on a base record.
	 * 
	 * @param base the base record to derive a VDRecord.
	 * @param rNumDims the number of dimensions in the VDRecord.
	 */
	public VDRecord(Record base, int rNumDims) {
		mimic(base);
		mRNumDims = rNumDims;
	}

	/**
	 * Read the contents of a VDRecord from an input stream.
	 * 
	 * @param offset the current byte offset into the stream
	 * @param in the input stream to read from.
	 * 
	 * @return the byte offset after reading the record.
	 * 
	 * @throws IOException if any reading error occurs.
	 */
	public long read(long offset, DataInputStream in) throws IOException {
		mVDRnext = in.readLong(); offset += 8; // The file offset of the next VDR. 
		mDataType = in.readInt(); offset += 4; // The data type of this entry.
		mMaxRec = in.readInt(); offset += 4; // The maximum record number written to this variable.
		mVXRHead = in.readLong(); offset += 8; // The file offset of the first Variable Index Record (VXR).
		mVXRTail = in.readLong(); offset += 8; // The file offset of the last VXR.
		mFlags = in.readInt(); offset += 4; // Boolean flags, one per bit, describing some aspect of this variable.
		mSRecords = in.readInt(); offset += 4; // Type of sparse records.
		in.readInt(); offset += 4; // rfuB - Reserved for future use.
		in.readInt(); offset += 4; // rfuC -Reserved for future use.
		in.readInt(); offset += 4; // rfuF - Reserved for future use.
		mNumElems = in.readInt(); offset += 4; // The number of elements of the data type for this variable at each value.
		mNum = in.readInt(); offset += 4; // This variable's number.
		mCPRorSPRoffset = in.readLong(); offset += 8; // CPR/SPR offset depending on bits set in 'Flags' and compression used.
		mBlockingFactor = in.readInt(); offset += 4; // Blocking factor for this variable.
		byte[] buff = new byte[256];
		in.read(buff, 0, 256); offset += 256;  // The name of this attribute.
		mName = new String(buff).trim();
		if(mType == Constant.ZVDR_TYPE) {
			mZNumDims = in.readInt(); offset += 4; // The number of dimensions for this zVariable. This field will not be present if this is an rVDR (rVariable).
			if(mZNumDims > 0) { // Zero or more contiguous dimension sizes for this zVariable
				mZDimSize = new int[mZNumDims];
				for(int i = 0; i < mZNumDims; i++) { mZDimSize[i] = in.readInt(); offset += 4; 	}
				mDimVarys = new int[mZNumDims];
				for(int i = 0; i < mZNumDims; i++) { mDimVarys[i] = in.readInt(); offset += 4; }
			}
		} else {	// Assume its an rVDR
			mDimVarys = new int[mRNumDims];
			for(int i = 0; i < mRNumDims; i++) { mDimVarys[i] = in.readInt(); offset += 4; }
		}
		
		if((mFlags & Constant.FLAG_PAD) == Constant.FLAG_PAD) {
			mPadValue = new double[mNumElems];
			for(int i = 0; i < mNumElems; i++) { mPadValue[i] = in.readInt(); offset += Constant.getDataTypeSize(mDataType); }
		}
		
		return offset;
	}
	
	/**
	 * Display a description of the record.
	 */
	public void dump(CDF cdf) {
		System.out.println("=================================");
		System.out.println("              VDR");
		System.out.println("=================================");
		System.out.println("VDRnext: " + mVDRnext);
		System.out.println("DataType: " + mDataType);
		System.out.println("MaxRec: " + mMaxRec);
		System.out.println("VXRHead: " + mVXRHead);
		System.out.println("VXRTail: " + mVXRTail);
		System.out.println("Flags: " + mFlags);
		System.out.println("SRecords: " + mSRecords);
		System.out.println("NumElems: " + mNumElems);
		System.out.println("Num: " + mNum);
		System.out.println("CPRorSPRoffset: " + mCPRorSPRoffset);
		System.out.println("BlockingFactor: " + mBlockingFactor);
		System.out.println("Name: " + mName);
		System.out.println("ZNumDims: " + mZNumDims);
		if(mZNumDims > 0) {	// These values are present
			System.out.print("ZDimSize:");
			for(int i = 0; i < mZDimSize.length; i++) System.out.print(" " + mZDimSize[i]); 
			System.out.println("");
			System.out.print("mDimVarys:");
			for(int i = 0; i < mDimVarys.length; i++) System.out.print(" " + mDimVarys[i]); 
			System.out.println("");
		}
		if((mFlags & Constant.FLAG_PAD) == Constant.FLAG_PAD) {
			System.out.print("mPadValue:");
			for(int i = 0; i < mPadValue.length; i++) System.out.print(" " + mPadValue[i]); 
			System.out.println("");
		}
	}
}
