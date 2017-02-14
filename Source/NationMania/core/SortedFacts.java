package NationMania.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import NationMania.ui.GameScreen;
import NationMania.ui.Main;

/**
 * Class for dividing nation facts to buckets based on fact weight for better randomization<br>
 * Each bin contains a different range of difficulties.<br>
 * The bins are ordered from the hardest facts to the easiest<br>
 * The randomization of every fact is divided to 2 steps:<br>
 * 1. Randomizing bin - number of bin is chosen from the "area" (+- 1) of the current bin.<br>
 * 2. Randomizing fact - inside the chosen bin, we randomize a number between 0 and the total number of facts in that bin.<br>
 * The fact in the randomized index is returned.<br>
 * 	 */

public class SortedFacts {
	
	@SuppressWarnings("serial")
	public static class List extends ArrayList<Fact>{}
	
	/**
	 * Configuration Constant - number of buckets to distribute the facts to<br>
	 * This value is 8 by default. 
	 */
	private static int NUM_BINS = Config.GET_VAR_INT("NUM_BINS") == -1 ? 8 : Config.GET_VAR_INT("NUM_BINS");
	
	/**
	 * Configuration Constant - number of facts relations that allowed to be displayed every round.<br>
	 * This number is limited in order to maximise game experience and variety.<br>
	 * This value is 2 by default. 
	 */
	private static int MAX_RELATIONS = Config.GET_VAR_INT("MAX_RELATIONS") == -1 ? 2 : Config.GET_VAR_INT("MAX_RELATIONS");
	
	/**
	 *  Configuration Constant - setting threshold for minimum number of reports needed for fact in order for it<br>
	 *  not to display anymore.<br>
	 *  This value is 1 by default. 
	 */
	public static int MIN_REPORTED = Config.GET_VAR_INT("MIN_REPORTED") == -1 ? 1 : Config.GET_VAR_INT("MIN_REPORTED");

	private HashMap<String,Integer> types;			//don't allow more than MAX_RELATIONS types of facts to appear in one round
	private List[] bins; 							//divide state facts list to bins - for better randomization
	private int current;							//the current bin - goes from the hardest to the easiest (every next clue is easier than the one before)
	private int easyFactsIndex;						//indexes >= easyFactsIndex holds the 50 easiest facts on the country
	private int easyFactsSize;						//total number of easy facts left
	private double maxWeight;						//fact with the maximum weight - determines the weight range of every bin
	private int randomBin;							//the last bin the was randomized

	/**
	 * Constructor
	 */
	public SortedFacts() {
		initBinsLists();
		setStartingBin();
		types = new HashMap<String,Integer>();
	}
	
	/**
	 * Called from Constructor<br>
	 * Initialize all the bins
	 */
	private void initBinsLists() {
		bins = new List[NUM_BINS];
		for (int i =0; i < NUM_BINS; i++)
			bins[i] = new List();
	}
	
	/**
	 * Called from Constructor<br>
	 * Sets the number of the bin which the initial facts will be randomize from.<br>
	 * The number is set according to the game difficulty - the harder the difficulty the lower the number
	 */
	private void setStartingBin() {
		float difficultyRatio = (float) (Game.getDifficulty() / (float) Game.MAX_DIFFICULTY);
		current =  NUM_BINS - (Game.MAX_DIFFICULTY - Game.getDifficulty()) - (int) (difficultyRatio * NUM_BINS);
	}
	
	/**
	 * Gets from DB the highest difficulty from all the facts of the randomized nation.<br>
	 * This is needded in order to calculate the difficulty ranges of every bin (they are all relative to the highest difficulty)<br>
	 * @param maxWeight the maximum weight of fact from all the nation facts
	 */
	public void setMaxWeight(double maxWeight) {
		this.maxWeight = maxWeight;
	}
	
	/**Sets the number of "easy facts" this nation has.<br>
	 * The number is set to a quarter of the total number of facts the nation has.<br>
	 * The easy fact parameter is set in case the current bin is the highest bin (easiest facts), and the bin is empty.<br>
	 * @param size estimated total number of facts the nation has in the DB
	 */
	public void setEasyFactsSize(int size) {
		easyFactsSize = size / 4;
	}

	/**Get facts from DB, add them to list, sort, and create their type in the types map<br>
	 * @param fact the fact created from the values presented in the current row in the DB
	 */
	public void addFact(Fact fact) {
		putFactInBin(fact);
		types.put(fact.getRelation(), 0);
	}
	
