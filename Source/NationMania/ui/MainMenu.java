package NationMania.ui;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import NationMania.core.Game;
import NationMania.core.Nations;

/**
 * Main Menu Screen - allows user to choose activity - enter new game, change difficulty, update account, etc.
 * 	 */

public final class MainMenu {
	/**
	 * parent panel for main menu components (added to card layout).
	 * 
	 * 	 */
	public static JPanel panel;
	/**
	 * the background image label
	 * 
	 * 	 */
	private Label background;
	/**
	 * the logo
	 * 
	 * 	 */
	private Label LogoLabel;
	/**
	 * the label displayed to say hi to the user
	 * 
	 * 	 */
	public static Label welcomeUser;
	/**
	 * the label used to let user know of any errors that occur
	 * 
	 * 	 */
	public static Label errorMsg;
	/**
	 * a button for the user to get their NationMania on
	 * 
	 * 	 */
	private Button newGame;
	/**
	 * a button for a new user who wants to beat the old user under his own name
	 * 
	 * 	 */
	private Button logOut;
	/**
	 * a button for the user to go to the settings screen
	 * 
	 * 	 */
	private Button settings;
	/**
	 * a button for the user to checks the high scores
	 * 
	 * 	 */
	private Button highScore;
	/**
	 * a button for a new user to see how to play
	 * 
	 * 	 */
	private Button gameInstructions;
	/**
	 * a button for quitters
	 * 
	 * 	 */
	private Button quit;
	/**
	 * constructor - responsible for building components 
	 * 
	 * 	 */
	public MainMenu() {
		panel = new JPanel();
		panel.setLayout(null);
		setButtons();
		setLabels();
		Main.frame.getContentPane().add(panel,"MainMenu");
		panel.setVisible(false);
	}
	/**
	 * makes all the buttons
	 * 
	 * 	 */
	private void setButtons() {
		
		newGame = new Button("newGameButton","New Game",159,183,497,23);
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel.setVisible(false);
				GameScreen.panel.setVisible(true);
				GameScreen.toggleEnable(false);
				setNewGame();
			}
		});
		panel.add(newGame);

		logOut = new Button("logOutButton","Log Out",159,333,497,23);
		logOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SignUp.isEdit = false;
				panel.setVisible(false);
				LoginMenu.panel.setVisible(true);
			}
		});
		panel.add(logOut);
		
		settings = new Button("settingsButton","Settings",159,213,497,23);
		settings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel.setVisible(false);
				Settings.panel.setVisible(true);
			}
		});
		panel.add(settings);
		
		highScore = new Button("highScore","High Score",159,243,497,23);
		highScore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HighScore.setTopScores();
				panel.setVisible(false);
				HighScore.panel.setVisible(true);
			}
		});
		panel.add(highScore);
		
		gameInstructions = new Button("gameInstructions","Game Instructions",159,273,497,23);
		gameInstructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.setVisible(false);
				GameInstructions.panel.setVisible(true);
			}
		});
		panel.add(gameInstructions);
		
		quit = new Button("Quit","Quit",159,363,497,23);
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.closeAllresourcesAndQuit();
			}
		});
		panel.add(quit);
	}
	/**
	 * makes all the labels
	 * 
	 * 	 */
	private void setLabels() {
				
		welcomeUser = new Label("welcomeUser","Hi User!",100,134,450,45,0,null,GameFonts.TITLE,Color.ORANGE);
		panel.add(welcomeUser);
		
		errorMsg = Main.setScreenErrorMsg();
		panel.add(errorMsg);
		LogoLabel = Main.setScreenLogoLabel();
		panel.add(LogoLabel);
		background = Main.setScreenBackground();
		panel.add(background);
	}
	/**
	 * Prepares and goes to the game screen
	 * 
	 * 	 */
	public static void setNewGame () {
		
		//if difficulty has not changed - don't need to create map again
		if (Settings.getGameLastDifficulty() != Game.getDifficulty())
			Main.executorService.execute(Nations.updateStatesWeight());
			
		Main.executorService.execute(GameScreen.initGame());
		GameScreen.panel.setEnabled(false);
		GameScreen.tryAgain.setVisible(false);
		GameScreen.mainMenu.setVisible(false);
		GameScreen.start.setVisible(true);
	}
}
