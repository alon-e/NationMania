package NationMania.core;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class for settings all Constant values of the game. <br>
 * The main function reads from Config.ini file into dictionary which
 * is read later in the game
 */


public final class Config {
	private static HashMap<String, String> Config_Table = new HashMap<String, String>();
	private static final String config_file_path = "Config.ini";
	
	/**
	 * Service for the app, gets the const with the given name  
	 * @param Var the name of the const to read from config map
	 * @return the desirable const as integer. return -1 if name don't match any key in map
	 */
	public static int GET_VAR_INT(String Var) { //not exist returns -1
		if (Config_Table.containsKey(Var)) {
			String Value = Config_Table.get(Var);
			return Integer.parseInt(Value);
		}
		else {
			return -1;
		}
	}
	
	/**
	 * Service for the app, gets the const with the given name
	 * @param Var the name of the const to read from config map
	 * @return the desirable const as string. return "NA" if name don't match any key in map
	 */
	public static String GET_VAR_STR(String Var){ //not exist returns "NA"
		if (Config_Table.containsKey(Var)) {
			String Value = Config_Table.get(Var);
			return Value;
		}
		else {
			return "NA";			
		}
	}
	
	/**
	 * Reads the config file into a hash map 'Config_table'
	 * @return 0 if the process succeeded, -1 if failed
	 * @throws IOException signals that an I/O exception of some sort has occurred. mainly throws what ReadFromFile will throw.
	 */
	public static int loadConfig() throws IOException { //returns 0 if succeeded, -1 if failed!
		
		File f = new File(config_file_path);
		if (f.isFile()) {
			ReadFromFile(config_file_path);
			return 0;
		}
		else {
			System.out.println(f.getPath());
			return -1;
		}				
	}
	
	/**
	 * private service for loadConfig() function. 
	 * open file for read, phrase it, and save the values in the hash map.
	 * @param config_file_path is the path for the config file.
	 * @throws IOException signals that an I/O exception of some sort has occurred. Can't open file for read.
	 */
	private static void ReadFromFile(String config_file_path) throws IOException {
		
		BufferedReader in = null;
		try {
		    in = new BufferedReader(new FileReader(config_file_path));
		    String line;
		    line = in.readLine(); //The header line (ignore it)
		    while ((line = in.readLine()) != null) {
			    if (line.length() > 0){
		    		if (line.charAt(0) != '#') { //use '#' for disable line in config file =D
				    	String[] nl = line.split("=");
				    	if ( nl.length == 2 ) { //if it's not 2, it's not config_line 
				    		Config_Table.put(nl[0], nl[1]);
				    	}
			    	}
		    	}
			}
		    
		} finally {
		    if (in != null) {
		        in.close();
		    }
		}
	}
}