	/**Called every time new fact is added.<br>
	 * Calculates the bin this facts belongs to, according to it's weight and the maximum weight.<br>
	 * Adds fact to relevant bin and promotes total size<br>
	 * @param fact sent from "addFact" function
	 */
	private void putFactInBin(Fact fact) {
		double bin = maxWeight / (NUM_BINS - 1);
		int index = (int) (fact.getWeight() / bin);
		bins[index].add(fact);
	}
	
	/**Called on 2 situations:<br>
	 * 1. Fact already displayed to this player in previous games<br>
	 * 2. Fact relation already displayed more times than the maximum allowd set in the MAX_RELATIONS parameter.<br>
	 * Fact is deleted from list to make sure it will not be randomized again<br>
	 * @param fact the fact that was randomized
	 */
	private void deleteFact(Fact fact) {
		bins[randomBin].remove(fact);
	}
	
	
	/**Called on the start of evert round, and every time the user asks for a new fact.<br>
	 * General Process:<br>
	 * 1. Randomizes bin and fact inside the bin.<br>
	 * 2. While fact is invalid - randomize again<br>
	 * 3. Delete fact from the bin and update it on game and player history<br>
	 * 4. For every second fact displayed to the user - increase the number of randomizing bin by 1<br>
	 * @return the valid fact that was randomized
	 */
	public Fact getFact() {
		
		Fact fact = randomizeFact();
		
		//while fact is invalid - randomize again
		while (fact.isInPlayerHistory() || types.get(fact.getRelation()) >= MAX_RELATIONS) {
			deleteFact(fact);		
			fact = randomizeFact();
		}
		
		//update fact as played
		markUsed(fact);
		
		//randomize from easier facts every second clue
		if ((GameScreen.hints + 1) % 2 == 0)
			current++;
		
		return fact;
	}
	
	/**Called from "getFact" function.<br>
	 * Deletes fact from the bin, and update it in all the history data structures:<br>
	 * 1. Round Relations<br>
	 * 2. Game History<br>
	 * 3. Player History<br>
	 * @param fact the fact that was randomized
	 */
	private void markUsed(Fact fact) {

		// delete fact from list so it will not appear again
		deleteFact(fact);
		
		//update history
		types.put(fact.getRelation(),types.get(fact.getRelation()) + 1);	
		Game.updateFactInGameHistory(fact.getID());
		Main.executorService.execute(Player.updatePlayerFactsHistory(fact.getID()));
	}
	
	
	
	/**Randomizing fact in two levels - randomize bin from the buckets, randomize fact inside the bin<br>
	 * In case we are in the last bin and it's empty, we move the current bin pointer to points to the easy facts index.<br>
	 * The easy facts index is the nubmer of bin, from which all the facts inside it and inside the easier bins are consider<br>
	 * easy facts.<br>
	 * @return the Fact that was randomized
	 */
	private Fact randomizeFact() {
		//first level - random bin first
		randomBin = randomizeBin();
	
		while (this.bins[randomBin].size() == 0) {
			//if the highet bin is empty and there are still easy facts left - go to the start of the easy facts
			if (current == NUM_BINS - 1 && easyFactsSize > 0)
				current = easyFactsIndex;
			else
				current = (current + 1) % NUM_BINS; //if reached to the end and no easy facts left - roll back to the start
			randomBin = randomizeBin();
		}
		if (randomBin >= easyFactsIndex)
			easyFactsSize--;

		//second level - randomize fact inside the bin
		int factIndex = new Random().nextInt(bins[randomBin].size());
		return bins[randomBin].get(factIndex);
	}
	
	/**Uses the pointer to the number of the current bin, in order to randomize a number between current - 1 and current + 1.<br>
	 * This is done to make the game less deterministic.<br>
	 * @return the bin number that was randomized by the function "secureRandom"
	 */
	private int randomizeBin() {
		return secureRandom(current - 1,current + 1) ;
	}
	

	/**	Special function that randomize a number from a range, including lower limit and upper limit.<br>
	 * In case the current bin is already at the last bin (easiest facts), no randomization need - stay in the last bin until<br>
	 *  it's empty or until the player cannot ask for more facts.<br>
	 *  The function is fault proof - it makes no assumption and check if both numbers are non negative and which is the maximum.<br>
	 * @param a first number
	 * @param b second number
	 * @return a number in the range between the small number to the high number, or NUM_BINS - 1 if the high number equals
	 * NUM_BINS or higher.
	 * If either number is negative, it sets it to 0.
	 */
	private int secureRandom(int a, int b) {
		//make sure values are not negative
		int max = Math.max(Math.max(a,b),0);
		int min = Math.max(Math.min(a,b),0);
		
		return max >= NUM_BINS ? NUM_BINS - 1 : (new Random().nextInt(max - min + 1) + min);
	}
}
