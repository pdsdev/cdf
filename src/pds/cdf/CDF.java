package pds.cdf;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 * Parse a CDF file and create lists of records, attributes and variables defined in the file. 
 * 
 * The parser is based on the CDF Internal Format specification Version 3.5, September 25, 2012. 
 * This CDF parser differs from the one distributed by the Goddard Space Flight Center (GSFC) in that
 * it is written entirely in Java and tracks byte offset for all data areas. Knowing the byte offsets of the
 * data is essential for creating PDS labels that describe the content of the CDF.
 * 
 * @author tking
 *
 */
public class CDF {
	
	private String mVersionID = "0.0.1";
	private String mOverview = "Parse a CDF file and create lists of records, attributes and variables defined in the file.";
	private String mAcknowledge = "Development funded by NASA's PDS project at UCLA.";
	
	int mVersion = 0;
	int mCompression = 0;
	
	boolean mVerbose = false;
	
	// Stored arguments
	String mPathName = "-stream-";
	
	// Record list
	ArrayList<Record> mRecordList = new ArrayList<Record>();
	CDRecord mCDR = null;
	GDRecord mGDR = null;
	ArrayList<ADRecord> mADRList = new ArrayList<ADRecord>();
	ArrayList<VXRecord> mVXRList = new ArrayList<VXRecord>();
	ArrayList<VVRecord> mVVRList = new ArrayList<VVRecord>();
	ArrayList<VDRecord> mVDRList = new ArrayList<VDRecord>();
	ArrayList<AEDRecord> mAEDRList = new ArrayList<AEDRecord>();

	// Processed items
	ArrayList<Attribute> mAttributes = new ArrayList<Attribute>();
	ArrayList<Variable> mVariables = new ArrayList<Variable>();
	
	VDRecord mCurrentVDR = null;
	
	long mOffset = 0L;
	
	ArrayList<String> mMessages = new ArrayList<String>();


	// create the Options
	Options mAppOptions = new org.apache.commons.cli.Options();
	
	/**
	 * A container of information about a CDF file.
	 */
	public CDF() 
	{
		mAppOptions.addOption("h", "help", false, "Dispay this text");
		mAppOptions.addOption("v", "verbose", false, "Verbose. Show status at each step.");
		mAppOptions.addOption("a", "attributes", false, "Attributes. Show attribute information.");
		mAppOptions.addOption("r", "variables", false, "Variables. Show variable information.");
	}

	/**
	 * Parse and view information about a CDF file from the command line.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) 
	{
		CDF me = new CDF();

		CommandLineParser parser = new PosixParser();
		try {
			boolean showAttributes = false;
			boolean showVariables = false;
			
			CommandLine line = parser.parse(me.mAppOptions, args);

			if (line.hasOption("h")) me.showHelp();
			if (line.hasOption("v")) me.mVerbose = true;
			if (line.hasOption("a")) showAttributes = true;
			if (line.hasOption("r")) showVariables = true;
			
			// Default is to show both attributes and varaibles
			if( ! showAttributes && ! showVariables) {
				showAttributes = true;
				showVariables = true;
			}
			
			// Process arguments looking for variable context
			if (line.getArgs().length != 1) {
				System.out.println("Pass the file to transform as a plain command-line argument.");
				return;
			}

			for (String name : line.getArgs()) {
				if (me.mVerbose) System.out.println("Processing: " + name);
				DataInputStream in = new DataInputStream(new FileInputStream(name));
				CDF cdf = new CDF(in);
				cdf.dump(showAttributes, showVariables);

				for(String message : me.mMessages) {
					System.out.println(message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Display help information.
	 **/
	public void showHelp() 
	{
		System.out.println("");
		System.out.println(getClass().getName() + "; Version: " + mVersionID);
		System.out.println(mOverview);
		System.out.println("");
		System.out.println("Usage: java " + getClass().getName()
				+ " [options] file");
		System.out.println("");
		System.out.println("Options:");

		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(getClass().getName(), mAppOptions);

		System.out.println("");
		System.out.println("Acknowledgements:");
		System.out.println(mAcknowledge);
		System.out.println("");
	}

	/**
	 * Parse a CDF file.
	 * 
	 * @param pathname the file system path and file name to a CDF file.
	 * 
	 * @throws IOException
	 */
	public CDF(String pathname) throws IOException {
		mPathName = pathname;
		DataInputStream in = new DataInputStream(new FileInputStream(pathname));
		parse(in);
		in.close();
	}
	
