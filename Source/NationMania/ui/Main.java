package NationMania.ui;

import javax.swing.JFrame;
import javax.swing.SwingConstants;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UIManager.*;

import NationMania.core.Config;
import NationMania.core.Nations;
import NationMania.db.Queries;

/**
 * Main Screen - loads values from Config file and initialize all GUI screens. <br>
 * Main thread of GUI.
 * 	 */

public class Main {
	
	public static int MAX_NUM_THREADS = Config.GET_VAR_INT("MAX_NUM_THREADS") == -1 ? 10 : Config.GET_VAR_INT("MAX_NUM_THREADS");
	public static JFrame frame;
	public static ArrayList<String> States;
	public static String[] Countries;
	public static ExecutorService executorService = Executors.newFixedThreadPool(MAX_NUM_THREADS);

	/**
	 * Launch the application.
	 * @throws IOException Signals that an I/O exception of some sort has occurred. 
	 * @param args not used
	 */
	public static void main(String[] args) throws IOException {
		
		if (Config.loadConfig() == -1) 
			System.out.println("Loading Configurtion File Failed! Using Default Values");
		
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException Signals that an I/O exception of some sort has occurred. 
	 */
	private static void initialize() throws IOException {	
		
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {}
		
		setFrame();
		new GameFonts();
		new SplashScreen();
		
		if (SplashScreen.testConnection()) {
			setNations();
			Main.executorService.execute(Main.setAllScreens());
		}
		else
			Main.closeAllresourcesAndQuit();
	}
	/**
	 * fills the states list and the countries array with an updated nations table
	 * 
	 */
	public static void setNations() {
		Main.States = new ArrayList<String>();
	
		for(int i = 0; i < Nations.getAllStates().size(); i++)
			Main.States.add(Nations.getAllStates().get(i).getName());
		
		Collections.sort(Main.States);

		Main.Countries = new String[Main.States.size()];
		
		for(int i = 0; i < Main.States.size(); i++)
			Main.Countries[i] = Main.States.get(i);
	}
	/**
	 * Creates the main and only frame for the game
	 * 
	 */
	private static void setFrame() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/pics/Icon.png")));
		frame.setResizable(false);
		frame.setTitle("Nation Mania!");
		frame.setBounds(100, 100, 804, 503);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
	}
	/**
	 * calls all screen constructors to initialize the ui tree
	 * @return Runnable - use to start new thread via the Executer.
	 */
	public static Runnable setAllScreens() {
		return new Runnable() {
			
			public void run() {
				new LoginMenu();
				new SignUp();
				new MainMenu();
				new Settings();
				new GameScreen();
				new GameTimer();
				new GameInstructions();
				new HighScore();
			}
		};
	}
	
	/**
	 * @param  Msg a label to be removed
	 * deletes the message (in label Msg) after two seconds 
	 * @return Runnable - use to start new thread via the Executer.
	 */
	public static Runnable deleteMsg (Label Msg) {
		return new Runnable() {
			public void run() {
				Msg.setVisible(true);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Msg.setVisible(false);
			}
		};
	}
	/**
	 * initializes the error message label
	 * 
	 * @return error Msg label
	 */
	public static Label setScreenErrorMsg() {
		Label errorMsg = new Label("ScreenError","Error Message",44,400,740,26,SwingConstants.CENTER,null,GameFonts.ERROR,Color.RED);
		errorMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
		errorMsg.setVisible(false);
		return errorMsg;
	}
	
	public static Label setScreenLogoLabel() {
		Label logo = new Label("ScreenLogo","",240,0,300,152,0,"/pics/LogoSmall.png",GameFonts.NULL,null);
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);
		return logo;
	}
	/**
	 * initializes the logo label
	 * 
	 * @return background label
	 */
	public static Label setScreenBackground() {
		return new Label("ScreenBackground","",0,0,800,475,0,"/pics/world-map-background.jpg",GameFonts.NULL,null);
	}
	/**
	 * closes after exit
	 * 
	 */
	public static void closeAllresourcesAndQuit() {
		frame.dispose();
		executorService.shutdown();
		Queries.closeAllConnections();
	}
}

