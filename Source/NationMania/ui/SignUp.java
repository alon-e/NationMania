package NationMania.ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import NationMania.core.Player;
import NationMania.db.Queries;

/**
 * Sign Up Screen - allows new users to register to DB. each user has unique username (NOT CASE SENSITIVE), password,<br>
 * email and home nation.<br>
 * Except email, all fields are mandatory (home nation default is set to Israel)<br>
 * User can change all fields except username.<br>
 * After Sign Up - activation mail is sent
 * 	 */

public final class SignUp {
	
	/**
	 * parent panel for login menu components (added to card layout).
	 * 
	 * 	 */
	public static JPanel panel;
	/**
	 * a text field for the user to write his username
	 * 
	 * 	 */
	public static JTextField usernameField;
	/**
	 * a password field for the user to write his password
	 * 
	 * 	 */
	private static JPasswordField passwordField;
	/**
	 * a password field for the user to validate his password
	 * 
	 * 	 */
	private static JPasswordField password2Field;
	/**
	 * a text field for the user to write his email
	 * 
	 * 	 */
	private static JTextField emailField;
	/**
	 * a combo box for the user to choose his nation
	 * 
	 * 	 */
	public static JComboBox<String> nationsList;
	/**
	 * the ol background image label
	 * 
	 * 	 */
	private Label background;
	/**
	 * a label to let the user know of errors or invalid account information
	 * 
	 * 	 */
	private static Label errorMsg;
	/**
	 * a label for the username text field
	 * 
	 * 	 */
	private Label usernameLabel;
	/**
	 * a label for the password field
	 * 
	 * 	 */
	private Label choosePassword;
	/**
	 * a label for the validate pssword field
	 * 
	 * 	 */
	private Label validatePassword;
	/**
	 * a label for the nations combo box
	 * 
	 * 	 */
	private Label homeNation;
	/**
	 * a label for the email text field
	 * 
	 * 	 */
	private Label enterEmail;
	/**
	 * the trusty logo
	 * 
	 * 	 */
	private Label LogoLabel;
	/**
	 * a button for the user to submit the new account details, register the account with the database and login to start playing
	 * 
	 * 	 */
	private Button submit;
	/**
	 * a button for the user to go back to the login screen/settings screen (depending on isEdit)
	 * 
	 * 	 */
	private Button back;
	
