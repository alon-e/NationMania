package NationMania.core;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.TreeMap;

import NationMania.db.Queries;
import NationMania.ui.Settings;

/**
 * This class containing all data needed for managing a full game, and keeping statistics for saving in the player's history.<br>
 * The fields used for managing are the game difficulty, and the fields next state and next fact.<br>
 * The fields game ID, total score, number of states discovered and the map gameNationsAndFacts that contains all the nations and<br>
 * facts that appeared in the game, uses for keeping statistics which will be considered on later games.<br>
 */
public final class Game {
	
	/**
	 *  Configuration Constant - determines the time in seconds until the game clock runs out
	 */
	public static int GAME_TIME_IN_SECONDS = Config.GET_VAR_INT("GAME_TIME_IN_SECONDS") == -1 ? 180 : Config.GET_VAR_INT("GAME_TIME_IN_SECONDS");
	
	/**
	 *  Configuration Constant - determines the maximum game difficulty available for the user to choose
	 */
	public static int MAX_DIFFICULTY = Config.GET_VAR_INT("MAX_DIFFICULTY") == -1 ? 3 : Config.GET_VAR_INT("MAX_DIFFICULTY");
		
	/**
	 *the game ID in the DB
	 */
	private static int gameID;							
	
	/**
	 * totalScore holds the total score the player achieved during the game.<br>
	 * the total score is the sun of all round scores
	 */
	private static int totalScore;
	
	/**
	 * difficulty the difficulty of the game chosen by the player.<br>
	 * the difficulty is initialized to 1 on application startup, and can be integer values between 1 and 3
	 */
	private static int difficulty = 1;
	
	/**
	 * statesDiscovered number of states guessed during this current game, meaning number of rounds ended successfuly
	 */
	private static int statesDiscovered;
	
	/**
	 * gameNationsAndFacts map of &lsaquo;nation,facts&rsaquo; containing all the states and facts that appeared in the current game
	 */
	private static NavigableMap<Integer, ArrayList<Integer>> gameNationsAndFacts;
	
	/**
	 * nextFact the next fact that will be displayed to the player if he hit the "what the fact" button
	 */
	private static Fact nextFact = null;
	
	/**
	 * nextState the next state that will be displayed to the player if he hit the "pass" button
	 */
	private static State nextState = null;
	
	/**
	 * Constructor<br>
	 * General Process:<br>
	 * 1. reset game statistics like the total score and the number of states discovered.<br>
	 * 2. updating the "last difficulty played" value to this current chosen difficulty<br>
	 * 3. Initializes the game &lsaquo;nation,facts&rsaquo; map <br>
	 */
	
	public Game () {
		totalScore = statesDiscovered = 0;					
		Settings.setGameLastDifficulty(getDifficulty());			
		Queries.logGame();									
		initGameNationsAndFacts();		
	}
	
	/**
	 * @return the ID assigned to the game when it was logged in the DB
	 */
	public static int getGameID() {
		return gameID;
	}
	
	/**Sets the parameter gameID as this game's ID
	 * @param gameID sets the game ID with the value generated in the DB
	 */
	public static void setGameID(int gameID) {
		Game.gameID = gameID;
	}
	
	/**
	 * @return the next fact to be displayed to the player. the fact was already randomized
	 */
	public static Fact getNextFact() {
		return nextFact;
	}
	
	/**Sets the parameter nextFact as this game's next fact
	 * @param nextFact the last fact that was randomized and not have been displayed to the user yet
	 */
	public static void setNextFact(Fact nextFact) {
		Game.nextFact = nextFact;
	}
	
	/**
	 * @return the next state to be the answer for the next round. the state was already randomized
	 */
	public static State getNextState() {
		return nextState;
	}
	
	/**Sets the parameter nextState as this game's next state
	 * @param nextState the state that was randomized.<br> 
	 * The next state is randomizing on the start of every new round by a different thread
	 */
	public static void setNextState(State nextState) {
		Game.nextState = nextState;
	}
	
	/**
	 * @return the sum of all round scores achieved by the player until now.
	 * At the end of the game, this is set as the game final score
	 */
	public static int getTotalScore() {
		return totalScore;
	}
	
	/**
	 * Updates at the end of every round the game total score by adding to it the round score
	 * @param roundScore the score achieved by the player in the last round played
	 */
	public static void updateTotalScore(int roundScore) {
		totalScore += roundScore;
	}
	
	/**
	 * @return the game difficulty - a number between 1 to 3
	 */
	public static int getDifficulty() {
		return difficulty;
	}
	
	/**Sets the game difficulty
	 * @param difficulty - the player's chosen difficulty
	 */
	public static void setDifficulty(int difficulty) {
		Game.difficulty = difficulty;
	}
	
	/**
	 * @return the number of states the player guessed successfuly so far
	 */
	public static int getStatesDiscovered() {
		return statesDiscovered;
	}
	
	/**
	 * Called at the end of a successful round. Increment the number of the game's states discovered parameter.
	 */
	public static void increaseStatesDiscovered() {
		statesDiscovered++;
	}
	
	/**
	 * Called from Constructor.
	 * Initiallize the game history map to new Tree Map
	 */
	private static void initGameNationsAndFacts() {
		gameNationsAndFacts = new TreeMap<Integer,ArrayList<Integer>>();
	}
	
	/**
	 * @return the game history map, which contains all the nations played in this get until now, and their related displayed facts.
	 * Those are saved in order to prevent them to been showed to the player again after a short time
	 */
	public static NavigableMap<Integer,ArrayList<Integer>> getGameNationsAndFacts() {
		return gameNationsAndFacts;
	}
	
	/**
	 * Called immidialty at the start of every round.<br>
	 * Inserts the nation that the current round is about to the game history map.
	 */
	public static void updateNationInGameHistory() {
		getGameNationsAndFacts().put(Nations.getMyLand().getID(), new ArrayList<Integer>());
	}
	
	/**Called immidialty after a fact has been randomized.<br>
	 * Inserts the fact ID to this current round nation's facts list in the game history map.
	 * @param factID the fact ID to be logged into history.
	 */
	
	public static void updateFactInGameHistory(int factID) {
		getGameNationsAndFacts().get(Nations.getMyLand().getID()).add(factID);				
	}

	/**Called at the end of every game from the "logGameStats" function.
	 * @return <code> true </code> if the game total score is higher than the best score the player achieved so far 
	 */
	private static boolean isNewHighScore() {
		return getTotalScore() > Player.getTopScore();
	}
	
	/**
	 * Called at the end of every game.<br>
	 * Update the last nation that was played in the game before it was terminated, and then logs the final game statistics<br>
	 * to the DB, meaning the final total score and number of states discovered.
	 */
	public static void logGameStats() {
		Queries.logGameStats(getGameID());
		
		if (isNewHighScore()) {				
			Queries.updateTopScore();
			Player.setTopScore(getTotalScore());
		}
	}
}