	/**
	 * Parse a CDF file.
	 * 
	 * @param in pre-opened {@link DataInputStream} to a CDF file.
	 * 
	 * @throws IOException
	 */
	public CDF(DataInputStream in) throws IOException {
		parse(in);
	}
	
	/**
	 * Parse a CDF file.
	 * 
	 * @param in pre-opened {@link DataInputStream} to a CDF file.
	 * 
	 * @throws IOException
	 */
	public void parse(DataInputStream in) throws IOException {
		
		// Magic numbers
		mVersion = in.readInt(); mOffset += 4;
		// if(!Util.isValidVersion(mVersion)) return;

		mCompression = in.readInt();  mOffset += 4;

		try {
			boolean more = true;
			while(more) {
				Record rec = readRecord(in);
				if(mVerbose) rec.dump();
				switch(rec.getType()) {
				case Constant.RECORD_CDR:	// CDR
					mCDR = new CDRecord(rec);
					mOffset = mCDR.read(mOffset, in);
					break;
				case Constant.RECORD_GDR:	// GDR
					mGDR = new GDRecord(rec);
					mOffset = mGDR.read(mOffset, in);
					break;
				case Constant.RECORD_ADR: // ADR
					ADRecord adr = new ADRecord(rec);
					mADRList.add(adr);
					mOffset = adr.read(mOffset, in);
					break;
				case Constant.RECORD_AGREDR: // AgrEDR
				case Constant.RECORD_AZEDR: // AzEDR
					AEDRecord aedr = new AEDRecord(rec);
					mAEDRList.add(aedr);
					mOffset = aedr.read(mOffset, in);
					break;
				case Constant.RECORD_VXR: // VXR
					VXRecord vxr = new VXRecord(rec);
					mVXRList.add(vxr);
					mOffset = vxr.read(mOffset, in);
					break;					
				case Constant.RECORD_VVR: // VVR
					VVRecord vvr = new VVRecord(rec);
					mVVRList.add(vvr);
					mOffset = vvr.read(mOffset, in);
					break;
				case Constant.RECORD_RVDR: // rVDR
					VDRecord rvdr = new VDRecord(rec, mGDR.mRNumDims);
					mCurrentVDR = rvdr;
					mVDRList.add(rvdr);
					mOffset = rvdr.read(mOffset, in);
					break;
				case Constant.RECORD_ZVDR: // zVDR
					VDRecord zvdr = new VDRecord(rec, 0);
					mCurrentVDR = zvdr;
					mVDRList.add(zvdr);
					mOffset = zvdr.read(mOffset, in);
					break;
				default:	// All others
					if(rec.getSize() == 0) { more = false; break; } 	// Something wrong.
					long loffset = rec.getSize() - 12;
					in.skip(loffset); mOffset += loffset;
					break;
				}
			}
			
		} catch(EOFException ex) {
			// System.out.println("EOF at: " + mOffset);
			// Done
		} catch(Exception ex) {
			System.out.println("File does not appear to be a well formed CDF.");
			return;
			// ex.printStackTrace(System.out);
		}
		
		// Define global attributes
		for(ADRecord adr : mADRList) {
			if(adr.mScope == Constant.SCOPE_GLOBAL || adr.mScope == Constant.SCOPE_GLOBAL_ASSUME) {
				Attribute attr = new Attribute();
				mAttributes.add(attr);
				attr.setName(adr.mName);
				
				AEDRecord aedr = null;
				if(adr.mAgrEDRhead != 0) { aedr = getAEDR(adr.mAgrEDRhead); }
				if(adr.mAzEDRhead != 0) { aedr = getAEDR(adr.mAzEDRhead); }
				attr.setDataType(aedr.mDataType);
				attr.setStartByte(aedr.mDataStartByte);
				while(aedr != null) {
					attr.addValues(Constant.valueToArrayList(aedr.mValue, aedr.mDataType, aedr.mNumElems));
					aedr = getAEDR(aedr.mAEDRnext);
				}				
			}
		}	

		// Define Variables
		for(VDRecord vdr : mVDRList) {
			Variable v = new Variable();
			mVariables.add(v);
			v.setName(vdr.mName);
			v.setDataType(vdr.mDataType);
			v.setIndex(vdr.mNum);
			v.setFlags(vdr.mFlags);
			v.setStartByte(getVariableStartByte(vdr));
			v.setRecordCount(vdr.mMaxRec+1);	// Always add 1 - zero referenced
			if(vdr.mType == Constant.RECORD_ZVDR) {
				if(vdr.mZNumDims == 0) {
					int[] d = { 1 };
					v.setDims(d);					
				} else {
					v.setDims(vdr.mZDimSize);
				}
			} else {	// rVariable
				int[] d = { 1 };
				v.setDims(d);
			}
			v.setPadValue(vdr.mPadValue);
		}
		
		// Define variable attributes
		for(ADRecord adr : mADRList) {
			if(adr.mScope == Constant.SCOPE_VARIABLE || adr.mScope == Constant.SCOPE_VARIABLE_ASSUME) {
				Attribute attr = new Attribute();
				attr.setName(adr.mName);
				
				AEDRecord aedr = null;
				if(adr.mAgrEDRhead != 0) { aedr = getAEDR(adr.mAgrEDRhead); }
				if(adr.mAzEDRhead != 0) { aedr = getAEDR(adr.mAzEDRhead); }
				if(aedr == null) continue;	// No values - probably virtual
				
				attr.setDataType(aedr.mDataType);
				attr.setStartByte(aedr.mDataStartByte);
				
				int listSize = adr.mMAXzEntry + 1;	// Always one more
				if(adr.mMAXgrEntry > listSize) listSize = adr.mMAXgrEntry;
				if(adr.mNzEntries > listSize) listSize = adr.mNzEntries;
				if(adr.mNgrEntries > listSize) listSize = adr.mNgrEntries;
				
				ArrayList<String> values = new ArrayList<String>(listSize);
				for(int i = 0; i < listSize; i++) values.add("");
				
				while(aedr != null) {	// Element number corresponds to variable number
					mVariables.get(aedr.mNum).setAttribute(adr.mName, Constant.valueToArrayList(aedr.mValue, aedr.mDataType, aedr.mNumElems));
					aedr = getAEDR(aedr.mAEDRnext);
				}			
			}
		}	
		
	}

	
	/**
	 * Read a CDF record from the input stream.
	 * 
	 * @param in	A pre-opened (@link DataInputStream}.
	 * 
	 * @return	a {@link Record} containing the information parsed from the input stream.
	 *  
	 * @throws IOException
	 */
	public Record readRecord(DataInputStream in) throws IOException {
		Record rec = new Record(mOffset);
		mRecordList.add(rec);
		mOffset = rec.read(in);
		
		return rec;
	}

