package NationMania.core;

import NationMania.db.Queries;

/**
 * Class containing all data for current round (one round = one state)<br>
 * The data is only valid for the current game with the current player
 * 	 */
public final class Round {
	
	/**
	 * Configuration Constant - the score which the player starts with every new round<br>
	 * this value is multiplied by a constant according to the game difficulty.
	 */
	public static int STARTING_SCORE =  Config.GET_VAR_INT("STARTING_SCORE") == -1 ? 100 : Config.GET_VAR_INT("STARTING_SCORE");
	
	/**
	 * Configuration Constant - determines the number of points reduced when player is asking for the first addition clue
	 */
	public static int STARTING_CLUE_PENALTY =  Config.GET_VAR_INT("STARTING_CLUE_PENALTY") == -1 ? 10 : Config.GET_VAR_INT("STARTING_CLUE_PENALTY");
	
	
	/**
	 * Configuration Constant - number of clues the player get for free every new round
	 */
	public static int NUM_STARTING_CLUES = Config.GET_VAR_INT("NUM_STARTING_CLUES") == -1 ? 2 : Config.GET_VAR_INT("NUM_STARTING_CLUES");
	
	
	private static int score;							
	private static boolean success;					
	private static int penalty;									
	
	
	/**
	 * Called every new round.<br>
	 * Resets the round parameters to their initial values
	 */
	public static void reset() {
		//increase starting score according to game difficulty
		score = (int) (STARTING_SCORE *  ((float) (1 + Game.getDifficulty()) / 2));
		success = false;
		penalty = STARTING_CLUE_PENALTY;
	}


	/**
	 * @return round current score left for the player to gain
	 */
	public static int getScore() {
		return score;
	}
	
	/**
	 * @return number of points reduced to the player on the next fact he requests.
	 */
	public static int getPenalty() {
		return penalty;
	}
	
	/**Increase the penalty every time a new fact is displayed<br>
	 * @param penalty number of points reduced to the player for displaying last fact.
	 * The function is not called when the player asks for the continent.
	 */
	public static void setPenalty(int penalty) {
		Round.penalty = penalty;
	}
	
	/**
	 * @return <code> true </code> if the player guessed the right nation, meaning the name in the choose box macthes the name
	 * of the current randomized nation.
	 */
	public static boolean isSuccess() {
		return success;
	}
	
	/**Called every time the player submit a guess.<br>
	 * Sets the success parameter according to if the guess was correct
	 * @param success <code> true </code> if the player guessed correctly, and <code> false </code> otherwise.
	 */
	public static void setSuccess(boolean success) {
		Round.success = success;
	}
	
	
	/**Called after player makes a valid report - update fact on DB that has been reported.<br>
	 * If fact was reported more than a certain threshold, it will not be displayed anymore.<br>
	 * This action not valid for continent facts.<br>
	 * @param factID the ID of the reported fact
	 */
	public static void reportFact(int factID) {
			Queries.updateFactsReported(factID);
	}
	
	/**Called after player requests a new fact or continent, and reduces the number of points left for this round.<br>
	 * If the player requests a fact - the penalty is 2 points more than the last fact.<br>
	 * If the player requests a continent - the penalty is set to quater of the remaining round points.<br>
	 * 
	 * @param penalty number of points reduced to the player
	 */
	public static void reducePoints(int penalty) {
		score -= penalty;
	}
	
	/**
	 * Called only if the player succeeded to find the hidden nation.<br>
	 * Updates the total score with the score the player earned this round, and increase the nubmer of states discovered.
	 */
	public static void nationFound() {
		Game.increaseStatesDiscovered();
		Game.updateTotalScore(score);
	}
}
