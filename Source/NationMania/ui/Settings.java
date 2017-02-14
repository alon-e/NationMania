package NationMania.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;

import NationMania.core.Game;
import NationMania.core.Nations;
import NationMania.db.Queries;
import NationMania.parsing.YagoUpdate;

/**
 * Settings Screen - holds Game Difficulty, Update DB and Update User Account.<br>
 * User can change all fields except username.
 * 	 */

public final class Settings {
	
	public static JPanel panel;
	private Label background;
	private Label LogoLabel;
	private static Label updateLabel;
	private static Label updateLabel2;
	public static Label errorMsg;
	private Label gameDifficulty;
	private JPanel dp;
	private static Button updateDB;
	private Button back;
	private static int updateProgress;
	private static JProgressBar updateBar;
	private JRadioButton ez;
	private JRadioButton mid;
	private JRadioButton hard;
	private ButtonGroup dif;
	public static Button updateAccount;
	
	private static int lastDifficultyPlayed;

	/**
	 * @return the difficulty of the last game played
	 */
	public static int getGameLastDifficulty() {
		return lastDifficultyPlayed;
	}

	/**Called at the beginning of every game.
	 * This parameter set to ensure every time the player changed the difficulty and only then, the nations probabilites
	 * in the nations list will be updated accordnigly.
	 * @param gameLastDifficulty the difficulty set for the current game
	 */
	public static void setGameLastDifficulty(int gameLastDifficulty) {
		Settings.lastDifficultyPlayed = gameLastDifficulty;
	}
	
	/**
	 * Constructor
	 * Called on applicataion startup
	 */
	public Settings() {
		panel = new JPanel();
		panel.setLayout(null);
		setButtons();
		setLabels();
		lastDifficultyPlayed = 1;
		Main.frame.getContentPane().add(panel,"Settings");
		panel.setVisible(false);
	}

	/**Gets the current status of the DB update
	 * @param updateStatus update satus as sent from Yago Update.
	 */
	public static void setUpdateStatus(String updateStatus) {
		updateLabel.setText(updateStatus);
	}

	/**Gets the current status of the DB update
	 * @param updateStatus2 update satus as sent from Yago Update.
	 */
	public static void setUpdateStatus2(String updateStatus2) {
		updateLabel2.setText(updateStatus2);
	}

	/**Gets the current progress precentage of the DB update
	 * @param updateProgress a number between 1 to 100 represents when the update will be over
	 */
	public static void setUpdateProgress(int updateProgress) {
		Settings.updateProgress = updateProgress;
		
		//update failed - return to 0% and quit update
		if (updateProgress == -1)
			updateBar.setValue(0);
		else 
			updateBar.setValue(updateProgress);
		
		updateBar.setString(updateBar.getValue() + "%");
	}	
	
	/**
	 * Difficulty buttons initialization.
	 * Difficulty is 1 by default, and can be changed by pressing th relevant button.
	 * The change is set only when player hit the "Back" button.
	 */
	public void setDifficulties() {
		ez = new JRadioButton("Provincial (easy)");
	    mid = new JRadioButton("Traveler (medium)");
	    hard = new JRadioButton("Worldly (hard)");
	    
	    ez.setForeground(Color.GREEN);
	    ez.setFont(GameFonts.getFont(GameFonts.DIFFICULTY));
		//ez.setFont(new Font("Impact",Font.PLAIN,ez.getFont().getSize() + 8));
	    dp.add(ez);
	    
		mid.setForeground(Color.YELLOW);
		mid.setFont(GameFonts.getFont(GameFonts.DIFFICULTY));
	    //mid.setFont(new Font("Impact",Font.PLAIN,mid.getFont().getSize() + 8));
	    dp.add(mid);
	    
	    hard.setForeground(Color.RED);
	    hard.setFont(GameFonts.getFont(GameFonts.DIFFICULTY));
	    //hard.setFont(new Font("Impact",Font.PLAIN,hard.getFont().getSize() + 8));
	    dp.add(hard);
	    
	    switch (Game.getDifficulty()) {
		case 1:
			ez.setSelected(true);
			break;
		case 2:
			mid.setSelected(true);
			break;
		case 3:

			hard.setSelected(true);
			break;
		default:
			ez.setSelected(true);
			break;
		}
	    
	    dif = new ButtonGroup();
	    dif.add(ez);
	    dif.add(mid);
	    dif.add(hard);
	 }
	
