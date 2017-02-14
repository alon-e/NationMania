package NationMania.parsing;
//Implemented with heritage - general entry, then Nations, Facts, etc - have more fields.



/**
 *This is a data structure that holds an Yago entry.<br>
 *It contains the 4 fields of a given row<br>
 *
 *It is the base for <code>YagoEntryFact, YagoEntryNation</code>
 */
public class YagoEntry {
	String id;
	String leftEntity;
	public String relation;
	String rightEntity;

	
	
	/**
	 * Constructor
	 * @param id yago entry id
	 * @param leftEntity the Subject entity
	 * @param relation the relation between them
	 * @param rightEntity  the Predicate entity
	 */
	public YagoEntry(String id, String leftEntity, String relation,
			String rightEntity) {
		super();
		this.id = id;
		this.leftEntity = leftEntity;
		this.relation = relation;
		this.rightEntity = rightEntity;
	}
	
}
