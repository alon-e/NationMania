package NationMania.ui;

import java.awt.Color;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import NationMania.core.Nations;
import NationMania.db.Queries;


/**
 * Welcome Screen - test for valid connection to DB. If found - upload application. else - set error message and exit
 * 	 */

public final class SplashScreen {

		/**
	 	* parent panel for login menu components (added to card layout).
	 	* 
	 	* 	 */
		public static JPanel panel;
		/**
		 * a label to let the user know of errors in connecting to the database
		 * 
		 * 	 */
		private static Label errorMsg;
		/**
		 * a label to let the user know of any milestones in connecting to the database
		 * 
		 * 	 */
		private static Label validMsg;
		/**
		 * a label to let the user know of anything else
		 * 
		 * 	 */
		private static Label displayMsg;
		/**
		 * the background image label
		 * 
		 * 	 */
		private static Label background;
		/**
		 * constructor - responsable for building components 
		 * 
		 * 	 */
		public SplashScreen() {
			panel = new JPanel();
			panel.setLayout(null);
			setLabels();
			Main.frame.getContentPane().add(panel,"SplashScreen");
			Main.frame.setVisible(true);
			panel.setVisible(true);
		}
		/**
		 * makes all the labels 
		 * 
		 * 	 */
		private void setLabels() {
			
			errorMsg = new Label("errorMsg","error msg",109,270,600,33,SwingConstants.CENTER,null,GameFonts.ERROR,Color.RED);
			errorMsg.setVisible(false);
			panel.add(errorMsg);
			
			validMsg = new Label("errorMsg","Testing Database Connection...",109,270,600,33,SwingConstants.CENTER,null,GameFonts.ERROR,Color.WHITE);
			validMsg.setVisible(true);
			panel.add(validMsg);
		
			background = new Label("background","bg",0,0,810,475,0,"/pics/splashScreen.jpg",GameFonts.NULL,null);
			panel.add(background);
		}
	/**
	 * checks to connection to the database and lets the user know 
	 * 
	 * @return <code>true</code> if connection is good
	 * 		   <code>false</code> else
	 */
		public static boolean testConnection() {		
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException ie) {}
			
			boolean flag = true;
			try {
				Nations.setAllStates();
			} catch (SQLException e) {
				displayMsg(errorMsg,validMsg,Queries.QUERY_FAILED_MESSAGE + " Exiting...");
				flag = false;
			}
			if (flag) {
				validMsg.setColor(Color.GREEN);
				displayMsg(validMsg,errorMsg,"Connection Succeeded! Loading application...");
			}
			
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException ie) {}
				return flag;
		}
		
		private static void displayMsg(Label Msg,Label otherMsg, String text) {
			Msg.setText(text);
			displayMsg = Msg;
			otherMsg.setVisible(false);
			Main.executorService.execute(Main.deleteMsg(displayMsg));
		}
}
