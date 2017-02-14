package NationMania.ui;
import java.awt.Color;

import NationMania.core.Game;

/**
 * Game Timer - initiated every time a new game is starting. counts down the time left for the current game<br>
 * After game is finished - log game information to DB
 * 	 */

	public final class GameTimer {
		
		public static boolean quit;
		/**
		 * used when a user action requires the time to be stopped early
		 * 
		 */
		public static boolean stopTimer = false;
		/**
		 * 
		 * draws to progress bar and ends game
		 * @return Runnable - use to start new thread via the Executer.
		 */
		public static Runnable startTimer() {
			return new Runnable() {
				public void run() {
					
					for(int i = Game.GAME_TIME_IN_SECONDS; i >= 0; i--) {
						
						GameScreen.progressBar.setValue(i);
						GameScreen.progressBar.setString(timeDisplay(i));
						GameScreen.progressBar.setStringPainted(true);
						if(i < 20) GameScreen.progressBar.setForeground(Color.red);
						
						try {
							if(stopTimer) {
								break;
							}
							Thread.sleep(1000);
							if(stopTimer) {
								break;
							}
						}
						catch(Exception e){}
					}
					Game.logGameStats();
					
					for(int i = 0; i < GameScreen.MAX_NUM_FACTS; i++) GameScreen.Facts[i].setVisible(false);
	
					if (GameScreen.progressBar.getValue() == 0) {
						GameScreen.toggleEnable(false);
						GameScreen.tryAgain.setVisible(true);
						GameScreen.mainMenu.setVisible(true);
					}
					stopTimer = false;
				}
			};
		}
		/**
		 * prepares progress bar for a new game
		 * 
		 */
		public static void initTimer() {
			while (stopTimer);		//if last game timer still has not stopped - wait
			
			stopTimer = quit = false;
			GameScreen.progressBar.setForeground(Color.blue);
			GameScreen.progressBar.setValue(Game.GAME_TIME_IN_SECONDS);
			GameScreen.progressBar.setString(timeDisplay(Game.GAME_TIME_IN_SECONDS));
			GameScreen.progressBar.setStringPainted(true);
		}
		/**
		 * @param i - number of seconds (int)
		 * @return a string to display (min:sec)
		 */
		private static String timeDisplay(int i) {
			String minutes,seconds;
			minutes = "" + i / 60;
			seconds = (i % 60 < 10) ? "0" : "";
			seconds += i % 60;
			return minutes + ":" + seconds;
		}
	}
