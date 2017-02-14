package NationMania.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import NationMania.core.Fact;
import NationMania.core.Game;
import NationMania.core.Nations;
import NationMania.core.Round;
import NationMania.db.Queries;

/**
 * Game Screen - holds all the information needed during a game. calls and manages the java core functions with threads
 * 
 * 	 */

public final class GameScreen {
	
	/**
	 * used to initialize the fact array
	 * 
	 * 	 */
	public static int MAX_NUM_FACTS = 16;
	/**
	 * parent panel for gamescreen components (added to card layout).
	 * 
	 * 	 */
	public static JPanel panel;
	/**
	 * field where the player can enter his answer (combo box with a list of nation names).
	 * 
	 * 	 */
	private static JComboBox<String> nationsGuess;
	/**
	 * the background image label
	 * 
	 * 	 */
	private Label background;
	/**
	 * the logo
	 * 
	 * 	 */
	private Label smallLogo;
	/**
	 * label showing the current game total score
	 * 
	 * 	 */
	private static Label totalScore;
	/**
	 * label showing the number of nations already guessed correctly for the current game
	 * 
	 * 	 */
	private static Label countriesDiscovered;
	/**
	 * label showing how many points the player can earn if he guesses correctly.
	 * 
	 * 	 */
	private static Label roundScore;
	/**
	 * label for "Report Fact" area title
	 * 
	 * 	 */
	private static Label reportFact;
	/**
	 * label for leting user know about errors
	 * 
	 * 	 */
	private static Label errorMsg;
	/**
	 * label commending user for success
	 * 
	 * 	 */
	private static Label validMsg;
	/**
	 * another label to communicate with the user
	 * 
	 * 	 */
	private static Label submitMsg;
	/**
	 * the list of facts to be showen
	 * 
	 * 	 */
	public static Label[] Facts;
	/**
	 * a text area where the user can speciffy the index of the fact they want to report
	 * 
	 * 	 */
	private static JTextField reportedFact;
	/**
	 * ***Nation Mania fun fact!*** the famous "WhatTheFact" button was originaly named "HitMe"
	 * the designers felt it was outdated and it was changed by the second beta version.***
	 * 
	 * button the user can press to reveal the next fact
	 * 
	 * 	 */
	private static Button hitMe;
	/**
	 * the button the covers the screen after pressing "New Game", allows the user time to prepare before the timer is started
	 * ***looks like luggage***
	 * 	 */
	public static Button start;
	/**
	 * one of the two buttons showen imediately after the timer finishes.
	 * allows user another chance to maybe get a better score
	 * 
	 * ***Nation Mania fun fact!*** it is costom in the finals of nm tournaments for the contestents to ask for up to two more trys*** (using this button).
	 * ***looks like luggage*** 
	 * 	 */
	public static Button tryAgain;
	/**
	 * the other button showen after the game, for if the player is tired and wants to checks the high scores
	 * ***looks like home***
	 * 	 */
	public static Button mainMenu;
	/**
	 * button right bellow nationsGuess where user can submit his answer
	 * 
	 * 	 */
	private static Button submit;
	/**
	 * button on top left for cowards and people in a hurry
	 *  
	 * 	 */
	private static Button quit;
	/**
	 * toggle #1 in duo - toggles the type of report to send for the fact (the fact is a dead giveaway) 
	 * 
	 * 	 */
	private static JToggleButton giveAway;
	/**
	 * toggle #2 in duo - toggles the type of report to send for the fact (the fact is usless) 
	 * 
	 * 	 */
	private static JToggleButton badClue;
	/**
	 * button on the bottom right corner allowing the user to move to the next nation without guessing
	 * 
	 * ***looks like a plane over a globe*** 
	 * 	 */
	private static Button pass;
	/**
	 * button to the left of pass, so the user can buy the continent of the nation (displayed like a fact).
	 * 
	 * ***Nation Mania fun fact!*** all points charged for clues from players is donated to benifit children, in the very nation that the user is spending them to guess***
	 * ***looks like a compass*** 
	 * 
	 * 	 */
	private static Button getContinent;
	/**
	 * progress bar to show the user the time left for this game
	 * see GameTimer()
	 * 
	 * 	 */
	public static JProgressBar progressBar;
	/**
	 * the number keeping track of the number of facts used for this nation
	 * 
	 * 	 */
	public static int hints;
	/**
	 *  number used to show if the user bought a continent this game, mostly to know where to write the next fact.
	 * 
	 * 	 */
	private static int continentFactNum;
	/**
	 * a boolean corresponding to the toggle buttons 
	 * 
	 * 	 */
	private static boolean reportDeadGiveaway;
	/**
	 * another boolean corresponding to the same toggle buttons 
	 * 
	 * 	 */
	private static boolean reportBadClue;

