package NationMania.ui;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import NationMania.core.Nations;
import NationMania.core.Player;
import NationMania.db.Queries;

/**
 * Login Screen - the first screen displayed on startup.<br>
 * login can be set by pressing "login" button or by "Enter"
 * 	 */

public final class LoginMenu {
	/**
	 * parent panel for login menu components (added to card layout).
	 * 
	 * 	 */
	public static JPanel panel;
	/**
	 * a text field for the user to write his username
	 * 
	 * 	 */
	private JTextField usernameField;
	/**
	 * a password field for the user to write his password
	 * 
	 * 	 */
	private JPasswordField passwordField;
	/**
	 * the background image label
	 * 
	 * 	 */
	private Label background;
	/**
	 * a label to let the user know of errors or invalid accounts
	 * 
	 * 	 */
	public static Label errorMsg;
	/**
	 * a label for the username text field
	 * 
	 * 	 */
	private Label usernameLabel;
	/**
	 * a label for the password text field
	 * 
	 * 	 */
	private Label passwordLabel;
	/**
	 * the logo
	 * 
	 * 	 */
	private Label LogoLabel;
	/**
	 * a button for the user to go to the signup screen
	 * 
	 * 	 */
	private Button signUp;
	/**
	 * a button for the user to submit and log in
	 * 
	 * 	 */
	private Button login;
	/**
	 * a button for the user to exit the game
	 * 
	 * 	 */
	private Button quit;
	/**
	 * constructor - responsable for building components 
	 * 
	 * 	 */
	public LoginMenu() {
		panel = new JPanel();
		panel.setLayout(null);
		setButtons();
		setFields();
		setLabels();
		Main.frame.getContentPane().add(panel,"LoginMenu");
		panel.setVisible(true);
		SplashScreen.panel.setVisible(false);
	}
	/**
	 * makes all the buttons
	 * 
	 * 	 */
	public void setButtons() {
		
		login = new Button("loginButton","Login",159,279,497,23);
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setLogin();
			}
		});
		panel.add(login);
		
		signUp = new Button("signUpButton","Sign up",159,309,497,23);
		signUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//database is empty! send user to update DB screen
				if (Queries.QUERY_FAILED_MESSAGE.equals("ERROR: No Countries where found! Please Update Database!")) {
					initDatabase();
					return;
				}
				panel.setVisible(false);
				SignUp.panel.setVisible(true);
				errorMsg.setText("");
				SignUp.isEdit = false;
				SignUp.usernameField.setEditable(true);
			}
		});
		panel.add(signUp);
		
		quit = new Button("Quit","Quit",159,363,497,23);
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.closeAllresourcesAndQuit();
			}
		});
		panel.add(quit);
	}
	/**
	 * makes all the text and pssword fields
	 * 
	 * 	 */
	public void setFields() {
		passwordField = new JPasswordField();
		passwordField.setBounds(296, 222, 360, 30);
		loginByEnter(passwordField);
		panel.add(passwordField);
	
		usernameField = new JTextField();
		usernameField.setBounds(296, 183, 360, 30);
		usernameField.setColumns(10);
		loginByEnter(usernameField);
		panel.add(usernameField);
	}
	/**
	 * @param username user name
	 * @param password user password
	 * checks to see if the username and password submitted represent a valid account in the database
	 * @return <code>true</code> if a valid login
	 * 		   <code>false</code> else	 
	 *  	 
	 */
	public boolean isValidLogin(String username, String password) {
		if (isFieldEmpty(username) || isFieldEmpty(password) || !isUserFound(username, password))
			return false;
			
		try {
			Queries.logUser(username);
		} catch (SQLException e) {
			errorMsg.setText("ERROR: Unable to validate user! Please try again later");
			return false;
		}
		return true;
	}
	/**
	 * @param field username or password
	 * @return <code>true</code> if the given string is empty
	 * 		   <code>false</code> else	 
	 *  	 
	 */
	private boolean isFieldEmpty(String field) {
		if (field.equals("")) {
			errorMsg.setText("Username or Password are empty! Please try again");
			return true;
		}
	return false;
	}
	/**
	 * @param username user name
 	 * @param password user password
	 * @return <code>true</code> if the username and password combination is found in the database
	 * 		   <code>false</code> else	 
	 *  	 
	 */
	public boolean isUserFound(String username, String password) {
		try {
			if(!Queries.findUser(username,password)) {
				errorMsg.setText("Wrong Username or Password!");
				return false;
				}	
		} catch (SQLException e) {
			errorMsg.setText(Queries.QUERY_FAILED_MESSAGE);
			return false;
		}		
		return true;
	}
	/**
	 * makes all the labels
	 * 
	 * 	 */
	public void setLabels() {
		
		usernameLabel = new Label("Username","Username",159,190,127,17,SwingConstants.CENTER,null,GameFonts.NULL,null);
		usernameLabel.setOpaque(true);
		panel.add(usernameLabel);
		
		passwordLabel = new Label("Password","Password",159,229,127,17,SwingConstants.CENTER,
				null,GameFonts.NULL,null);
		passwordLabel.setOpaque(true);
		panel.add(passwordLabel);
		
		errorMsg = Main.setScreenErrorMsg();
		panel.add(errorMsg);
		LogoLabel = Main.setScreenLogoLabel();
		panel.add(LogoLabel);
		background = Main.setScreenBackground();
		panel.add(background);
	}
	/**
	 * @param field field
	 * has the given field listen for an enter key press to submit the account information
	 * 	 */
	private void loginByEnter(JComponent field) {
		field.addKeyListener( new KeyAdapter() {
	        public void keyReleased(KeyEvent e) {
	        	if (e.getKeyCode() == KeyEvent.VK_ENTER)
	        		setLogin();
	        }
		});
	}
	/**
	 * logs in to the validated player account and sets up the main menu
	 * 
	 * 	 */
	private void setLogin() {
		if (isValidLogin(usernameField.getText(), new String(passwordField.getPassword()))) {
			panel.setVisible(false);
			logUser();
		}
		else {
			errorMsg.setColor(Color.RED);
			Main.executorService.execute(Main.deleteMsg(errorMsg));
		}
	}
	
	public static void logUser() {
		
		if (!SignUp.isEdit) {
			//change nations weight according to initial difficulty 1 
			Main.executorService.execute(Nations.updateStatesWeight());
			MainMenu.welcomeUser.setText("Hi " + Player.getUsername() + "!");
		}
		else
			MainMenu.welcomeUser.setText("Account Updated Successfuly!");
		
		MainMenu.welcomeUser.setVisible(true);
		MainMenu.panel.setVisible(true);
		Main.executorService.execute(Main.deleteMsg(MainMenu.welcomeUser));
		errorMsg.setText("");
	}
	/**
	 * called on first installation of database - send user to update DB screen
	 * 
	 * 	 */
	private void initDatabase() {
		Settings.errorMsg.setText(Queries.QUERY_FAILED_MESSAGE);
		Settings.updateAccount.setEnabled(false);
		panel.setVisible(false);
		Settings.panel.setVisible(true);
		Main.executorService.execute(Main.deleteMsg(Settings.errorMsg));
	}
}
