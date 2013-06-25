package pds.cdf;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Global Descriptor Record (CDR).
 * The GDR contains general information about the CDF. 
 * Defined in Section 2.3 of CDF specification.
 * 
 * @author tking
 *
 */
public class GDRecord extends Record {

	long mRVDRhead;	// The file offset of the first rVariable Descriptor Record (rVDR).
	long mZVDRhead; // The file offset of the first zVariable Descriptor Record (zVDR).
	long mADRhead; // The file offset of the first Attribute Descriptor Record (ADR).
	long mEOF; // The end-of-file (EOF) position in the dotCDF file.
	int mNrVars; // The number of rVariables in the CDF.
	int mNumAttr; // The number of attributes in the CDF.
	int mRMaxRec; // The maximum rVariable record number in the CDF.
	int mRNumDims; // The number of dimensions for rVariables.
	int mNzVars; // The number of zVariables in the CDF.
	long mUIRhead; // The file offset of the first Unused Internal Record (UIR).
	int[] rDimSizes; // Zero or more contiguous rVariable dimension sizes depending on the value of the	rNumDims field described above.
	
	/**
	 * Create a GDRecord based on a base record.
	 * 
	 * @param base the base record to derive a GDRecord.
	 */
	public GDRecord(Record base) {
		mimic(base);
	}

	/**
	 * Read the contents of a GDRecord from an input stream.
	 * 
	 * @param offset the current byte offset into the stream
	 * @param in the input stream to read from.
	 * 
	 * @return the byte offset after reading the record.
	 * 
	 * @throws IOException if any reading error occurs.
	 */
	public long read(long offset, DataInputStream in) throws IOException {
		mRVDRhead = in.readLong(); offset += 8; // The file offset of the first rVariable Descriptor Record (rVDR).
		mZVDRhead = in.readLong(); offset += 8; // The file offset of the first zVariable Descriptor Record (zVDR).
		mADRhead = in.readLong(); offset += 8; // The file offset of the first Attribute Descriptor Record (ADR).
		mEOF = in.readLong(); offset += 8; // The end-of-file (EOF) position in the dotCDF file.
		mNrVars = in.readInt(); offset += 4; // The number of rVariables in the CDF.
		mNumAttr = in.readInt(); offset += 4; // The number of attributes in the CDF.
		mRMaxRec = in.readInt(); offset += 4; // The maximum rVariable record number in the CDF.
		mRNumDims = in.readInt(); offset += 4; // The number of dimensions for rVariables.
		mNzVars = in.readInt(); offset += 4; // The number of zVariables in the CDF.
		mUIRhead = in.readLong(); offset += 8; // The file offset of the first Unused Internal Record (UIR).
		in.readInt(); offset += 4;	// rfuC
		in.readInt(); offset += 4;	// rfuD
		in.readInt(); offset += 4;	// rfuE
		if(mRNumDims > 0) {
			rDimSizes = new int[mRNumDims];
			for(int i = 0; i < mRNumDims; i++) { rDimSizes[i] = in.readInt(); offset += 4; }
		}
		return offset;
	}

	/**
	 * Display a description of the record.
	 */
	public void dump() {
		System.out.println("=================================");
		System.out.println("              GDR");
		System.out.println("=================================");
		System.out.println("RVDRhead: " + mRVDRhead);
		System.out.println("ZVDRhead: " + mZVDRhead);
		System.out.println("ADRhead: " + mADRhead);
		System.out.println("EOF: " + mEOF);
		System.out.println("NrVars: " + mNrVars);
		System.out.println("NumAttr: " + mNumAttr);
		System.out.println("RMaxRec: " + mRMaxRec);
		System.out.println("RNumDims: " + mRNumDims);
		System.out.println("NzVars: " + mNzVars);
		System.out.println("UIRhead: " + mUIRhead);
		System.out.print("RDimSize:");
		for(int i = 0; i < mRNumDims; i++) { System.out.println(" " + rDimSizes[i]); }
		System.out.println("");
		System.out.println("");
	}

}