	/**
	 * a flag for wether this is a new account or an exiting account to be edited
	 * 
	 * 	 */
	public static boolean isEdit;
	/**
	 * constructor - responsable for building components 
	 * 
	 * 	 */
	public SignUp() {
		panel = new JPanel();
		panel.setLayout(null);
		setSButtons();
		setFields();
		setLabels();
		Main.frame.getContentPane().add(panel,"SignUp");
		panel.setVisible(false);
		isEdit = false;
	}
	/**
	 * makes all the buttons
	 * 
	 * 	 */
	public void setSButtons() {
		
		submit = new Button("submitButton","Submit",159,363,496,23);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sign();
			}
		});
		panel.add(submit);
		
		back = new Button("back","Back",159,397,496,23);
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel.setVisible(false);
				if (isEdit)
					Settings.panel.setVisible(true);
				else
					LoginMenu.panel.setVisible(true);
				clearFields();
			}
		});
		panel.add(back);
	}
	/**
	 * makes all the text and password fields
	 * 
	 * 	 */
	public void setFields() {
		
		usernameField = new JTextField();
		usernameField.setBounds(285, 163, 370, 30);
		usernameField.setColumns(26);
		signByEnter(usernameField);
		panel.add(usernameField);

		passwordField = new JPasswordField();
		passwordField.setBounds(285, 204, 370, 30);
		passwordField.setColumns(26);
		signByEnter(passwordField);
		panel.add(passwordField);
		
		password2Field = new JPasswordField();
		password2Field.setColumns(26);
		password2Field.setBounds(285, 245, 370, 30);
		signByEnter(password2Field);
		panel.add(password2Field);
		
		emailField = new JTextField();
		emailField.setColumns(26);
		emailField.setBounds(285, 286, 370, 30);
		signByEnter(emailField);
		panel.add(emailField);
		
		nationsList = new JComboBox<String>(new DefaultComboBoxModel<String>(Main.Countries));
		nationsList.setBounds(285, 332, 177, 20);
		nationsList.setToolTipText("");
		nationsList.setMaximumRowCount(26);
		signByEnter(nationsList);
		nationsList.setSelectedItem("Israel");
		panel.add(nationsList);
	}
	/**
	 * makes all the labels
	 * 
	 * 	 */
	public void setLabels() {
		
		usernameLabel = new Label("username","Username",159,171,116,14,SwingConstants.CENTER,null,GameFonts.NULL,null);
		usernameLabel.setOpaque(true);
		panel.add(usernameLabel);
		
		
		choosePassword = new Label("password","Choose Password",159,212,116,14,SwingConstants.CENTER,null,GameFonts.NULL,null);
		choosePassword.setOpaque(true);
		panel.add(choosePassword);

		
		validatePassword = new Label("validatePassword","Re-enter Password",159,253,116,14,SwingConstants.CENTER,null,GameFonts.NULL,null);
		validatePassword.setOpaque(true);
		panel.add(validatePassword);
		
		enterEmail = new Label("email","Email Address",159,294,116,14,SwingConstants.CENTER,null,GameFonts.NULL,null);
		enterEmail.setOpaque(true);
		panel.add(enterEmail);
		
		homeNation = new Label("homeNation","Home Nation",159,335,116,14,SwingConstants.CENTER,null,GameFonts.NULL,null);
		homeNation.setOpaque(true);
		panel.add(homeNation);
		
		errorMsg = Main.setScreenErrorMsg();
		errorMsg.setLocation(errorMsg.getX(),errorMsg.getY() + 20);
		panel.add(errorMsg);
		LogoLabel = Main.setScreenLogoLabel();
		panel.add(LogoLabel);
		background = Main.setScreenBackground();
		panel.add(background);
	}
	/**
	 * clears all the text and password fields
	 * 
	 * 	 */
	public static void clearFields() {
		passwordField.setText("");
		password2Field.setText("");
		usernameField.setText("");
		emailField.setText("");
		errorMsg.setText("");
	}
	/**
	 * @param field field
	 * has the given field listen for an enter key press to submit the account information
	 * 	 */
	private static void signByEnter(JComponent field) {
		field.addKeyListener( new KeyAdapter() {
	        public void keyReleased(KeyEvent e) {
	        	if (e.getKeyCode() == KeyEvent.VK_ENTER)
	        		sign();
	        }
		});
	}
	/**
	 * submits the account information
	 * 
	 * 	 */
	private static void sign() {
		boolean isValidSign = checkAndRegisterUser(usernameField.getText(),new String(passwordField.getPassword()),
				new String(password2Field.getPassword()),emailField.getText());
		if(isValidSign) {
			if (!isEdit)
				Main.executorService.execute(Player.sendAccountActivatedEmail());
			
			clearFields();
			panel.setVisible(false);
			LoginMenu.logUser();
		}
		else {
			Main.executorService.execute(Main.deleteMsg(errorMsg));
		}
	}
	/**
	 * submits the account information to be edited to an existing user
	 * 
	 * 	 */
	public static void editUser() {
		usernameField.setText(Player.getUsername());
		emailField.setText(Player.getEmail());	
		nationsList.setSelectedItem(Player.getNation());
		
		SignUp.usernameField.setEditable(false);
	}
	/**
	 * @param username username
 	 * @param password password
 	 * @param password2 password2
 	 * @param email email
	 * checks to see if the username, passwords and email given are not taken, that the password match and that the email is valid (if it exists)
	 * 
	 * @return <code>true</code> if registration was successful 
	 * 		   <code>false</code> else
	 */
	public static boolean checkAndRegisterUser(String username,String password,String password2,String email) {
		
		if (isValidSign(username,password, password2, email)) {
			try {
				if (isEdit) {
					Queries.editUser(password,email,(String) nationsList.getSelectedItem());
				}
				else {
					Queries.addUser(username, password, email, (String) nationsList.getSelectedItem());
				}
				return true;
			} catch (SQLException e) {
				errorMsg.setText(Queries.QUERY_FAILED_MESSAGE);
				return false;
			}	
		}
		return false;
	}
	
	public static boolean isValidSign(String username,String password,String Repassword,String email) {
		return isValidUsername(username) && isValidPassword(password, Repassword) && isValidEmail(email);
	}
	
	public static boolean isValidUsername(String username) {
		return !isFieldEmpty(username) && !isUserAlreadyExist(username);
	}
	
	private static boolean isFieldEmpty(String field) {
		if (field.equals("")) {
			errorMsg.setText("Username or Password are empty! Please try again");
			return true;
		}
	return false;
	}

	public static boolean isPasswordMatch(String password,String RePassword) {
		if (!password.equals(RePassword)) {
			errorMsg.setText("Passwords don't match, Please try again");
			return false;
		}
		return true;
	}

	public static boolean isValidPassword(String password,String RePassword) {
		return !isFieldEmpty(password) && isPasswordMatch(password,RePassword);
	}

	public static boolean isValidEmail(String email) {
		if (!isValidEmailAddress(email)) {
			errorMsg.setText("Email entered is not vaild! Please try again");
			return false;
		}
		return true;
	}
	
	public static boolean isUserAlreadyExist(String username) {
		if (isEdit)
			return false;
		try {
			if(Queries.isUserExist(username)) {
				errorMsg.setText("Username is alredy taken! Please choose another");
				return true;
			}
		} catch (SQLException e) {
			errorMsg.setText("ERROR: Unable to register user. Please try again later");
			return false;
		}	
		return false;
	}
	/**
	 * @param email email
	 * checks to see the email is valid
	 * 
	 * @return <code>true</code> if email is valid
	 * 		   <code>false</code> else
	 */
	public static boolean isValidEmailAddress(String email) {
		if (email.equals(""))
			return true;
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
	}
}
