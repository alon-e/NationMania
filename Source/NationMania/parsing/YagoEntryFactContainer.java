package NationMania.parsing;
import java.util.HashMap;

import NationMania.core.Config;


/**
 * This class Contains all the facts found.<br>
 * Updates the facts' types.<br>
 * Removes facts under a certain popularity threshold<br>
 * and Relations that where waived by user.
 */
public class YagoEntryFactContainer {
	
	/**
	 * Maximum number of entries to retrieve.
	 */
	private static final int NUM_ENTRIES =  (Config.GET_VAR_INT("NUM_ENTRIES")!= -1 ? Config.GET_VAR_INT("NUM_ENTRIES") : 500000) ;
	/**
	 * Minimum popularity of entries on which to retrieve.
	 */
	private static final int WEIGHT_THRESHOLD = (Config.GET_VAR_INT("WEIGHT_THRESHOLD")!= -1 ? Config.GET_VAR_INT("WEIGHT_THRESHOLD") : 0);
	int facts_i;
	private static HashMap<String, Integer> nations;
	public static HashMap<String, String> factsHash = new HashMap<String, String>();
	
	YagoEntryFact[] facts = new YagoEntryFact[NUM_ENTRIES];
	
	/**
	 * Constructor
	 */
	public YagoEntryFactContainer() {
		super();
		this.facts_i = 0;
	}
	
	/**
	 * Return the amount of facts in the container
	 * @return amount of facts collected
	 */
	public int getSize(){
		return facts_i;
	}
	
	
	
	
	/**
	 * This method collects facts on nations <br>
	 * by deciding the Subject and Predicate, <br>
	 * popularity and waiver files.
	 * 
	 * @param ye_arr an array of yago entries where a nation (entity) is either the Subject or Predicate
	 * 
	 */
	public void appendData(YagoEntry[] ye_arr) {
		
		int i = 0;
		if (facts_i>=NUM_ENTRIES) { return;}
		String releventData;
		String nation;
		while (i < ye_arr.length) {
			if(ye_arr[i] == null) {
				break;
			}
			if(ye_arr[i].leftEntity.equals("")) {
				break;
			}
			//allow nations to be in both left & right
			if (nations.containsKey(ye_arr[i].leftEntity)) {
				releventData = ye_arr[i].rightEntity;
				nation = ye_arr[i].leftEntity;
				factsHash.put(releventData, "");
				
					
			} else{
				releventData = ye_arr[i].leftEntity;
				nation = ye_arr[i].rightEntity;
				factsHash.put(releventData, "");
			}
			int weight = -1;
			if (YagoParser.factsFileHash.containsKey(releventData) && !nations.containsKey(releventData)) {
				//The actual num. of times the relevant data is present in yagoFacts - nations are irrelevant.
				weight = (int) YagoParser.factsFileHash.get(releventData);
				
			}
			//check relevant data goesn't contain the nation's name:
			if (YagoCaption.getCaption(releventData).toLowerCase().contains(YagoCaption.getCaption(nation).toLowerCase()) 
					|| (weight < WEIGHT_THRESHOLD && weight > 0 )
						|| ( YagoParser.waiverWeightHash.containsKey(ye_arr[i].relation) ? (YagoParser.waiverWeightHash.get(ye_arr[i].relation) == 0 ) : false )){
				i++;
				continue;
			}
			
			facts[facts_i] = new YagoEntryFact(ye_arr[i].id, ye_arr[i].leftEntity, ye_arr[i].relation, ye_arr[i].rightEntity, releventData,"", weight,nation);
			i++;
			facts_i++;
		}		
		
	}
	
	
	/**
	 * This method updates facts' types <br>
	 * by mapping a fact to a type<br>
	 * 
	 * @param ye_arr an array of yago entries where a fact (entity) is the Subject
	 */
	public void updateData(YagoEntry[] ye_arr) {
		
		int i = 0;
		if (facts_i>=NUM_ENTRIES) { return;}
		String relevantData2;
		while (i < ye_arr.length) {
			//Hash the new types
			if(ye_arr[i] == null) {
				break;
			}
			if(ye_arr[i].leftEntity.equals("")) {
				break;
			}
			if (factsHash.containsKey(ye_arr[i].leftEntity)) {
				relevantData2 = ye_arr[i].rightEntity;
				factsHash.put(ye_arr[i].leftEntity, relevantData2);
			}
			i++;	
		}

		
		//write the new types
		for (i=0;i<facts_i;i++){
			if (factsHash.containsKey(facts[i].leftEntity)) {
				facts[i].factType = (String) factsHash.get(facts[i].factName);
			}
		}
		
	}

	/**
	 * Sets a list of nations to be used by appendData in decisions
	 * @param nationsList a list on nations concatenated by pipes (originally used for greps)
	 */
	public static void setNations(String nationsList) {
		nations = new HashMap<String, Integer>();
		String[] nl = nationsList.split("\\|");
		for (String nation : nl) {
			nations.put(nation, 1);
		}
	
	}
}


