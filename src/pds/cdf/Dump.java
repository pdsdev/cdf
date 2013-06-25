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
 * Parses a CDF file and displays detailed information about the internal structure and contents of the file.
 * 
 * @author tking
 *
 */
public class Dump {
	private String mVersion = "0.0.1";
	private String mOverview = "Scan a CDF file show the data block information.";
	private String mAcknowledge = "Development funded by NASA's PDS project at UCLA.";

	private boolean mVerbose = false;

	// create the Options
	Options mAppOptions = new org.apache.commons.cli.Options();

	/**
	 * Create an instance.
	 */
	public Dump() 
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
		Dump me = new Dump();

		CommandLineParser parser = new PosixParser();
		try {
			CommandLine line = parser.parse(me.mAppOptions, args);

			if (line.hasOption("h")) me.showHelp();
			if (line.hasOption("v")) me.mVerbose = true;
			// Process arguments looking for variable context
			if (line.getArgs().length != 1) {
				System.out
						.println("Pass the file to transform as a plain command-line argument.");
				return;
			}

			for (String name : line.getArgs()) {
				if (me.mVerbose) System.out.println("Processing: " + name);
				me.scanCDF(name);
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
	 * Parse a CDF file and display detailed information about the internal structure and contents of the file.
	 * 
	 * @param pathname the file system path and filename to the CDF file.
	 * 
	 * @throws Exception if any non-recoverable errors occur.
	 */
	public void scanCDF(String pathname)
			throws Exception
	{
		DataInputStream in = new DataInputStream(new FileInputStream(pathname));
		
		// CDF
		CDF cdf = new CDF(in);
		System.out.println("Version: " + Constant.toHexString(cdf.getVersion()));
		System.out.println("Compression: " + Constant.toHexString(cdf.getCompression()));

		cdf.getCDR().dump();
		cdf.getGDR().dump();
	
		ADRecord adr = cdf.getADR(cdf.getGDR().mADRhead);
		while(adr != null) {
			adr.dump();
			ArrayList<String> values = cdf.getAttributeValues(adr);
			for(String v : values) System.out.println("Value: " + v);
			adr = cdf.getADR(adr.mADRnext);
		}
	
		VDRecord vdr = cdf.getVDR(cdf.getGDR().mRVDRhead);
		while(vdr != null) {
			vdr.dump(cdf);
			vdr = cdf.getVDR(vdr.mVDRnext);
		}
		
		vdr = cdf.getVDR(cdf.getGDR().mZVDRhead);
		while(vdr != null) {
			vdr.dump(cdf);
			vdr = cdf.getVDR(vdr.mVDRnext);
		}
		
		System.out.println("");
		System.out.println("Attributes:");
		ArrayList<Attribute> attr = cdf.getAttributes();
		for(Attribute a : attr) {
			a.dump();
		}

		System.out.println("");
		System.out.println("Variables:");
		ArrayList<Variable> var = cdf.getVariables();
		for(Variable v : var) {
			v.dump();
		}
		
		in.close();
	}
}
