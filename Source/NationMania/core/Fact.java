package NationMania.core;

/**
 *This is a data structure that holds relevant information for the game about all the facts of the current and next randomized state.<br>
 *It contains a unique ID, weight parameter to determine it's difficulty, and name, type and relation, <br>
 *which combined together builds the fact string. <br>
 */
public class Fact {
	
	private int ID;				
	private String name;		
	private String type;		
	private String relation;	
	private double weight;		 

	
	/**Constructor
	 * @param ID unique ID to distinguish fact
	 * @param name fact name on DB - represent who/what this fact is about
	 * @param type fact type on db - represent the being of the fact
	 * @param relation fact relation on DB - represent the relation between the fact and the country it's about
	 * @param weight fact weight on DB - represent the difficulty of the fact (how much it tells about the nation)
	 */
	public Fact(int ID,String name, String type, String relation, double weight) {
		this.ID = ID;
		this.name = name;
		this.type = type;
		this.relation = relation;
		this.weight = weight;
	}

	/**
	 * @return the fact unique ID which singulate it from other facts in the DB
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @return the value of this fact - meaning the person,building,place etc this fact is about
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the weight of the fact which determines the bin it will fall into on the bucket sort preformed on the state's facts list
	 */
	public double getWeight() {
		return weight;
	}
	
	/**Checks if this fact has already been displayed to the current player recenlty.
	 * Recent time is set as a constant in the Config file. The default value is 1 week.
	 * @return <code>true</code> if this fact ID was found in the player facts history
	 */
	public boolean isInPlayerHistory() {
		return Player.getPlayedFacts().contains(getID());
	}

	/**
	 * @return the type of this fact. For example: "actor"
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the relation between the fact and the nation it's about. For example "is located in"
	 */
	public String getRelation() {
		return relation;
	}	
}
