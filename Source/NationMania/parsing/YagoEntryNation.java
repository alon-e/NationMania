package NationMania.parsing;


/**
 *This is a data structure that holds a parsed nation.<br>
 *It contains the 4 fields of a given row<br>
 *And data fields as well <br>
 *It extends <code>YagoEntry</code>
 */
public class YagoEntryNation extends YagoEntry {
	
	public String nationName;
	public int weight;	
	
	public YagoEntryNation(String id, String leftEntity, String relation,
			String rightEntity) {
		super(id, leftEntity, relation, rightEntity);
	}


	
	/**
	 * Constructor
	 * @param id yago entry id
	 * @param leftEntity the Subject entity
	 * @param relation the relation between them
	 * @param rightEntity the Predicate entity
	 * @param nationName the nation name - yago entity or literal
	 * @param weight the nation weight - calculated as the number of facts a nation has
	 * 
	 */
	public YagoEntryNation(String id, String leftEntity, String relation,String rightEntity, String nationName, int weight) {
			
		super(id, leftEntity, relation, rightEntity);

		this.nationName = nationName;
		this.weight =weight;
	}

	/**
	 * This method collects nations <br>
	 * 
	 * @param ye_arr an array of yago entries where a nation (entity) is the Subject
	 * @return a parsed array
	 */
	public static YagoEntryNation[] collectData(YagoEntry[] ye_arr) {
		
		YagoEntryNation[] yn = new YagoEntryNation[ye_arr.length];
		int i = 0;
		while (i < ye_arr.length) {
			if(ye_arr[i] == null) {
				break;
			}
			if(ye_arr[i].leftEntity.equals("")) {
				break;
			}
			String relevantData = ye_arr[i].leftEntity;
			int weight = 1;
			yn[i] = new YagoEntryNation(ye_arr[i].id, ye_arr[i].leftEntity, ye_arr[i].relation, ye_arr[i].rightEntity, relevantData, weight);
			i++;
		}		
		return yn;
	}

}
