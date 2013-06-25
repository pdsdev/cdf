package pds.cdf;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * CDF Descriptor Record (CDR).
 * The CDR contains general information about the CDF. 
 * Defined in Section 2.2 of CDF specification.
 * 
 * @author tking
 *
 */
public class CDRecord extends Record {

	long mGDROffset;
	int	mVersion;
	int	mRelease;
	int mEncoding;
	int mFlags;
	int mIncrement;
	String	mCopyright;
	
	/**
	 * Create a CDRecord based on a base record.
	 * 
	 * @param base the base record to derive a CDRecord.
	 */
	public CDRecord(Record base) {
		mimic(base);
	}
	
	/**
	 * Read the contents of a CDRecord from an input stream.
	 * 
	 * @param offset the current byte offset into the stream
	 * @param in the input stream to read from.
	 * 
	 * @return the byte offset after reading the record.
	 * 
	 * @throws IOException if any reading error occurs.
	 */
	public long read(long offset, DataInputStream in) throws IOException {
		mGDROffset = in.readLong(); offset += 8;
		mVersion = in.readInt(); offset += 4;
		mRelease = in.readInt(); offset += 4;
		mEncoding = in.readInt(); offset += 4;
		mFlags = in.readInt(); offset += 4;
		in.readInt(); offset += 4;	// Skip rfuA
		in.readInt(); offset += 4;	// Skip rfuB
		mIncrement = in.readInt(); offset += 4;
		in.readInt(); offset += 4;	// Skip rfuD
		in.readInt(); offset += 4;	// Skip rfuE
		
		// We're at byte 44 in the file.
		// We've read 36 bytes of CDR and 8 bytes of magic numbers. 
		// Skip to start of first GDR
		byte[] buff = new byte[1945];
		int len = 256;
		in.read(buff, 0, len); offset += len;
		mCopyright = new String(buff).trim();
		
		return offset;
	}

	/**
	 * Display a description of the record.
	 */
	public void dump() {
		System.out.println("=================================");
		System.out.println("              CDR");
		System.out.println("=================================");
		System.out.println("GDR Offset: " + mGDROffset);
		System.out.println("Version: " + mVersion);
		System.out.println("Release: " + mRelease);
		System.out.println("Encoding: " + mEncoding + " [" + Constant.getEncodingName(mEncoding) + "]");
		System.out.println("Flags: " + Integer.toBinaryString(mFlags));
		System.out.println("Increment: " + mIncrement);
		System.out.println("Copyright: " + mCopyright);
		System.out.println("");
	}

	/**
	 * Retrieve the offset of the GD Record in the CDF file.
	 * 
	 * @return the offset to the GDRecord.
	 */
	public long getGDROffset() { return mGDROffset; }
	
	/**
	 * Retrieve the version number of the CDF file.
	 * 
	 * @return the version number in the CDF file.
	 */
	public int	getVersion() { return mVersion; }
	
	/**
	 * Retrieve the release number declared in the CDF file.
	 * 
	 * @return the release number.
	 */
	public int	getRelease() { return mRelease; }
	
	/**
	 * Retrieve the encoding token for the CDF file.
	 * 
	 * @return the encoding token.
	 */
	public int getEncoding() { return mEncoding; }
	
	/**
	 * Retrieve the flags set in the CDF file.
	 * 
	 * @return the flags.
	 */
	public int getFlags() { return mFlags; }
	
	/**
	 * Retrieve the increment number declared in the CDF file.
	 * 
	 * @return the increment number.
	 */
	public int getIncrement() { return mIncrement; }
	
	/**
	 * Retrieve the copyright statement declared in the CDF file.
	 * 
	 * @return the copyright statement.
	 */
	public String getCopyright() { return mCopyright; }

}