	/**
	 * Retrieve a {@link ADRecord} with the given index.
	 * 
	 * @param index the file offset as defined in the CDF file.
	 * 
	 * @return the corresponding {@link ADRecord} or null if none found.
	 */
	public ADRecord getADR(long index) {
		ADRecord adr = null;
		
		for(ADRecord a : mADRList) {	// Scan list of ADR for matching index
			if(a.mOffset == index) adr = a; 
		}
		
		return adr;
	}
	
	/**
	 * Retrieve a {@link AEDRecord} with the given index.
	 * 
	 * @param index the file offset as defined in the CDF file.
	 * 
	 * @return the corresponding {@link AEDRecord} or null if none found.
	 */	
	public AEDRecord getAEDR(long index) {
		AEDRecord aedr = null;
		
		for(AEDRecord a : mAEDRList) {	// Scan list of ADR for matching index
			if(a.mOffset == index) aedr = a; 
		}
		
		return aedr;
	}
	
	/**
	 * Retrieve a VDRecord with the given index.
	 * 
	 * @param index the file offset as defined in the CDF file.
	 * 
	 * @return the corresponding {@link VDRecord} or null if none found.
	 */	
	public VDRecord getVDR(long index) {
		VDRecord vdr = null;
		
		for(VDRecord a : mVDRList) {	// Scan list of ADR for matching index
			if(a.mOffset == index) vdr = a; 
		}
		
		return vdr;
	}
	
	/**
	 * Retrieve a VXRecord with the given index.
	 * 
	 * @param index the file offset as defined in the CDF file.
	 * 
	 * @return the corresponding {@link VXRecord} or null if none found.
	 */	
	public VXRecord getVXR(long index) {
		VXRecord vxr = null;
		
		for(VXRecord a : mVXRList) {	// Scan list of ADR for matching index
			if(a.mOffset == index) vxr = a; 
		}
		
		return vxr;
	}
	
