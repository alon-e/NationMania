package NationMania.parsing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import NationMania.core.Config;

/**
 * This class parses yago files<br>
 * It retrieves entries <br>
 * <br>
 * It collects Nations, Facts and Types
 *
 */
public class YagoParser {
	/**
	 * Maximum number of entries to retrieve.
	 */
	private static final int NUM_ENTRIES =  (Config.GET_VAR_INT("NUM_ENTRIES")!= -1 ? Config.GET_VAR_INT("NUM_ENTRIES") : 500000) ;
	/**
	 * 
	 * YAGO_FOLDER - a path to YAGO TSV files folder -<br>
	 *		Needs to contain: 
	 *			<code>yagoTypes.tsv , yagoFacts.tsv , yagoDateFacts.tsv &amp; yagoLiteralFacts.tsv</code>
	 *
	 */
	private static final String YAGO_FOLDER = Config.GET_VAR_STR("YAGO_FOLDER");

	private static String nationsForGrep;
	
	private static HashMap<String, Integer> nations;
	private static HashMap<?, ?> factsHash;
	public static HashMap<String, Integer> factsFileHash = new HashMap<String, Integer>();
	public static HashMap<String, Integer> waiverWeightHash = new HashMap<String, Integer>();
	
	
	private static YagoEntry[] readFile(String filePath, String searchTerm,int searchField) throws IOException {
		return readFile(filePath,searchTerm,searchField, NUM_ENTRIES);
	}
	/**reads yago files and collects entries that match searchTerm AND searchField
	 * 
	 * @param filePath a yago file path
	 * @param searchTerm a string to search for, override by "Dosen't Matter"
	 * @param searchField a coded manner to make different comparisons, see cmp  
	 * @param max_find maximum number of entries to retrieve.
	 * @return an array of YagoEntry containing matches
	 * @throws IOException Signals that an I/O exception of some sort has occurred. 
	 */
	private static YagoEntry[] readFile(String filePath, String searchTerm,int searchField, int max_find) throws IOException {
				
		YagoEntry[] data = new YagoEntry[NUM_ENTRIES];
		BufferedReader in = null;
		
		//Used for faster sifting
		int matchSearhTerm = searchTerm.equals("Dosen't Matter") ? 0 : 1;
		//Used to hash facts - for use in weight computation
		int yagoFactFile = filePath.contains("yagoFacts") ? 1 : 0;
		int foundSearhTerm = 0;
		
		//read yago files
		try {
		    in = new BufferedReader(new FileReader(filePath));
		    int i =0;
		    String line;
		    while ((line = in.readLine()) != null) {
		    	
		    	if ( matchSearhTerm==1){
		    	//multiple search terms
		    		foundSearhTerm = 0;
		    		for ( String term : searchTerm.split("\\|")){
		    			if (line.contains(term)){
		    				foundSearhTerm = 1;
		    				break;
		    			}
		    		}
		    		if (foundSearhTerm==0) {continue;}
		    	}
		    	//compare line to entity pattern
		    	YagoEntry ye = parseLine(line);
		    	
		    	if (ye!=null){
		    		//line is an entry
		    		if(yagoFactFile==1){
		    			//hash facts
		    			String key = ye.leftEntity;
		    			if (factsFileHash.containsKey(key)) {
		    				factsFileHash.put(key, (int) factsFileHash.get(key) + 1);
		    			} else {
		    				factsFileHash.put(key, 1);
		    			}
		    		}
		    		if(cmp(searchTerm,ye,searchField)){
		    			if(filterUnwantedChars(ye)){
		    				//add fact to array
			    			data[i] = ye;
			    			if(i>=max_find){
				    			break;
				    		}
			    			if(i%40==0){
			    			YagoUpdate.setStatus2(String.format("found %d Entries\n",i));
			    			}
			    			//return up to NUM_ENTRIES 
			    			if (i<NUM_ENTRIES-1){
			    				i++;
			    			}
			    			
		    			}
		    		}
		    	}
		    }
		    YagoUpdate.setStatus2(String.format("found %d Entries\n",i));
		} finally {
		    if (in != null) {
		        in.close();
		    }
		}
		
		return data;
	}
	
	
	/**
	 * Filter out entries with non alphanumeric or special signs
	 * Used to manage the printable facts
	 * @param ye an yago entry
	 * @return <code>true</code> if left and right fields comply to rule above
	 * 		   <code>false</code> else
	 */
	private static boolean filterUnwantedChars(YagoEntry ye) {
		// Filter out entities with foreign chars
		if(ye.leftEntity.contains("/") || ye.rightEntity.contains("/") ){
			return false;
		}
		Pattern regex = Pattern.compile("[^A-Za-z0-9 ,.!@#$%^&*()_+={}<>;:\"\'\\-]");
		Matcher m = regex.matcher(ye.leftEntity);
	    	if (m.find( )) {
		   return false;
	    	}	
		m = regex.matcher(ye.rightEntity);
		if (m.find( )) {
	      	   return false;
		}
	
		return true;
	}
	/**
	 * Compares entry fields to searchTerm<br>
	 * based on <code>field</code><br>
	 * There are some special coded field values that broaden the comparison<br>
	 * see code comments
	 * @param searchTerm String to match
	 * @param ye yago entry
	 * @param field field to compare or extended comparison in code comments
	 * @return <code>true</code> if entry complies to rule above
	 * 		   <code>false</code> else
	 */
	private static boolean cmp(String searchTerm, YagoEntry ye,int field) {
		//compares entities on different levels
		switch (field) {
			case 1:
				return ye.id.matches(searchTerm);
			case 2:
				return ye.leftEntity.matches(searchTerm);
			case 3:
				return ye.relation.matches(searchTerm);
			case 4:
				return ye.rightEntity.matches(searchTerm);
			
			case 12:
				//compares left entity to nations list
				return (YagoParser.nations.containsKey(ye.leftEntity));
			case 16:
				//compares left & right entity to nations list (2+4 = 6)
				return (YagoParser.nations.containsKey(ye.rightEntity) || YagoParser.nations.containsKey(ye.leftEntity));
			
			case 22:
				//compares left entity to facts list - Used for Entity type finding
				return (YagoParser.factsHash.containsKey(ye.leftEntity));
				//return (this.factsHash.containsKey(ye.leftEntity) && ye.rightEntity.startsWith("<wordnet"));
			case 0:
				//does no comparison - used for debug
				return true;
			default:
				return false;
		}
			
	}
	/**
	 * Parses a line of text to yago's 4 value TAB separated structure
	 * @param line a line of test
	 * @return a parsed yago entry (see YagoEntry) OR null if not a yago entry line.
	 */
	private static YagoEntry parseLine(String line){
		

		      // Pattern of a single entity
		      String pattern = "(.*)\t(.*)\t(.*)\t(.*)\t(.*)";
		      Pattern r = Pattern.compile(pattern);

		      // Try to match line to pattern
		      Matcher m = r.matcher(line);
		      if (m.find( )) {
		    	  return new YagoEntry(m.group(1),m.group(2),m.group(3),m.group(4));
		      } else {
		         //System.out.println("NO MATCH");
		         return null;
		      }
	}
	

	
	/**
	 * Sifts yagoTypes for countries
	 * @return an array of nations &amp; keeps an inner rep. of nations
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public static YagoEntryNation[] getNations() throws IOException{
		String path = YAGO_FOLDER + "yagoTypes.tsv";
		YagoEntry[] ye_arr =  readFile(path,"<wikicat_Countries>",4);
		YagoEntryNation[] yn_arr = YagoEntryNation.collectData(ye_arr);
		nationsForGrep = "<Israel>";
		for (YagoEntryNation yn: yn_arr) {
			if(yn==null) {
				break;
			}
			if(yn.leftEntity.equals("")) {
				break;
			}
			//Used to filter categories
			nationsForGrep+="|"+yn.leftEntity; 
		}	
		return yn_arr;
	}
	
	/**
	 * Sifts yagoFacts, yagoDateFacts, yagoLiteralFacts for facts
	 * @return an array of facts with fact_types &amp; relation_types
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public static YagoEntryFactContainer getAllFacts() throws IOException{
		
		
		
		YagoParser.setNations(nationsForGrep);
		
		YagoEntryFactContainer yfc = new YagoEntryFactContainer();
		YagoEntryFactContainer.setNations(nationsForGrep);
		YagoEntry[] ye_arr;
		
		//yagoFacts
		String path = YAGO_FOLDER + "yagoFacts.tsv";
		ye_arr =  readFile(path,"Dosen't Matter",16);
		YagoUpdate.setProgress(23);
		yfc.appendData(ye_arr);
		YagoUpdate.setProgress(24);

		factsFileHash.clear();
		
		//yagoDateFacts
		path =  YAGO_FOLDER + "yagoDateFacts.tsv";
		ye_arr =  readFile(path,"Dosen't Matter",12);
		YagoUpdate.setProgress(30);
		yfc.appendData(ye_arr);
		YagoUpdate.setProgress(31);

		//yagoLiteralFacts
		path = YAGO_FOLDER + "yagoLiteralFacts.tsv";
		ye_arr =  readFile(path,"Dosen't Matter",12);
		YagoUpdate.setProgress(37);
		yfc.appendData(ye_arr);
		YagoUpdate.setProgress(38);
		
		//getFactTypes
		factsHash = YagoEntryFactContainer.factsHash;
		updateEntityTypes(yfc);
		YagoUpdate.setProgress(60);
		return yfc;
	}
	
	/**Sifts yagoTypes for fact_types
	 * @param yfc Yago Entry Fact Container
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	private static void updateEntityTypes(YagoEntryFactContainer yfc) throws IOException {
		YagoEntry[] ye_arr;
		ye_arr =  readFile(YAGO_FOLDER + "yagoTypes.tsv","<wordnet",22);
		YagoUpdate.setProgress(45);
		yfc.updateData(ye_arr);
		
	}
	
	
	/**
	 * Sets a list of nations to be used by appendData in decisions
	 * @param nationsList a list on nations concatenated by pipes (originally used for greps)
	 */
	private static void setNations(String nationsList) {
		nations = new HashMap<String, Integer>();
		String[] nl = nationsList.split("\\|");
		for (String nation : nl) {
			nations.put(nation, 1);
		}
	
	}
	
	/**sets relation weights
	 * Used to skip facts and give relations weight 
	 * @param waiverpath path to the waiver file
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 */
	public static void setWaiverWeightHash(String waiverpath) throws IOException {
		//sets waiver hash - used for relation type weight
		BufferedReader in = null;
		try {
		    in = new BufferedReader(new FileReader(waiverpath));
		    String line;
		    line = in.readLine(); //The header line
		    while ((line = in.readLine()) != null) {
		    	String[] nl = line.split(",");
		    	if ( nl.length ==2 ) {
		    		waiverWeightHash.put(nl[0], Integer.parseInt(nl[1]));
		    	}
			}
		    
		} finally {
		    if (in != null) {
		        in.close();
		    }
		}
	
	}

}