package pds.cdf;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.HelpFormatter;

/**
 * Determine if the CDF file is compliant with archive requirements.
 * 
 * Checks the CDF file and determines if the file complies with the CDF tools, ISTP/CDF and PDS archiving requirements
 * for structure and content.   
 * 
 * @author tking
 *
 */
public class Check {
	private String mVersion = "0.0.1";
	private String mOverview = "Scan a CDF file and determine if the content is compliant with archive requirements."
							 + " Checks include if the CDF files meets requirements specified for CDF tools, ISTP/CDF"
							 + " and PDS archiving."
							 ;
	private String mAcknowledge = "Development funded by NASA's PDS project at UCLA.";

	private boolean mVerbose = false;

	// create the Options
	Options mAppOptions = new org.apache.commons.cli.Options();

	ArrayList<String> mMessages = new ArrayList<String>();
	
	/**
	 * Create an instance.
	 */
	public Check() 
	{
		mAppOptions.addOption("h", "help", false, "Dispay this text");
		mAppOptions.addOption("v", "verbose", false,
				"Verbose. Show status at each step.");
	}

	/**
	 * Run the tools from the command-line.
	 * 
	 * Use the "-h" option for options and details.
	 * 
	 * @param args command-line arguments.
	 */
	public static void main(String[] args) 
	{
		Check me = new Check();

		CommandLineParser parser = new PosixParser();
		try {
			CommandLine line = parser.parse(me.mAppOptions, args);

			if (line.hasOption("h")) me.showHelp();
			if (line.hasOption("v")) me.mVerbose = true;
			// Process arguments looking for variable context
			if (line.getArgs().length != 1) {
				me.showHelp();
				return;
			}

			for (String name : line.getArgs()) {
				if (me.mVerbose) System.out.println("Processing: " + name);
				me.checkCDF(name);
				
				// Output messages
				if(me.mMessages.isEmpty()) {
					System.out.println("Format is OK.");
				} else {
					System.out.println("Format is non-compliant.");
					System.out.println("Reasons:");
				}
				
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
		System.out.println(getClass().getName() + "; Version: " + mVersion);
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
	 * Parse a CDF file and check for compliance with CDF tools, ISTP/CDF and PDS archiving requirements
	 * for structure and content.
	 * 
	 * @param pathname the file system path and filename to the CDF file.
	 * 
	 * @throws Exception if any non-recoverable errors occur.
	 */
	public void checkCDF(String pathname)
			throws Exception
	{
		DataInputStream in = new DataInputStream(new FileInputStream(pathname));
		String	stat = "";
		
		// CDF
		CDF cdf = new CDF(in);

		if(mVerbose) {
			System.out.println("Version: " + Constant.toHexString(cdf.getVersion()));
			System.out.println("Compression: " + Constant.toHexString(cdf.getCompression()));
		}
		if(cdf.mVersion < 0xCDF30000) {
			mMessages.add("Version is prior to 3.0.");
			return;
		}
		if(cdf.isCompressed()) {
			mMessages.add("File is compressed.");
			return;
		}

		GDRecord gdr = cdf.getGDR();
		if(gdr == null) {
			mMessages.add("Error parsing CDF. There is no GDR record.");
			return;
		}
		if(gdr.mUIRhead != 0L) {
			mMessages.add("There are unused records in the CDF, most likely remenants of deleted or overwritten varaibles or attributes. These must be removed.");
			mMessages.add("Unused record contains: " + cdf.getUIR(gdr.mUIRhead).mSize + " bytes at offset: " + cdf.getUIR(gdr.mUIRhead).mOffset + " bytes.");
			
		}
		if(cdf.hasSparseness()) {
			mMessages.add("There are sparse records in the CDF. These must be removed.");
			
		}

		// Check for CDF tools compliance
		String[] CDFToolsAttributes =  { "FORMAT", "VALIDMIN", "VALIDMAX", "FILLVAL",
				"MONOTON", "SCALEMIN", "SCALEMAX"
				};
		
		// Check for required attributes.
		// From ISTP Spec. http://spdf.gsfc.nasa.gov/istp_guide/istp_guide.html
		String[] ISTPGlobalAttributes =  { "Project", "Source_name", "Discipline", "Data_type",
				"Descriptor", "Data_version", "Logical_file_id", "PI_name", "PI_affiliation",
				"TEXT", "Instrument_type", "Mission_group", "Logical_source", "Logical_source_description"
				};
		
		/*
		String[] ISTPGlobalAttributesOptional = { "Generated_by", "Generation_date", "HTTP_LINK", "LINK_TEXT", "LINK_TITLE",
				"MODS ", "Parents ", "Rules_of_use", "Skeleton_version", "Software_version",
				"Time_resolution", "TITLE", "Validate"};
		*/
		
		stat = "";
		if(mVerbose) {
			System.out.print("Checking Global ISTP attributes:");
			String delim = " ";
			for(String v : ISTPGlobalAttributes) {
				System.out.print(delim + v);
				delim = ", ";
			}
			System.out.println("");
		}
		for(String v : ISTPGlobalAttributes) {
			if(cdf.getGlobalAttribute(v) == null) {
				mMessages.add("Global ISTP required attribute '" + v + "' is missing.");
				stat = "Missing one or more attributes.";
			}
		}
		if(stat.isEmpty()) stat = " OK";
		if(mVerbose)  System.out.println(stat);
		
		
		// Check variables
		// According to ISTP spec.
		// CDF Variable names must begin with a letter and can contain numbers and undercores, but no other special characters.
		// Data is always either Real or Integer type. Data is always time (record) varying, 
		// but can be of any dimensionality. Real or Integer data are always defined as having one element. 

		if(mVerbose) {
			System.out.println("Variables: Checking variable types, attributes, compression and physical storage.");
		}
		
		ArrayList<String> names = cdf.getVariableNames();
		for(String name : names) {
			if( ! Character.isLetter(name.charAt(0)) ) {
				mMessages.add("Variable name '" + name + "' does not start with a letter.");
				stat += " Bad variable name";				
			}
			if( ! name.matches("[a-zA-Z0-9_]+") ) {
				mMessages.add("Variable name '" + name + "' contains special characters.");
				stat += " Bad variable name";				
			}
		}
		if(mVerbose) {
			System.out.print("Checking for CDF Tools attributes:");
			String delim = " ";
			for(String v : CDFToolsAttributes) {
				System.out.print(delim + v);
				delim = ", ";
			}
			System.out.println("");
		}
		stat = "";
		for(String v : CDFToolsAttributes) {
			if(cdf.getVariableAttribute(v) == null) {
				mMessages.add("CDF tool required attribute '" + v + "' is missing.");
				stat = "Missing one or more attributes.";
			}
		}
		if(stat.isEmpty()) stat = " OK";
		if(mVerbose)  System.out.println(stat);

		// Check for ISTP compliance
		String[] ISTPVariableAttributes =  { "CATDESC", "DEPEND_0", "DISPLAY_TYPE", "FIELDNAM",
				"FORM_PTR", "LABELAXIS/LABEL_PTR_i", "UNITS/UNIT_PTR", "VAR_TYPE"
				};
		
		if(mVerbose) {
			System.out.print("Checking for ISTP variable attributes:");
			String delim = " ";
			for(String v : ISTPVariableAttributes) {
				System.out.print(delim + v);
				delim = ", ";
			}
			System.out.println("");
		}
		stat = "";
		for(String v : ISTPVariableAttributes) {
			if(v.contains("/")) continue;	// Skip ones with alternates
			if(cdf.getVariableAttribute(v) == null) {
				mMessages.add("ISTP required attribute '" + v + "' is missing.");
				stat = "Missing one or more attributes.";
			}
		}
		if(stat.isEmpty()) stat = " OK";
		if(mVerbose)  System.out.println(stat);

		if(mVerbose)  System.out.println("Checking physical storage.");

   		VDRecord vdr = cdf.getVDR(cdf.getGDR().mRVDRhead);
		if(vdr != null) {
			mMessages.add("File contains r-Variables. This is not allowed.");			
		}
		
		vdr = cdf.getVDR(cdf.getGDR().mZVDRhead);
		while(vdr != null) {
			if(mVerbose) System.out.print("   " + vdr.mName + ":");
			stat = "";
			
			// Check for compression at the variable level
			if(vdr.mFlags == Constant.FLAG_COMPRESSION) {
				mMessages.add("Variable '" + vdr.mName + "' is compressed.");
				stat += " Compressed";
			}
			
			// Check if virtual
			VXRecord vxr = cdf.getVXR(vdr.mVXRHead);
			if(vxr == null) {	// Not data - must be virtual
				mMessages.add("Variable '" + vdr.mName + "' does not contain any data and may be calculated (virtual).");				
				stat += " Virtual";
			} else {
				if(vxr.mNusedEntries > 1) {
					mMessages.add("Records for variable '" + vdr.mName + "' are fragmented (not contiguous).");
					stat += " Fragmented";
				}
			}
			
			// Check if record variance and data type conforms
			// We do this on the processed variable stack so we can access the attributes
			for(Variable v : cdf.getVariables()) {
				if(v.getAttributeValue("VAR_TYPE").equals("data")) {
					if((v.mFlags & Constant.FLAG_VARIANCE) != Constant.FLAG_VARIANCE ) {
						mMessages.add("Records for variable '" + vdr.mName + "' are not invariant.");
						stat += " Record Variant";			
					}
					switch(v.getDataType()) {
					case Constant.CDF_EPOCH16:
						mMessages.add("Variable '" + vdr.mName + "' has the non-allowed data type of " + Constant.getDataTypeName(v.getDataType()) + ".");
						stat += " Data Type";			
					}
				}
			}
			
			// Check if invariant dimensions
			int vStart = 0;
			if(vdr.mDataType != Constant.CDF_CHAR) {
				vStart = 1;	// Character fields are allowed to have the first dimension to be variant
			}
			if(vdr.mType == Constant.RECORD_ZVDR) {
				for(int i = vStart; i < vdr.mZNumDims; i++) {
					if(vdr.mDimVarys[i] != -1) {
						mMessages.add("Variable '" + vdr.mName + "' is not invariant.");
						stat += " Variant";
					}
				}
			} else {
				for(int i = vStart; i < vdr.mRNumDims; i++) {
					if(vdr.mDimVarys[i] != -1) {
						mMessages.add("Variable '" + vdr.mName + "' is not invariant.");
						stat += " Variant";
					}
				}					
			}

			if(stat.isEmpty()) stat = " OK";
			if(mVerbose)  System.out.println(stat);
			
			vdr = cdf.getVDR(vdr.mVDRnext);
		}
		
		// Print Assessment
		switch(cdf.getCDR().getEncoding()) {
		case 1:	// NETWORK_ENCODING
		case 2: // SUN_ENCODING
		case 5: // SGi_ENCODING
		case 7: // IBMRS_ENCODING
		case 9: // MAC_ENCODING
		case 12: //	NeXT_ENCODING
			if(mVerbose) System.out.println("Encoding is OK.");
			break;
		default:
			mMessages.add("Encoding is not MSB.");
			break;
		}
		
		// Check for disallowed records
		for(Record rec : cdf.mRecordList) {
			switch(rec.getType()) {
			case Constant.RECORD_UIR:	// UIR - Unused
				// Unused records, but may not be referenced - Check of referenced UIR is already done.
				// mMessages.add("There are unused variables in the CDF. These must be removed.");
				break;
			case Constant.RECORD_RVDR: // rVDR - Non-contiguous storage format
				mMessages.add("There are rVariables present. These need to be converted to zVaraibles.");
				break;
			case Constant.RECORD_CCR:	// CCR - Compressed
				mMessages.add("There are compressed records in the CDF.");
				break;
			case Constant.RECORD_CPR:	// CPR - Compressed
				mMessages.add("There are compressed parameters in the CDF.");
				break;
			case Constant.RECORD_SPR:	// SPR - Sparse
				mMessages.add("There are sparse parameters in the CDF.");
				break;
			case Constant.RECORD_CVVR:	// CVVR - Compressed
				mMessages.add("There are compressed variables in the CDF.");
				break;					
			}
		}

		// Scan VXR list
		for(VXRecord vxr : cdf.mVXRList) {
			if(vxr.mVXRnext != 0L) mMessages.add("Variable records are fragmented. A possible cause could be that the records were written incrementally. Re-writing the file may correct the problem.");
		}

				
		in.close();
	}
}