	/**
	 * Initialize all screen buttons.
	 * updateDB button - Clean DB and start importing from Yago.
	 * Since the process is long and cannot be stopped in the middle, user should press twice to set button.
	 * updateAccount button - allows user to edit his profile.
	 * The user can edit all fields, except his username.
	 * Back button - go back to Main Menu screen.
	 */
	private void setButtons() {
		
		updateDB = new Button("updateDB","Update Database",159,227,497,23);
		updateDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (updateDB.getText().equals("Update Database - Are You Sure?")) {
					Main.executorService.execute(startUpdate());
					updateDB.setText("Update Database");
				}
				else
					updateDB.setText("Update Database - Are You Sure?");
			}
		});

		panel.add(updateDB);
		
		updateAccount = new Button("updateAccount","Update Account",159,257,497,23);
		updateAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateDB.setText("Update Database");
				SignUp.isEdit = true;
				SignUp.editUser();
				panel.setVisible(false);
				SignUp.panel.setVisible(true);
			}
		});
		panel.add(updateAccount);
		
		back = new Button("back","Back",159,316,497,23);
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateDB.setText("Update Database");
				panel.setVisible(false);
				if(ez.isSelected()) Game.setDifficulty(1);
				if(mid.isSelected()) Game.setDifficulty(2);
				if(hard.isSelected()) Game.setDifficulty(3);
				
				
				if (!updateAccount.isEnabled()) {		//initialized databese - go back to login screen!
					
					//reset query message and try again after update
					Queries.QUERY_FAILED_MESSAGE = "";
					try {		
						Nations.setAllStates();
					} catch (SQLException e) {
	
					}
					if (!Queries.QUERY_FAILED_MESSAGE.equals(""))		//get countries failed again
						LoginMenu.errorMsg.setText(Queries.QUERY_FAILED_MESSAGE);
					else {
						LoginMenu.errorMsg.setColor(Color.GREEN);
						LoginMenu.errorMsg.setText("Updating Database Succeeded! Now you can Sign Up!");
					}				
					LoginMenu.panel.setVisible(true);
					updateAccount.setEnabled(true);
					Main.executorService.execute(Main.deleteMsg(LoginMenu.errorMsg));
				}
				else
					MainMenu.panel.setVisible(true);
			}
		});
		panel.add(back);	
	}
	
	/**
	 * Initiallize all screen labels
	 */
	private void setLabels() {
		
		dp = new JPanel();
		setDifficulties();
		dp.setOpaque(false);
		dp.setBounds(159, 188, 497, 30);
		panel.add(dp);
		
		gameDifficulty = new Label("GameDifficulty","Game Difficulty:", 159,140,497,34, 0, null, GameFonts.TITLE,Color.WHITE);
		panel.add(gameDifficulty);
		
		updateBar = new JProgressBar(0, 100);
		updateBar.setFont(GameFonts.getFont(GameFonts.INFO));
		updateBar.setBounds(159,400,497,30);
		updateBar.setForeground(Color.blue);
		updateBar.setStringPainted(true);
		updateBar.setVisible(false);
		panel.add(updateBar);
		
		updateLabel = new Label("updateLabel","",159,355,497,30,0,null,GameFonts.INFO,Color.WHITE);
		updateLabel.setVisible(false);
		panel.add(updateLabel);
		
		updateLabel2 = new Label("updateLabel2","",159,375,497,30,0,null,GameFonts.INFO,Color.WHITE);
		updateLabel2.setVisible(false);
		panel.add(updateLabel2);	
		
		errorMsg = Main.setScreenErrorMsg();
		panel.add(errorMsg);
		LogoLabel = Main.setScreenLogoLabel();
		panel.add(LogoLabel);
		background = Main.setScreenBackground();
		panel.add(background);
	}
	
	
	/**Perform full update of DB from Yago - game is disabled at all update time!
	 * Update cannot be done to the DB on TAU server, only to the local DB.
	 * A new thread calls the Yago Update function, while the current thread prints all recieved status messages.
	 * The proccess takes roughly about 6 minutes.
	 * @return Runnable - use to start new thread via the Executer.
	 */
	public static Runnable startUpdate () {
		return new Runnable() {
			public void run() {
				initUpdateLabels();
				Main.executorService.execute(YagoUpdate.secureUpdate());		//call Yago Update class
				while (updateProgress >= 0 && updateProgress < 100) {
					
					//update text and progress every second
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch(Exception e) {}
				}
			
				try {
					//wait for 2 seconds to read the last message
					TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e) {}
				
				resetAfterUpdate();
			}
		};
	}
	
	/**
	 * Called on update start.
	 * Disables application, and initialize all status labels to their starting values.
	 */
	private static void initUpdateLabels () {
		Main.frame.setEnabled(false);
		updateBar.setVisible(true);
		updateLabel.setVisible(true);
		updateLabel2.setVisible(true);
		updateProgress = 0;
		updateLabel.setText("");
		updateLabel2.setText("");
	}
	
	/**
	 * Called on update finish.
	 * Enables back application.
	 */
	private static void resetAfterUpdate () {
		updateBar.setVisible(false);
		updateLabel.setVisible(false);
		updateLabel2.setVisible(false);
		Main.frame.setEnabled(true);
	}
}