	/**
	 * Retrieve a VXRecord with the given index.
	 * 
	 * @param index the file offset as defined in the CDF file.
	 * 
	 * @return the corresponding {@link VVRecord} or null if none found.
	 */	
	public VVRecord getVVR(long index) {
		VVRecord vvr = null;
		
		for(VVRecord a : mVVRList) {	// Scan list of ADR for matching index
			if(a.mOffset == index) vvr = a; 
		}
		
		return vvr;
	}
	
	/**
	 * Retrieve all values for an attribute given the attribute name.
	 * 
	 * @param name the attribute name.
	 * 
	 * @return ArraList<String> of values or an empty list if the attribute is not found. 
	 */
	public ArrayList<String> getAttributeValues(String name) {
		ArrayList<String> values = new ArrayList<String>();
		
		ADRecord adr = getADR(mGDR.mADRhead);
		while(adr != null) {
			if(adr.mName.equals(name)) { values = getAttributeValues(adr); break; }
			adr = getADR(adr.mADRnext);
		}
		
		return values;
	}

	/**
	 * Retrieve all values for an attribute.
	 * 
	 * @param adr The ADRecord for this attribute
	 * 
	 * @return ArraList<String> of values or an empty list if the attribute is not found. 
	 */
	public ArrayList<String> getAttributeValues(ADRecord adr) {
		ArrayList<String> values = new ArrayList<String>();
		
		AEDRecord aedr = getAEDR(adr.mAgrEDRhead);
		while(aedr != null) {
			values.add(aedr.valueToString());
			aedr = getAEDR(aedr.mAEDRnext);
		}	
		aedr = getAEDR(adr.mAzEDRhead);
		while(aedr != null) {
			values.add(aedr.valueToString());
			aedr = getAEDR(aedr.mAEDRnext);
		}	
		
		return values;
	}

	/**
	 * Retrieve a list of all global attribute names.
	 * 
	 * @return {@link ArrayList} containing names of all global attributes.
	 */
	public ArrayList<String> getGlobalAttributeNames()
	{
		ArrayList<String> values = new ArrayList<String>();
		
		ADRecord adr = getADR(mGDR.mADRhead);
		while(adr != null) {
			if(adr.mScope == Constant.SCOPE_GLOBAL || adr.mScope == Constant.SCOPE_GLOBAL_ASSUME) {
				values.add(adr.mName);
			}
			adr = getADR(adr.mADRnext);
		}
		
		return values;
	}

	/**
	 * Retrieve the ADRecord for the global attribute with the given name.
	 * 
	 * @param name the attribute name.
	 * 
	 * @return the {@link ADRecord} associated with the name or null if no attribute exists.
	 */
	public ADRecord getGlobalAttribute(String name)
	{
		for(ADRecord adr : mADRList) {	// Scan list of ADR for matching index
			if(adr.mScope == Constant.SCOPE_GLOBAL || adr.mScope == Constant.SCOPE_GLOBAL_ASSUME) {
				if(adr.mName.equals(name)) return adr;
			}
		}
		
		return null;
	}

	/**
	 * Retrieve a list of all variable attribute names.
	 * 
	 * Note: In a CDF a variable attribute is a sparse array, not all variables will have all attributes.
	 *  
	 * @return {@link ArrayList} containing names of all variable attributes.
	 */
	public ArrayList<String> getVariableAttributeNames()
	{
		ArrayList<String> values = new ArrayList<String>();
		
		ADRecord adr = getADR(mGDR.mADRhead);
		while(adr != null) {
			if(adr.mScope == Constant.SCOPE_VARIABLE || adr.mScope == Constant.SCOPE_VARIABLE_ASSUME) {
				values.add(adr.mName);
			}
			adr = getADR(adr.mADRnext);
		}
		
		return values;
	}

	/**
	 * Retrieve the {@link ADRecord} for the variable attribute with the given name.
	 * 
	 * @return the {@link ADRecord} associated with the name or null if no attribute exists.
	 */
	public ADRecord getVariableAttribute(String name)
	{
		for(ADRecord adr : mADRList) {	// Scan list of ADR for matching index
			if(adr.mScope == Constant.SCOPE_VARIABLE || adr.mScope == Constant.SCOPE_VARIABLE_ASSUME) {
				if(adr.mName.equals(name)) return adr;
			}
		}
		
		return null;
	}


