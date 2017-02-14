package NationMania.parsing;

/**
 *This is a data structure that holds a parsed fact.<br>
 *It contains the 4 fields of a given row<br>
 *And data fields as well <br>
 *It extends <code>YagoEntry</code>
 */
public class YagoEntryFact extends YagoEntry {
	
	public String factName;
	public String factType;
	public int weight;
	public String nation;
	
	public YagoEntryFact(String id, String leftEntity, String relation,
			String rightEntity) {
		super(id, leftEntity, relation, rightEntity);
	}

	/**
	 * Constructor
	 * @param id yago entry id
	 * @param leftEntity the Subject entity
	 * @param relation the relation between them
	 * @param rightEntity the Predicate entity
	 * @param factName the fact name - yago entity or literal
	 * @param factType the fact type - yago entity or literal
	 * @param weight the fact weight - calculated as the times a fact is an entry in YagoFacts
	 * @param nation the nation which the fact relates to - yago entity
	 */
	public YagoEntryFact(String id, String leftEntity, String relation,String rightEntity, String factName, String factType, int weight, String nation) {
			
		super(id, leftEntity, relation, rightEntity);

		this.factName = factName;
		this.factType = factType;
		this.weight = weight;
		this.nation = nation;
	}
}
