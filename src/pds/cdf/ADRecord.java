package pds.cdf;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Attribute Descriptor Record (ADR).
 * Contains a description of an attribute in a CDF. There will be one ADR per attribute.
 * Defined in Section 2.4 of CDF specification.
 * 
 * @author tking
 *
 */
public class ADRecord extends Record {
	long mADRnext; // The file offset of the next ADR. 
	long mAgrEDRhead; // The file offset of the first Attribute g/rEntry Descriptor Record (AgrEDR) for this attribute.
	int mScope; // The intended scope of this attribute.
	int mNum; // This attribute's number. Attributes are numbered beginning with zero (0).
	int mNgrEntries; // The number of g/rEntries for this attribute.
	int mMAXgrEntry; // The maximum numbered g/rEntry for this attribute. g/rEntries are numbered beginning with zero (0).
	long mAzEDRhead; // The file offset of the first Attribute zEntry Descriptor Record (AzEDR) for this attribute.
	int mNzEntries; // The number of zEntries for this attribute. 
	int mMAXzEntry; // The maximum numbered zEntry for this attribute. zEntries are numbered	beginning with zero (0). 
	String mName; // The name of this attribute.

	/**
	 * Create a ADRecord based on a base record.
	 * 
	 * @param base the base record to derive a ADRecord.
	 */
	public ADRecord(Record base) {
		mimic(base);
	}

	/**
	 * Read the contents of a ADRecord from an input stream.
	 * 
	 * @param offset the current byte offset into the stream
	 * @param in the input stream to read from.
	 * 
	 * @return the byte offset after reading the record.
	 * 
	 * @throws IOException if any reading error occurs.
	 */
	public long read(long offset, DataInputStream in) throws IOException {
		mADRnext = in.readLong(); offset += 8; // The file offset of the next ADR. 
		mAgrEDRhead = in.readLong(); offset += 8; // The file offset of the first Attribute g/rEntry Descriptor Record (AgrEDR) for this attribute.
		mScope = in.readInt(); offset += 4; // The intended scope of this attribute.
		mNum = in.readInt(); offset += 4; // This attribute's number. Attributes are numbered beginning with zero (0).
		mNgrEntries = in.readInt(); offset += 4; // The number of g/rEntries for this attribute.
		mMAXgrEntry = in.readInt(); offset += 4;  // The maximum numbered g/rEntry for this attribute. g/rEntries are numbered beginning with zero (0).
		in.readInt(); offset += 4; // rfuA - Reserved for future used. Always set to zero (0).
		mAzEDRhead = in.readLong(); offset += 8; // The file offset of the first Attribute zEntry Descriptor Record (AzEDR) for this attribute.
		mNzEntries = in.readInt(); offset += 4; // The number of zEntries for this attribute. 
		mMAXzEntry = in.readInt(); offset += 4; // The maximum numbered zEntry for this attribute. zEntries are numbered	beginning with zero (0). 
		in.readInt(); offset += 4; // rfuE - Reserved for future use. Always set to negative one (-1).
		byte[] buff = new byte[256];
		in.read(buff, 0, 256); offset += 256; // The name of this attribute.
		mName = new String(buff).trim();

		return offset;
	}
	
	/**
	 * Display a description of the record.
	 */
	public void dump() {
		System.out.println("=================================");
		System.out.println("              ADR");
		System.out.println("=================================");
		System.out.println("ADRnext: " + mADRnext);
		System.out.println("AgrEDRhead: " + mAgrEDRhead);
		System.out.println("Scope: " + mScope + Constant.getScopeName(mScope));
		System.out.println("Num: " + mNum);
		System.out.println("NgrEntries: " + mNgrEntries);
		System.out.println("MAXgrEntry: " + mMAXgrEntry);
		System.out.println("AzEDRhead: " + mAzEDRhead);
		System.out.println("NzEntries: " + mNzEntries);
		System.out.println("MAXzEntry: " + mMAXzEntry);
		System.out.println("Name: " + mName);
		System.out.println("");
	}	
}
