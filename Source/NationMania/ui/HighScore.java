package NationMania.ui;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import NationMania.db.Queries;

/**
 * HighScores Screen - print to user the best scores played by all users<br>
 * The number of rows to display is set in the Config file and cannot be more than 10
 * 	 */

public final class HighScore {
	/**
	 * parent panel for high scores screen components (added to card layout).
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
	 * label used to tell user if an error occurs
	 * 
	 * 	 */
	private static Label errorMsg;
	/**
	 * this screen title labe
	 * 
	 * 	 */
	private Label HighScore;
	/**
	 * an array holding the labels for the high scores
	 * 
	 * 	 */
	private static Label[] row;
	/**
	 * button for user who dosnt see his name on the top and just cant stand for it
	 * 
	 * 	 */
	private Button back;
	/**
	 * constructor - responsable for building components 
	 * 
	 * 	 */
	public HighScore() {
		panel = new JPanel();
		panel.setLayout(null);
		setButtons();
		setLabels();
		Main.frame.getContentPane().add(panel,"HighScore");
		panel.setVisible(false);
	}
	/**
	 * retrieves the high scores from the database
	 * 
	 * 	 */
	public static void setTopScores() {
		try {
			Queries.getTopScores();
		} catch (SQLException e) {
			errorMsg.setText(Queries.QUERY_FAILED_MESSAGE + " Data is not updated");
			Main.executorService.execute(Main.deleteMsg(errorMsg));
		}
	}
	/**
	 * makes all the buttons
	 * 
	 * 	 */
	private void setButtons() {
		back = new Button("back","Back",159,313,505,23);
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.setVisible(false);
				MainMenu.panel.setVisible(true);
			}
		});
		panel.add(back);
	}
	/**
	 * puts the high scores values in their respective labels
	 * 
	 * @param topScores string array of top scores
	 */
	public static void setHighScoresLabels(String[] topScores) {
		for (int i = 0; i < Queries.TOP_SCORE_TABLE_SIZE; i++) {
			row[i].setText((i + 1) + ". " + topScores[i]);
		}
	}
	/**
	 * makes all the labels
	 * 
	 * 	 */
	private void setLabels() {
		
		HighScore = new Label("HighScore","High Score:",300,135,200,40,0,null,GameFonts.TITLE,Color.WHITE);
		panel.add(HighScore);
		
		row = new Label[Queries.TOP_SCORE_TABLE_SIZE];
		
		for (int i = 0; i < Queries.TOP_SCORE_TABLE_SIZE; i++) {
			row[i] = new Label("rowScore",(i + 1) + ". ",260,178 + 25 * i, 190, 24,0,null,GameFonts.INVALID,Color.ORANGE);
			row[i].setHorizontalAlignment(SwingConstants.LEFT);
			
			if (i >= Queries.TOP_SCORE_TABLE_SIZE / 2)
				row[i].setBounds(425,178 + 25 * (i - (Queries.TOP_SCORE_TABLE_SIZE / 2)),180,24);
			
			row[i].setOpaque(false);
			panel.add(row[i]);
		}
		
		errorMsg = Main.setScreenErrorMsg();
		panel.add(errorMsg);
		LogoLabel = Main.setScreenLogoLabel();
		panel.add(LogoLabel);
		background = Main.setScreenBackground();
		panel.add(background);
	}
}
