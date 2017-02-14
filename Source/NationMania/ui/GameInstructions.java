package NationMania.ui;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Game Instructions Screen. contains information for user of how to play the game
 * 	 */

public final class GameInstructions {
	/**
	 * parent panel for game instructions components (added to card layout).
	 * 	 */
	public static JPanel panel;
	/**
	 * the label showing the actual instructions (used to be text, now its a picture)
	 * 	 */
	private JLabel instructions;
	private Label background;
	private Label logLabel;
	private Button back;
	/**
	 * constructor - responsable for building components 
	 * 
	 * 	 */
	public GameInstructions() {
		panel = new JPanel();
		panel.setLayout(null);
		setButtons();
		setInstructions();
		setLabels();
		Main.frame.getContentPane().add(panel,"GameInstructions");
		panel.setVisible(false);
	}
	/**
	 * makes all the buttons
	 * 
	 * 	 */
	private void setButtons() {
		back = new Button("back","Back",159,356,497,23);
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.setVisible(false);
				MainMenu.panel.setVisible(true);
			}
		});
		panel.add(back);
	}
	/**
	 * sets the display properties for the instrctions label
	 * 
	 * 	 */
	private void setInstructions() {	
		instructions = new Label("instructions","",152,130,600,220,SwingConstants.CENTER,null,GameFonts.INSTRUCTIONS,Color.WHITE);
//disabled for Linux compatibility
//		instructions.setText("<html><bold>Guess as many countries as you can in 3 minutes!<br>"
//				+ "Use minimun clues and beat everyone else to the highest score!<br>"
//				+ "Each round you get 2 clues for free. You can ask for more, but it will cost ya.<br>"
//				+ "You can also get the state's continent, but it's expensive. It will cost you<br>"
//				+ "a quarter of the current round's remaining points.<br>"
//				+ "If a clue is too easy or faulty, please let us know using the relevant button.<br>"
//				+ "Note that if your up for it, you can change the game difficulty.<br>"
//				+ "So - Are you Colombus or just a doofus?<br>"
//				+ "Good Luck!</bold></html>");
//		panel.add(instructions);
		
		instructions.setIcon(new ImageIcon(Main.class.getResource("/pics/instructions.png")));
		instructions.setOpaque(false);
		instructions.setFocusable(false);
		instructions.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		panel.add(instructions);
	}
	/**
	 * nakes all the labels
	 * 
	 * 	 */
	private void setLabels() {
		
		logLabel = Main.setScreenLogoLabel();
		panel.add(logLabel);
		background = Main.setScreenBackground();
		panel.add(background);
	}

}
