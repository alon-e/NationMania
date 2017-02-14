package NationMania.parsing;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import NationMania.core.Config;
import NationMania.db.YagoSQL;
import NationMania.ui.Settings;



/**
 * This class takes care of updating the DB based on Yago TSV files <br>
 * It tries to do so in a safe manner in order to prevent DB corruption<br>
 * <br>
 * Required files are validated &amp; Errors are plotted to the user.<br>
 * <br>
 * <pre>
 * General Process:
 *  1. Check all files are present
 *  2. Validate the sql scripts
 *  3. Check server isn't TAU Server
 *  4. Update
 *  	4.1. Truncate tables
 *  	4.2. Find nations in Yago &amp; import them to DB
 *  	4.3. Find Facts &amp; Types in Yago &amp; import them to DB
 *  	4.4. Run all kinds of queries that manipulate the data to be more useful, see: lib\SQL\AFTER_IMPORT_ALL.sql
 *  
 *</pre>
 */
public class YagoUpdate {
	
	/**
	 * 
	 * YAGO_FOLDER - a path to YAGO TSV files folder -<br>
	 *		Needs to contain: 
	 *			<code>yagoTypes.tsv , yagoFacts.tsv , yagoDateFacts.tsv &amp; yagoLiteralFacts.tsv</code>
	 *
	 */
	private static String YAGO_FOLDER = !(Config.GET_VAR_STR("YAGO_FOLDER").equals("NA")) ? Config.GET_VAR_STR("YAGO_FOLDER") : "ERROR\\";
	/**
	 * 
	 *WAIVERS_FOLDER - contains .csv files used for skipping problematic facts &amp; setting relations' weights
	 *
	 *
	 *
	 */
	private static String WAIVERS_FOLDER = !(Config.GET_VAR_STR("WAIVERS_FOLDER").equals("NA")) ? Config.GET_VAR_STR("WAIVERS_FOLDER") : "lib"+File.separator+"SQL"+File.separator;

	/**
	 * 
	 *SQL_FOLDER - contains .sql files used for Truncating tables &amp; Manipulating the DB <br>
	 *		(files are check for SHA1 checksum to prevent manipulation of queries)
	 *
	 */
	private static String SQL_FOLDER = !(Config.GET_VAR_STR("SQL_FOLDER").equals("NA")) ? Config.GET_VAR_STR("SQL_FOLDER") : "lib"+File.separator+"SQL"+File.separator;	
	
	
	
	/**
	 * <pre>
	 * Updates DB based on Yago.
	 * Plots progress to user via 
	 * setStatus, 
	 * setStatus2, 
	 * setProgress
	 * </pre>
	 * @throws IOException Signals that an I/O exception of some sort has occurred. 
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 */
	public static void update() throws IOException,SQLException {
		setProgress(1);
		
		//waivers - used for caption parsing & weight
		YagoParser.setWaiverWeightHash(WAIVERS_FOLDER+"relation_type_weight.csv");
		YagoCaption.setPreferredMeanings(WAIVERS_FOLDER+"PreferredMeanings.csv");
		setProgress(3);

		YagoSQL sql;
		sql = new YagoSQL();
		
		//make sure the update is local
		if (sql.CheckIfServer()){
			setStatus("ERROR: User unauthorized to update on TAU server! See User Manual");
			setProgress(-1);
			return;
		}
		//Truncate relevant tables
		setStatus("Cleaning Database tables...");
		sql.yagoRunSQLScript(SQL_FOLDER+"TRUNCATE.sql");
		setProgress(6);
		
		//get Nations
		setStatus("Collecting Nations from Yago..."); 
		YagoEntryNation[] data = YagoParser.getNations();
		setStatus("Finished collecting Nations from Yago"); 
		setProgress(9);
		sql.yagoInsertNations(data);
		setStatus("Finished importing Nations to DB"); 
		setProgress(19);

		//get Facts
		setStatus("Collecting Facts from Yago..."); 
		
		YagoEntryFactContainer yfc = YagoParser.getAllFacts();
		setStatus("Finished collecting Facts from Yago"); 
		setProgress(9);
		setStatus2(String.format("%d facts will be imported\n",yfc.getSize()));
		
		sql.yagoInsertFactTypes(yfc.facts);
		setStatus("Finished importing fact types to DB");
		setProgress(65);
		sql.yagoInsertRelationTypes(yfc.facts);
		setStatus("Finished importing relation types to DB");
		setProgress(70);
		
		setStatus("Importing Facts to DB..."); 
		sql.yagoInsertFacts(yfc.facts);
		setStatus("Finished importing Facts to DB"); 
		setProgress(90);

		//manipulate DB
		//Check AFTER_IMPORT_ALL.sql hasn't changed
		try {
			if (!YagoChecksum.check(SQL_FOLDER+"AFTER_IMPORT_ALL.sql").equals("60fb9f59a36632aae656223b2f286c6ef48c1d71")) { 
				setStatus("ERROR: failed checksum: "+ SQL_FOLDER + "AFTER_IMPORT_ALL.sql");
				setProgress(-1);
				return;
				}
		} catch (Exception e) {
			setStatus("ERROR: failed checksum: "+e.getMessage());
			setProgress(-1);
		}
		setStatus("Setting values..."); 
		sql.yagoRunSQLScript(SQL_FOLDER+"AFTER_IMPORT_ALL.sql");
		
		//Finish
		setProgress(100);
		setStatus("Done :)");
	}
	/**
	 * Runs a secure update.<br>
	 * To be called by the Executer.
	 * 
	 * @return Runnable - use to start new thread via the Executer.
	 */
	public static Runnable secureUpdate() {
		return new Runnable() {
			public void run() {				
				setProgress(0);
				if (checkFiles()) {
					try {
						
						update();
					} catch (IOException e) {
						//IOexceptions shouldn't occur based on files missing - so recovering is hard.
						setStatus("ERROR: "+e.getMessage());
						setProgress(-1);
					} catch (SQLException e) {
						//IOexceptions shouldn't occur based on files missing - so recovering is hard.
						setStatus("ERROR: "+e.getMessage());
						setProgress(-1);
					}
				}
			}
		};
	}
	