	/**
	 * Retrieve a list of all variable names.
	 * 
	 * @return an {@link ArrayList} of names.
	 */
	public ArrayList<String> getVariableNames()
	{
		ArrayList<String> values = new ArrayList<String>();
		
		// scan rVDR
		VDRecord vdr = getVDR(mGDR.mRVDRhead);
		while(vdr != null) {
			values.add(vdr.mName);
			vdr = getVDR(vdr.mVDRnext);
		}
		
		// scan zVDR
		vdr = getVDR(mGDR.mZVDRhead);
		while(vdr != null) {
			values.add(vdr.mName);
			vdr = getVDR(vdr.mVDRnext);
		}

		return values;
	}


	/**
	 * Retrieve the {@link VDRecord} for the variable with the given name.
	 * 
	 * @param name the name associated with the variable.
	 * 
	 * @return the {@link VDRecord} associated with the name or null if no variable exists.
	 */
	public VDRecord getVariable(String name) {
		VDRecord vdr = null;
		
		for(VDRecord v : mVDRList) {	// Scan list of ADR for matching index
			if(v.mName.equals(name)) { vdr = v; break; } 
		}
		
		return vdr;
	}
	
	/** 
	 * Retrieve the start byte of the data for a variable with a given name.
	 * 
	 * @return the start byte of the variable data or zero if the variable does not exist.
	 */
	public long getVariableStartByte(String name) {
		VDRecord vdr = getVariable(name);
		
		return getVariableStartByte(vdr);
	}
	
	/** 
	 * Retrieve the start byte of the data for a variable using the {@link VDRecord} for the variable.
	 * 
	 * @return the start byte of the variable data or zero if the variable does not exist.
	 */
	public long getVariableStartByte(VDRecord vdr) {
		long startByte = 0L;
		
		VXRecord vxr = getVXR(vdr.mVXRHead);
		
		if(vxr == null) return startByte;
		
		if(vxr.mNentries > 0 && vxr.mNusedEntries > 0) {	// We use just the first one for start byte
				VVRecord vvr = getVVR(vxr.mVVRList[0]);
				startByte = vvr.mDataStartByte;
		} 
		return startByte;
	}

 	/** 
     * Generate a nested HashMap for the CDF file that includes file information.
     * The {@link HashMap} is a keyword, value map. Each OBJECT is stored
     * in a ArrayList with a HashMap of the object. The value assigned
     * to the OBJECT keyword is mapped to "OBJECT_NAME" in the {@link HashMap}.
     * The first file referenced by an object pointer is assigned to "PRODUCT_FILE"
     * and the MD5 checksum for the file is assigned to "PRODUCT_MD5".
     *
     * @since           1.0
     */
	public HashMap<String, Object> getHashMap() {
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("attributes", mAttributes);
		map.put("variables", mVariables);
		map.put("data", getDataVariables());
		
		CDRecord cdr = getCDR();
		map.put("version", "" + Constant.toHexString(cdr.mVersion));
		map.put("release", "" + cdr.mRelease);
		map.put("increment", "" + cdr.mIncrement);
		map.put("encoding", Constant.getEncodingName(cdr.mEncoding));
		map.put("copyright", cdr.mCopyright);
		
		map.put("pathName", mPathName);
		
		map.put("fileMD5", "");
		if( ! mPathName.equals("-stream-")) {	// if pathname defined
			try {
				map.put("fileMD5", igpp.util.Digest.digestFile(mPathName));
			} catch(Exception e) {
				// Do nothing
			}
		}
		return map;
	}

	/**
	 * Display a description of each attribute or variable.
	 * 
	 * @param showAttributes if true show attributes, otherwise do not.
	 * @param showVariables if true show variables, otherwise do not.
	 */
	public void dump(boolean showAttributes, boolean showVariables)
	{
		System.out.println("Version: " + Constant.toHexString(getVersion()));
		System.out.println("Compression: " + Constant.toHexString(getCompression()));
	
		if(showAttributes) {
			System.out.println("==================================");
			System.out.println("            Global Attributes");
			System.out.println("==================================");
			System.out.println("Attrribute count: " + getAttributes().size());
			for(Attribute a : getAttributes()) {
				a.dump();
			}
		}
		
		if(showVariables) {
			System.out.println("==================================");
			System.out.println("            Variables");
			System.out.println("==================================");
			System.out.println("Variable count: " + getVariableNames().size());
			for(Variable v : getVariables()) {
				v.dump();
			}
			System.out.println("==================================");
			System.out.println("            Data Variables");
			System.out.println("==================================");
			for(Variable v : getDataVariables()) {
				v.dump();
			}
		}
	}

