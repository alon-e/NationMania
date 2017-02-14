package NationMania.parsing;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class Builds captions for yago entities <br>
 * Some from PreferredMeanings files (not yago's)<br>
 * and others by manipulating the strings<br>
 * <br>
 * Used by YagoSQL for entity captions.<br>
 */
public class YagoCaption {
	
	/**
	 * Hold the preferred meaning - caption for an entity<br>
	 * Updates as captions are decided. 
	 */
	static HashMap<String, String> PreferredMeanings= new HashMap<String, String>();
	
	/**
	 * The method called by YagoSQL to get a string's caption
	 * @param name The yago entity or literal
	 * @return A well formated caption
	 */
	public static String getCaption(String name) {
		
		if (name == null) { return null;}
		if (name.equals("")) { return "";}
		if (PreferredMeanings.containsKey(name) ) {
			return preferredMeaningsCaption(name);
		}
		if (name.matches("\"([^\"].*)\".*")) {
			addManualCaption(name,qoutedCaption(name));
			return qoutedCaption(name);
		}
		addManualCaption(name,wordnetWrapCaption(name));
		return wordnetWrapCaption(name);
	}

	/**
	 * Wraps naiveCaption in order to remove wordnet_ prefix on entities
	 * @param name The yago entity or literal
	 * @return naiveCaption(entity with wordnet_)
	 */
	private static String wordnetWrapCaption(String name) {
		Pattern regex = Pattern.compile("<wordnet_(.*)_[0-9].*>");
		Matcher m = regex.matcher(name);
	      if (m.find( )) {
	    	  //remove wordnet
	    	  return naiveCaption("<"+m.group(1)+">");
	      }
	      regex = Pattern.compile("(.*)\\((.*)\\)(.*)");
	      m = regex.matcher(name);
		      if (m.find( )) {
		    	  //remove wordnet
		    	  return naiveCaption(m.group(1)+m.group(3));
		      }  
		return naiveCaption(name);
	}
	
	/**
	 * Removes &lsaquo; , &rsaquo; from string and replaces _ with whitespace<br>
	 * 
	 * @param name &lsaquo;entity_name&rsaquo;
	 * @return entity name
	 */
	private static String naiveCaption(String name) {
		
		return name.replace("<", "").replace(">", "").replace("_", " ");
	}
	
	/**
	 * Removes quotations and extra chars after like eng or ^^ etc. <br>
	 * @param name \"literal\"eng
	 * @return literal
	 */
	private static String qoutedCaption(String name) {
		Pattern regex = Pattern.compile("\"([^\"].*)\".*");
		Matcher m = regex.matcher(name);
	      if (!m.find( )) {
	    	  //Shoulden't happen - matched " "
	    	  return null;
	      }
      
		return m.group(1);
	}
	
	/**
	 * retrieves preferred meaning
	 * @param name a yago entity or literal
	 * @return name's preferred Meaning
	 */
	private static String preferredMeaningsCaption(String name) {
		
		return (String) PreferredMeanings.get(name);
}


	/**
	 * Add a preferred meaning (caption) to a name
	 * @param name yago entity
	 * @param caption a well formated caption
	 */
	private static void addManualCaption(String name,String caption) {
		
		PreferredMeanings.put(name, caption);
	}


	/**
	 * Fills the preferred meaning bank with captions from a csv file
	 * @param waiverpath a .csv file formated as name,caption with a header line
	 * @throws IOException Signals that an I/O exception of some sort has occurred. 
	 */
	public static void setPreferredMeanings(String waiverpath) throws IOException {
		
	
		BufferedReader in = null;
		try {
		    in = new BufferedReader(new FileReader(waiverpath));
		    String line;
		    line = in.readLine(); //The header line
		    while ((line = in.readLine()) != null) {
		    	String[] nl = line.split(",");
		    	if ( nl.length ==2 ) {
		    		PreferredMeanings.put(nl[0], nl[1]);
		    	}
			}
		} finally {
		    if (in != null) {
		        in.close();
		    }
		}
	}



	
}