	/**
	 * constructor - responasable for building components 
	 * 
	 * 	 */
	public GameScreen() {
		panel = new JPanel();
		panel.setLayout(null);
		setButtons();
		setAttributes();
		setLabels();
		Main.frame.getContentPane().add(panel,"GameScreen");
		panel.setVisible(false);
	}
	/**
	 * makes all the buttons 
	 * 
	 * 	 */
	private void setButtons() {
		
		hitMe = new Button("hitMe","",437,330,150,150);
		hitMe.setIcon(new ImageIcon(Main.class.getResource("/pics/whatthefactbutton.png")));
		hitMe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetReportFact();
				getNextFact(Game.getNextFact());
				randomizeNextFact();
			}
		});
		hitMe.setOpaque(false);
		hitMe.setFocusPainted(false);
		hitMe.setBorderPainted(false);
		hitMe.setContentAreaFilled(false);
		hitMe.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		panel.add(hitMe);
		
		getContinent = new Button("getContinent","",547,340,150,150);
		getContinent.setIcon(new ImageIcon(Main.class.getResource("/pics/getcontentbutton.png")));
		getContinent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetReportFact();
				getContinent();
			}
		});
		getContinent.setOpaque(false);
		getContinent.setFocusPainted(false);
		getContinent.setBorderPainted(false);
		getContinent.setContentAreaFilled(false);
		getContinent.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		panel.add(getContinent);
		

		pass = new Button("pass","",657,336,150,150);
		pass.setIcon(new ImageIcon(Main.class.getResource("/pics/passbutton.png")));
		pass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Round.reducePoints(Round.getScore());
				roundOver();
			}
		});
		pass.setOpaque(false);
		pass.setFocusPainted(false);
		pass.setBorderPainted(false);
		pass.setContentAreaFilled(false);
		pass.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		panel.add(pass);
		
	
		giveAway = new JToggleButton("Dead Giveaway");
		giveAway.setBounds(22,87,120,30);
		giveAway.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (reportDeadGiveaway)
					resetReportFact();
				else
					reportButtonPressed(true);
			}
		});
		panel.add(giveAway);
		
		badClue = new JToggleButton("Bad Clue");
		badClue.setBounds(152,87,120,30);
		badClue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (reportBadClue)
					resetReportFact();
				else
					reportButtonPressed(false);
			}
		});
		panel.add(badClue);
		
		submit = new Button("submit","Submit/Report",22,431,360,30);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				submitAnswer();
			}
		});
		panel.add(submit);
		
		quit = new Button("quit","Quit Game",22,22,120,30);
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		panel.add(quit);
		
		start = new Button("start","",0,0,808,475);
		start.setIcon(new ImageIcon(Main.class.getResource("/pics/startButton.png")));
		start.setOpaque(false);
		start.setFocusPainted(false);
		start.setBorderPainted(false);
		start.setContentAreaFilled(false);
		start.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GameScreen.startGame();
				toggleEnable(true);
				start.setVisible(false);
				}
			});
		panel.add(start);
		
		tryAgain = new Button("tryAgain","",0,0,400,475);
		tryAgain.setIcon(new ImageIcon(Main.class.getResource("/pics/restartButton.png")));
		tryAgain.setOpaque(false);
		tryAgain.setFocusPainted(false);
		tryAgain.setBorderPainted(false);
		tryAgain.setContentAreaFilled(false);
		tryAgain.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		tryAgain.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			MainMenu.setNewGame();
			}
		});
		panel.add(tryAgain);
		
		mainMenu = new Button("mainMenu","",395,0,408,475);
		mainMenu.setIcon(new ImageIcon(Main.class.getResource("/pics/mainMenuButton.png")));
		mainMenu.setOpaque(false);
		mainMenu.setFocusPainted(false);
		mainMenu.setBorderPainted(false);
		mainMenu.setContentAreaFilled(false);
		mainMenu.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		mainMenu.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			panel.setVisible(false);
			MainMenu.panel.setVisible(true);
			}
		});
		panel.add(mainMenu);
	}
	/**
	 * makes all the stuff that isnt buttons or labels 
	 * 
	 * 	 */
	private void setAttributes() {
		
		progressBar = new JProgressBar(0,Game.GAME_TIME_IN_SECONDS);
		progressBar.setFont(new Font("ariel", Font.PLAIN, 16));
		progressBar.setBounds(497, 20, 290, 26);
		GameScreen.panel.add(progressBar);
		
		nationsGuess = new JComboBox<String> (new DefaultComboBoxModel<String>(Main.Countries));
		nationsGuess.setEditable(true);
		nationsGuess.setBounds(22, 381, 356, 30);
		nationsGuess.setSelectedItem("");
		panel.add(nationsGuess);
		nationsGuess.setMaximumRowCount(26);
		
		JComponent editor = (JComponent) nationsGuess.getEditor().getEditorComponent();
	    editor.addKeyListener( new KeyAdapter() {

	        public void keyReleased(KeyEvent e) {
        		
	        	if (e.getKeyCode() == KeyEvent.VK_DOWN)
	        		nationsGuess.setSelectedItem(nationsGuess.getSelectedItem());
 	
	        	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	        		if (nationsGuess.getSelectedIndex() < 0)
	        			if (nationsGuess.getItemCount() != 0)
	        				nationsGuess.setSelectedIndex(0);
	        			else
	        				nationsGuess.setSelectedItem("");

	        		submitAnswer();
	        	}
	        }
	    });
	    
	    nationsGuess.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() { public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() != 38 && e.getKeyCode() != 40 && e.getKeyCode() != 10) {
                String a = nationsGuess.getEditor().getItem().toString();
                nationsGuess.removeAllItems();
                int match = 0;
            
                for (int i = 0; i < Main.States.size(); i++) {
                    if (Main.States.get(i).startsWith(a) || Main.States.get(i).toLowerCase().startsWith(a)) { 
                    	nationsGuess.addItem(Main.States.get(i)); match++; }
                }
                nationsGuess.getEditor().setItem(new String(a));
                nationsGuess.hidePopup();
                if (match != 0) { nationsGuess.showPopup(); }
            	}
        	}
	    });
	}
	/**
	 * makes all the labels 
	 * 
	 * 	 */
	private void setLabels() {
		
		Facts = new Label[MAX_NUM_FACTS];
		
		for (int i = 0; i < MAX_NUM_FACTS; i++) {
			if(i < 8)Facts[i] = new Label("Fact " + i,"",22,137 + 27 * i,375,20,0,null,GameFonts.FACT,null);
			else Facts[i] = new Label("Fact " + i,"",402,137 + 27 * (i - 8),375,20,0,null,GameFonts.FACT,null);
			Facts[i].setVisible(false);
			panel.add(Facts[i]);
		}
		
		totalScore = new Label("score","0",500,77,79,27,SwingConstants.CENTER,null,GameFonts.SCORE,null);
		panel.add(totalScore);
		
		countriesDiscovered = new Label("countriesDiscovered","0",710,77,79,27,SwingConstants.CENTER,null,GameFonts.SCORE,null);
		panel.add(countriesDiscovered);
		
		roundScore = new Label("roundScore","" + Round.getScore(),605,77,79,27,SwingConstants.CENTER,null,GameFonts.SCORE,null);
		panel.add(roundScore);
		
		smallLogo = new Label("smallLogo","",237,0,300,152,0,"/pics/LogoExtraSmall.png",GameFonts.NULL,null);
		smallLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(smallLogo);
		
		reportFact = new Label("reportFact","Report Fact:",22,57,90,27,(SwingConstants.CENTER),null,GameFonts.REPORT,null);
		reportFact.setColor(Color.WHITE);
		panel.add(reportFact);
		
		reportedFact = new JTextField();
		reportedFact.setBounds(110, 57, 30, 30);
		reportedFact.setFont(GameFonts.getFont(GameFonts.REPORT));
		reportedFact.setEnabled(false);
		
		reportedFact.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char vChar = e.getKeyChar();
                if (!(Character.isDigit(vChar)) || reportedFact.getText().length() > 1)
                    e.consume();
            }
                
            public void keyReleased(KeyEvent e) {
            	if (e.getKeyCode() == KeyEvent.VK_ENTER)
            		submitAnswer();
              	}
        });       
		panel.add(reportedFact);
		
		errorMsg = new Label("errorMsg","",145,52,597,36,SwingConstants.LEFT,null,GameFonts.INVALID,Color.RED);
		errorMsg.setVisible(false);
		panel.add(errorMsg);
		
		validMsg = new Label("validMsg","",157,52,597,36,SwingConstants.LEFT,null,GameFonts.VALID,Color.GREEN);
		validMsg.setVisible(false);
		panel.add(validMsg);
		
		background = new Label("background","bg",0,0,808,475,0,"/pics/GsBg.jpg",GameFonts.NULL,null);
		panel.add(background);	

	}
	/**
	 * used to help toggle buttons enabled collectivly
	 * 
	 * 	 */
	private static void turnOnButtons() {
		Main.executorService.execute(disablePass());
		hitMe.setEnabled(true);
		getContinent.setEnabled(true);
	}
	/**
	 *  pulls a new nation and reinitializes the board for a new round
	 *  @return Runnable - use to start new thread via the Executer.
	 * 	 */
	public static Runnable initGame() {
		return new Runnable() {
			public void run() {
				new Game();
				setNewRound();	
				GameTimer.initTimer();
			     for (int i = 0; i < Main.States.size(); i++) 
			    	 nationsGuess.addItem(Main.States.get(i));
			}
		};
	}

	public static void startGame () {
		displayInitialFacts();
		Main.executorService.execute(GameTimer.startTimer());
	}
	/**
	 *  returns control parameters for a fresh round and prepares the next nation
	 * 
	 * 	 */
	public static void setNewRound() {	
		resetValues();
		Nations.randNation();
	}
	/**
	 *  resets the values
	 * 
	 * 	 */
	private static void resetValues() {
		hints = 0;
		continentFactNum = -1;
		nationsGuess.setSelectedItem("");
		resetReportFact();
		Round.reset();
		updateRoundScore();
	}
	/**
	 *  checks nationsGuess combo box for the correct answer and reports if it is
	 *  @return <code>true</code> if the correct nation was guessed
	 * 		    <code>false</code> else
	 * 	 */
	private boolean guessCountry() {		
		Round.setSuccess(nationsGuess.getSelectedItem().equals(Nations.getMyLand().getName()));
		return Round.isSuccess();
	}
	/**
	 *  shows the two initial facts the user recieves for each nation
	 * 
	 * 	 */
	public static void displayInitialFacts() {	

		for (int i = 0; i < Round.NUM_STARTING_CLUES; i++) {	
			Fact fact = Nations.getMyLand().getStateFacts().getFact();
			
			displayFact(fact,String.format(fact.getRelation(), fact.getType() + " " + fact.getName()));
		}	
		randomizeNextFact();
	}
	/**
	 *  prepare next fact already while this fact is still displayed
	 * 
	 * 	 */
	
	private static void randomizeNextFact() {
		Game.setNextFact(Nations.getMyLand().getStateFacts().getFact()); 
	}
	
	private void getContinent() {

		Round.reducePoints(Round.getScore() / 4);
		updateRoundScore();
		displayContinent("Belongs to Continent: " + Nations.getMyLand().getContinent());
		getContinent.setEnabled(false);
	}
	/**
	 * adjusts and shows the next fact
	 * 
	 * @param fact a fact
	 * @param text it's corresponding caption
	 */
	private static void displayFact(Fact fact,String text) {
		Facts[hints].setText((hints + 1) + ". " + text);
		if(text.length() > 70) 
			Facts[hints].setFont(GameFonts.getFont(GameFonts.SMALL_FACT));

		Facts[hints].setToolTipText(text);
		Facts[hints].setVisible(true);
		hints++;
	}
	/**
	 * updates to report mode when one of the toggle buttons is pressed
	 * 
	 * 
	 * @param isDeadGiveaway is the dead give away button pressed
	 */
	private static void reportButtonPressed(boolean isDeadGiveaway) {
		if (isDeadGiveaway) {
			reportDeadGiveaway = true;
			reportBadClue = false;
			badClue.setSelected(false);
			giveAway.setSelected(true);
		}
		else {
			reportBadClue = true;
			reportDeadGiveaway = false;
			giveAway.setSelected(false);
			badClue.setSelected(true);
		}	
		reportedFact.setEnabled(true);
	}
	/**
	 * checks a fact report
	 * 
	 * 	
	 * @return <code>true</code> if a valid number was written in the report facts section
	 * 		   <code>false</code> else
	 */
	private static boolean isValidReport () {
		if (!isNumEntered())
			return false;
		if (!isNumValid())
			return false;
		
		return true;
	}
	/**
	 * make sure that the user chose a fact to report
	 * 
	 * @return <code>true</code> if a valid number was written in the report facts section
	 * 		   <code>false</code> else
	 * 	 */
	private static boolean isNumEntered () {
		if ((reportedFact.getText().equals(""))) {
			displayMsg(errorMsg,validMsg,"Enter Num Fact");
			reportedFact.setText("");
			return false;
		}
		return true;
	}
	/**
	 * and that fact exists
	 * @return <code>true</code> if a valid number was written in the report facts section
	 * 		   <code>false</code> else	 * 	 */
	private static boolean isNumValid () {
		if (Integer.parseInt(reportedFact.getText()) < 1 || Integer.parseInt(reportedFact.getText()) > hints) {
			displayMsg(errorMsg,validMsg,"Wrong Number!");
			reportedFact.setText("");
			return false;
		}
		if (Integer.parseInt(reportedFact.getText()) == continentFactNum) {
			displayMsg(errorMsg,validMsg,"Facts Only!");
			reportedFact.setText("");
			return false;
		}
		return true;
	}
	/**
	 * where the actual fact reporting ("the magic") happens.
	 * 
	 * 	 */
	private void reportFact () {
		errorMsg.setVisible(false);
		displayMsg(validMsg,errorMsg,"Thank You!");
		
		int numFact = Integer.parseInt(reportedFact.getText());
		
		//skip continent fact
		if (continentFactNum != -1 && numFact > continentFactNum)
			numFact--;
		
		Round.reportFact(Game.getGameNationsAndFacts().get(Nations.getMyLand().getID()).get(numFact - 1));
		
		resetReportFact();
	}
	/**
	 * unselecting the toggle and other stuff after the report.
	 * 
	 * 	 */
	private static void resetReportFact () {
		reportDeadGiveaway = reportBadClue = false;
		reportedFact.setText("");
		reportedFact.setEnabled(false);
		badClue.setSelected(false);
		giveAway.setSelected(false);
	}
	/**
	 * writes and shows a message for the user
	 * 
	 * 	
	 * @param Msg new message to be set
	 * @param otherMsg old message to be removed
	 * @param text a message to be displayed
	 */
	private static void displayMsg(Label Msg,Label otherMsg, String text) {
		Msg.setText(text);
		submitMsg = Msg;
		otherMsg.setVisible(false);
		Main.executorService.execute(Main.deleteMsg(submitMsg));
	}
	/**
	 * if user pressed get continent, this will show it to them
	 * 
	 * @param text a continent fact
	 */
	private static void displayContinent(String text) {
		Facts[hints].setText((hints + 1) + ". " + text);
		
		Facts[hints].setToolTipText(text);
		Facts[hints].setVisible(true);
		
		continentFactNum = hints + 1;
		hints++;
	}
	
	/**
	 * gets the next fact
	 * 
	 * @param fact fact
	 */
	private void getNextFact(Fact fact) {
		if (fact == null)
			return;
		if (Round.getScore() - Round.getPenalty() <= 0) {
			hitMe.setEnabled(false);
			return;
		}
		
		reducePoints();
		displayFact(fact,String.format(fact.getRelation(), fact.getType() + " " + fact.getName()));
	}
	/**
	 * reduces points from potencial round points, as payment for clues and stuff.
	 * 
	 * 	 */
	private void reducePoints() {
		Round.reducePoints(Round.getPenalty());
		Round.setPenalty(Round.getPenalty() + 2);
		updateRoundScore();
	}
	/**
	 * updates the round score
	 * 
	 * 	 */
	private static void updateRoundScore() {
		totalScore.setText("" + Game.getTotalScore());
		roundScore.setText("" + Round.getScore());
		countriesDiscovered.setText("" + Game.getStatesDiscovered());
	}
	
	/**
	 * checks answer after user presses submit
	 * 
	 * 	 */
	private void submitAnswer() {

		//submit report on invalid fact
		if (reportBadClue == true) {
			if (isValidReport())
				reportFact();
			return;
		}
		
		//submit report on giveaway fact
		if (reportDeadGiveaway == true)
			if (!isValidReport()) {
				return;
			}
		
		if (nationsGuess.getSelectedIndex() < 0) {
			displayMsg(errorMsg,validMsg,"Choose Nation");
			return;
		}

		//right guess
		if (guessCountry()) {
			//if reported dead giveaway and guessed right - report fact
			if (reportDeadGiveaway == true)
				reportFact();
			else 
				displayMsg(validMsg,errorMsg,"Nailed it!");
			
			roundOver();
		}
		
		else  {
			displayMsg(errorMsg,validMsg,"Wrong Guess!");
		}
	}
	/**
	 * disables pass button for a certein time so that the facts have time to load
	 * @return Runnable - use to start new thread via the Executer.
	 * 	 */
	private static Runnable disablePass() {
		return new Runnable() {
			public void run() {
				pass.setEnabled(false);
				try {
					Thread.sleep(1000);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pass.setEnabled(true);
			}
		};
	}
	/**
	 * resets the values for the new round
	 * 
	 * 	 */
	private void roundOver() {
		
		//log state and that been played to DB
		Main.executorService.execute(Nations.getMyLand().logState());
		
		//if round was won - update score and number of states discovered
		if (Round.isSuccess())
			Round.nationFound();
		
		for (int i = 0; i < hints; i++) {
			Facts[i].setVisible(false);
			Facts[i].setFont(GameFonts.getFont(GameFonts.FACT));
		}
			
		setNewRound();
		displayInitialFacts();
		turnOnButtons();
	}
	/**
	 * can both enable and disable all the screen buttons
	 * 
	 * 	
	 * @param state <code> true </code> OR <code> false </code>
	 */
	public static void toggleEnable(boolean state) {
		quit.setEnabled(state);
		pass.setEnabled(state);
		hitMe.setEnabled(state);
		getContinent.setEnabled(state);
		giveAway.setEnabled(state);
		badClue.setEnabled(state);
		nationsGuess.setEnabled(state);
		submit.setEnabled(state);
	}
	/**
	 * prepares stuff to exit the game
	 * 
	 * 	 */
	public static void quit() {
		GameTimer.stopTimer = true;
		resetValues();
		panel.setVisible(false);
		MainMenu.panel.setVisible(true);
	}
	/**
	 * prepares stuff to exit the game with an error message
	 * 
	 * 	 */
	public static void quitWithError() {
		MainMenu.errorMsg.setText(Queries.QUERY_FAILED_MESSAGE +  " Game has terminated");
		Main.executorService.execute(Main.deleteMsg(MainMenu.errorMsg));
		quit();
	}
}