	/**
	 * Check all the required files are in place &amp; scripts passed checksum
	 * 
	 * @return <code>true</code> if all the required files are in place &amp; scripts passed checksum
	 * 		   <code>false</code> else
	 */
	private static boolean checkFiles() {

		//Yago files
		if (!isPresent(YAGO_FOLDER+"yagoTypes.tsv")) { return false;}
		if (!isPresent(YAGO_FOLDER+"yagoFacts.tsv")) { return false;}
		if (!isPresent(YAGO_FOLDER+"yagoDateFacts.tsv")) { return false;}
		if (!isPresent(YAGO_FOLDER+"yagoLiteralFacts.tsv")) { return false;}
		
		//SQL scripts
		if (!isPresent(SQL_FOLDER+"TRUNCATE.sql")) { return false;}
		if (!isPresent(SQL_FOLDER+"AFTER_IMPORT_ALL.sql")) { return false;}
		
		//Waivers
		if (!isPresent(WAIVERS_FOLDER+"PreferredMeanings.csv")) { return false;}
		if (!isPresent(WAIVERS_FOLDER+"relation_type_weight.csv")) { return false;}
		
		//Checksum of SQL files
		try {
			if (!YagoChecksum.check(SQL_FOLDER+"TRUNCATE.sql").equals("10fa708744da8e3593da3dd372e58c9c8cb4568a")) { 
				setStatus("ERROR: failed checksum: "+ SQL_FOLDER + "TRUNCATE.sql");
				setProgress(-1);
				return false;
				}
		} catch (Exception e) {
			setStatus("ERROR: failed checksum: "+e.getMessage());
			setProgress(-1);
		}
		try {
			if (!YagoChecksum.check(SQL_FOLDER+"AFTER_IMPORT_ALL.sql").equals("60fb9f59a36632aae656223b2f286c6ef48c1d71")) { 
				setStatus("ERROR: failed checksum: "+ SQL_FOLDER + "AFTER_IMPORT_ALL.sql");
				setProgress(-1);
				return false;
				}
		} catch (Exception e) {
			setStatus("ERROR: failed checksum: "+e.getMessage());
			setProgress(-1);
		}
		
		return true;
	}
	
	
	/**
	 * Checks whether <code>path</code> exists &amp; plot a response to user if not
	 * @param path 	a path to a file.
	 * @return <code>true</code> if file exists
	 * 		   <code>false</code> else
	 */
	private static boolean isPresent(String path) {
		//Check if a file is present - reports errors
		File f = new File(path);
		if (!f.exists()) {
			setStatus("ERROR: missing file: " + path);
			setProgress(-1);
			return false;
		}
		return true;
	}
	
	
	
	
	/**
	 * plots <code>msg</code> to user via the main update status bar
	 * @param msg message to show the user
	 */
	public static void setStatus(String msg) {
		Settings.setUpdateStatus(msg);
	}
	/**
	 * plots <code>msg</code> to user via the sub update status bar
	 * @param msg message to show the user
	 */
	public static void setStatus2(String msg) {
		Settings.setUpdateStatus2(msg);
	}
	/**
	 * progresses the update progress bar
	 * @param i 0-100 progress percent
	 */
	public static void setProgress(int i) {
		Settings.setUpdateProgress(i);
	}
}