	/**
	 * Determine if any data is sparse.
	 *  
	 * @return true if there is sparse data, otherwise false.
	 */
	public boolean hasSparseness()
	{
		for(Record r : mRecordList) {
			switch(r.mType) {
			case Constant.RECORD_SPR: return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieve the version number declared in the CDF.
	 * 
	 * @return the version number declared in the CDF.
	 */
	public int getVersion() { return mVersion; }
	
	/**
	 * Retrieve the compression value declared in the CDF.
	 * 
	 * @return the compression information for the CDF.
	 */
	public int getCompression() { return mCompression; }
	
	/**
	 * Determine if the CDF is compressed.
	 * 
	 * @return true if the CDF is compressed at the file level, otherwise false.
	 */
	public boolean isCompressed() { return Constant.isCompressed(mCompression); }
	
	/**
	 * Set the offset value used maintained during the parsing of the CDF file.
	 * 
	 * @param offset the offset in bytes.
	 */
	public void setOffset(long offset) { mOffset = offset; }
	
	/**
	 * Retrieve the current offset value.
	 * 
	 * @return the current offset value.
	 */
	public long getOffset() { return mOffset; }
	
	/**
	 * Retrieve the current pathname value.
	 * 
	 * @return the current pathname value.
	 */
	public String getPathName() { return mPathName; }
	
	/**
	 * Retrieve the {@link CDRecord} for the CDF.
	 * @return the {@link CDRecord} for the CDF.
	 */
	public CDRecord getCDR() { return mCDR; }
	
	/**
	 * Retrieve the {@link GDRecord} for the CDF.
	 * 
	 * @return the {@link GDRecord} for the CDF.
	 */
	public GDRecord getGDR() { return mGDR; }
	
	/**
	 * Retrieve the description of each global attribute defined in the CDF.
	 * 
	 * @return the {@link ArrayList} of {@link Attribute} defined in the CDF.
	 */
	public ArrayList<Attribute> getAttributes() { return mAttributes; }

	/**
	 * Retrieve the description of a global attribute with the given name.
	 * 
	 * @param name	the name of the attribute
	 * 
	 * @return the {@link Attribute} with the matching name or NULL if none found.
	 */
	public Attribute getAttribute(String name) {
		for(Attribute att : mAttributes) {
			if(att.mName.equals(name)) return att;
		}
		
		return null;
	}
	

	/**
	 * Retrieve the value of a global attribute with the given name. 
	 * If the value is multi-value use ", " as the delimiter. 
	 * 
	 * @param name	the name of the attribute
	 * 
	 * @return the value of the matching attribute or an empty string if no attribute is found.
	 * */
	public String getAttributeValue(String name) {
		return getAttributeValue(name, ", ");
	}
	
	/**
	 * Retrieve the value of a global attribute with the given name. 
	 * If the value is multi-value placed the delim string between each value. 
	 * 
	 * @param name	the name of the attribute
	 * @param delim	the delimiter string to place between values.
	 * 
	 * @return the value of the matching attribute or an empty string if no attribute is found.
	 * */
	public String getAttributeValue(String name, String delim) {
		for(Attribute att : mAttributes) {
			if(att.mName.equals(name)) return att.getValueString(delim);
		}
		
		return "";
	}
	

	/**
	 * Retrieve the description of each variable defined in the CDF.
	 * 
	 * @return the {@link ArrayList} of {@link Variable} defined in the CDF.
	 */
	public ArrayList<Variable> getVariables() { return mVariables; }
	
	/**
	 * Retrieve the description of each variable which has the VAR_TYPE attribute of "data".
	 * 
	 * An ISTP compliant CDF defines a VAR_TYPE attribute to indicated the role of the variable.
	 * If VAR_TYPE attribute is not defined the variable is considered data.
	 * 
	 * @return the {@link ArrayList} of {@link Variable} defined in the CDF.
	 */
	public ArrayList<Variable> getDataVariables() {
		ArrayList<Variable> variables = new ArrayList<Variable>();
		for(Variable v : mVariables) {
			String type = v.getAttributeValue("VAR_TYPE").trim();	// Sometimes has leading/trailing spaces.
			if(type.isEmpty() || type.equals("data")) variables.add(v);
		}
		return variables; 
	}
	
	/**
	 * Retrieve a list of all records defined in the CDF.
	 * 
	 * @return the {@link ArrayList} of {@link Variable} defined in the CDF.
	 */
	public ArrayList<Record> getRecordList() { return mRecordList; }
	
	public String getDataTypePDS(int dataType) {
		return Constant.getDataTypePDS(dataType);
	}
}
