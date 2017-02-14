package NationMania.core;
import java.sql.SQLException;
import java.util.*;

import NationMania.db.Queries;
import NationMania.ui.Main;

/**
 * Static class holds list of all the states in the DB, and the functions related to their randomization algorithm.<br>
 * The nations list will be filled only on startup, unless a DB update will be preformed.
 */

public final class Nations {
	
	/**
	 * list of all the states exist in the DB, orderd from the hardest state to the easiest.
	 * State difficulty is set according to the number of facts it's related to in the DB.
	 */
	private static ArrayList<State> allStates = new ArrayList<State>();
		
	/**
	 * the current played nation
	 */
	private static State myLand;
	
	/**
	 * sum of all the state's probabilities. uses to random state from the list
	 */
	private static double totalProbability;	
	
	/**
	 * map of &lsaquo;probabilty,state&rsaquo; used to implement fast randomization of a state from the list.<br>
	 * each state set with the value of the sum of all probabilities of all the states that are before it in the list. <br>
	 */
	private static NavigableMap<Double,State> weightsMap;											
	
	/**
	 * @return the list containing all states
	 */
	public static ArrayList<State> getAllStates() {
		return allStates;
	}
	
	/**	Called on application startup.<br>
	 *  Loads all the states from the DB into allStates list.<br>
	 * @throws SQLException If Connection to DB failed, or accessing the nations table in the DB failed
	 */
	public static void setAllStates() throws SQLException {
		Queries.getCountries();
	}

	/**
	 * @return the current played nation
	 */
	public static State getMyLand() {
		return myLand;
	}
	
	
	/**
	 * Updates the current round nation and logs this nation in the game and player history
	 * @param randomizedLand the nation that was already randomized and set in the last round, except for the first round 
	 */
	private static void setMyLand(State randomizedLand) {
		myLand = randomizedLand;
		Game.updateNationInGameHistory();
		Main.executorService.execute(Player.updatePlayerNationsHistory(getMyLand().getID()));
	}
	
	/**
	 * Called at the start of every round.<br>
	 * In the first round, it randomizes once for the current round, and again on a different thread for the next round<br>
	 * In every other round, it sets the nation that was randomized in the previous round as the current nation, and randomizes<br>
	 * again for the next round.
	 */
	public static void randNation() {
		
		//if first round - need to randomize twice
		if (Game.getNextState() == null) 
			randFirstState();
		else
			setMyLand(Game.getNextState());		//next state already randomized - set as current

		//randomize on different thread the next state
		Main.executorService.execute(randNextState());
	}

	/**
	 * Called once every game in the first round, since this is the only time the next nation is not already set.<br>
	 * Randomizes a nation from the list, and get it's facts list from the DB.
	 */
	public static void randFirstState() {
		//randomize state
		setMyLand(randomizeState());
			
		//get state facts
		getMyLand().setStateFacts();
	}
	
	/**This function is identical to "randFirstState", except for the fact it saves the nation the was randomized in the<br>
	 * "game next fact" parameter.<br>
	 * This function is preformed by a different thread, to not disturb the flow of the game.
	 * @return Runnuble - use to start new thread via the Executer.
	 */
	public static Runnable randNextState() {
		return new Runnable() {		
			public void run() {
				//randomize next state
				Game.setNextState(randomizeState());
				
				//get next state facts
				Game.getNextState().setStateFacts();
			}
		};
	}
		
	/**Function for randomizing state from the states list.<br>
	 * The function randomizing a number between 1 to the sum of all state's probabilities, and returns the nation with the closest<br>
	 * value to the randomized number, from all values that are larger then it.<br>
	 * The function randomizing states, until it gets a state that was not played in this game already.
	 * @return the randomized state
	 */
	private static State randomizeState() {
	
		//if all nations already been played at this game - start over
		if (weightsMap.isEmpty())
			updateStatesWeight().run();
		
		double random = Math.random() * totalProbability;
		
		State state = weightsMap.ceilingEntry(random).getValue();
		while (state.isAlreadyChosen()) {	
			random = Math.random() * totalProbability;
			state = weightsMap.ceilingEntry(random).getValue();
		}

        return state;
	}
	

	/**
	 * This function is called on 3 situations:<br>
	 * 1. when user is logging in<br>
	 * 2. when all the nations already played in this game (very unlikely to happend)<br>
	 * 3. when the user start a new game with a different difficulty from the last game played<br>
	 * <br>
	 * The function computes the dynamic probability of every nation to be randomized during the game, based on it's weight<br>
	 * and the game difficulty.<br>
	 * For every difficulty, the function chooses a different range of states (easy,medium,hard) and multiply their weight,<br>
	 * in order to increase their probability to be chosen.<br>
	 * The function is done be a new thread.<br>
	 * @return Runnable - use to start new thread via the Executer.
	 */
	public static Runnable updateStatesWeight () {
		return new Runnable() {		
			public void run() {
				
				float difficultyRatio = (float) Game.getDifficulty() / (float) Game.MAX_DIFFICULTY;
				
				//Initialize the weights map and total probability
				weightsMap = new TreeMap<Double,State>();
				totalProbability = 0;
				
				//take the relevant part of nations from the list (easy,medium,hard) and multiple their weight according to
				//chosen game difficulty!		
				int lowerLimit = (int) (allStates.size() * (1 - difficultyRatio));
				int upperLimit = lowerLimit + (int) ((float) allStates.size() / (float) Game.MAX_DIFFICULTY);
						
				for (int i = 0; i < allStates.size(); i++) {
					State state = allStates.get(i);
					
					//if state in the desired range - increase it's probability
					//else - sets it's probability to be it's weight
					if (i >= lowerLimit && i <= upperLimit)
						state.setProbability(state.getWeight() * Game.getDifficulty() * Game.MAX_DIFFICULTY);
					else
						state.setProbability(state.getWeight());
			
					totalProbability += state.getProbability();
					weightsMap.put(totalProbability,state);
				}
			}
		};
	}
}
	
