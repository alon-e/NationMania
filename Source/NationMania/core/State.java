package NationMania.core;

import java.sql.SQLException;

import NationMania.db.Queries;
import NationMania.ui.GameScreen;

/**
 * Class that holds basic information for every state.<br>
 * If the State has been randomized during a game, it contains all it's related facts from the DB, ordered and divided to buckets
 * 	 */
public class State {
	
	private int ID;								//Unique ID for each State
	private String name;						//Country name
	private double weight;						//weight determinate the probability to be chosen in the random
	private double probability;					//probability of state to be randomized (combination of weight and game difficulty)
	private String continent;					//the continent name this state is in
	private SortedFacts stateFacts;				//facts related to this states, sorted to bins according to fact weight
	
	/**Constructor
	 * @param ID nation unique ID
	 * @param name nation caption
	 * @param weight nation weight
	 * @param continent continent name
	 */
	public State(int ID, String name, double weight,String continent) {
		this.ID = ID;
		this.name = name;
		this.weight = weight;
		this.continent = continent;
		Nations.getAllStates().add(this);
	}

	/**
	 * @return state ID as appear in DB
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @return state name as appear in DB
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return state weight as appear in DB. 
	 * The weight uses in calculation of the probability the state will be randomized
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @return state probabilitiy to be randomized
	 * Ther probability is a combination of the state weight and the game difficulty
	 */
	public double getProbability() {
		return probability;
	}

	/**Sets the probability of the state
	 * @param probability value calculated base on the state weight and game difficulty
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	/**Called from function "setStateFacts".
	 * Initiallize the state facts list
	 */
	private void initStateFacts() {
		stateFacts = new SortedFacts();
	}
	
	/**
	 * Called every time a state is randomized.<br>
	 * Gets all facts that belongs to this state from the DB, while sorting them into buckets according to their difficulty.<br>
	 * In case accessing the DB failed (if connection is lost or table is damaged), prints an error message to the user<br>
	 * and quits the game.
	 */
	public void setStateFacts() {
		initStateFacts();
		try {
			Queries.getStateFacts(this);
		} catch (SQLException e) {
			GameScreen.quitWithError();
		}
	}

	/**
	 * @return the orderd facts list of this state
	 */
	public SortedFacts getStateFacts() {
		return stateFacts;
	}

	/**Called when player ask for the "get continent" clue
	 * @return the continent name which this state belongs to.
	 */
	public String getContinent() {
		return continent;
	}
	
	
	/**Called after randomizing a state.
	 * @return <code> true </code> if this state has already been played in the current game.
	 */
	public boolean isAlreadyChosen() {
		return Game.getGameNationsAndFacts().containsKey(getID());
	}
		
	/**Called at the end of every round, except the last one (last round is the one when the time ran out, or the player quitted<br>
	 * the game.<br>
	 * Update the game information in DB by adding this state ID, and the ID of all it's facts that displayed this round.<br>
	 * This is done be a new thread, in order to not disturb the flow of the game.
	 * @return Runnable - use to start new thread via the Executer.
	 */
	public Runnable logState() {
		return new Runnable() {
			public void run() {
				Queries.logState(getID());
			}
		};	
	}
}
