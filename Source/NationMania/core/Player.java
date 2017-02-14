package NationMania.core;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Static Class containing all the information needed about the logged user.
 * 
 * 	 */
public final class Player {
	
	
	/**
	 * Final value - the email address from which the application sends the activation email to the user
	 */
	private final static String NATION_MANIA_EMAIL = "me@example.com";

	/**
	 * Final value - the password for the NATION_MANIA_EMAIL
	 */
	private final static String NATION_MANIA_PASSWORD = "password";
	

	private static int ID;									//unique ID per user
	private static String username;							//unique username (limitations on the string)?
	private static String email;							//player email
	private static String nation;							//player home nation
	private static int topScore;							//best score ever
	private static ArrayList<Integer> playedFacts;			//all the facts presented to the user
	private static ArrayList<Integer> playedNations;		//all the nations the user played already
	
	/**Constructor
	 * @param ID player's unique ID
	 * @param username user name
	 * @param email player's email
	 * @param nation player's home nation
	 * @param topScore player's top score
	 */
	public Player(int ID, String username,String email,String nation,int topScore) {
		Player.ID = ID;
		Player.username = username;
		Player.email = email;
		Player.nation = nation;
		Player.topScore = topScore;
		playedFacts = new ArrayList<Integer>();
		playedNations = new ArrayList<Integer>();
	}
	
	/**
	 * @return player ID
	 */
	public static int getID() {
		return ID;
	}

	/**
	 * @return player username - the name is uniqe and cannot be replaced
	 */
	public static String getUsername() {
		return username;
	}

	/**Called when the player updates his account.
	 * Sets player email.
	 * @param email the email entered by the player when he edit his account.
	 */
	public static void setEmail(String email) {
		Player.email = email;
	}
	
	/**
	 * @return Player email address. If user did not enter email, the field holds an empty string.
	 */
	public static String getEmail() {
		return email;
	}
	
	/**
	 * @return Player home nation
	 */
	public static String getNation() {
		return nation;
	}
	
	/**Called when the player updates his account.<br>
	 * Sets player home nation
	 * @param nation the nation the user chose on update account screen
	 */
	public static void setNation(String nation) {
		Player.nation = nation;
	}
	
	/**
	 * @return Player best score in all games since he registered
	 */
	public static int getTopScore() {
		return topScore;
	}
	
	/** 
	 * Called on the end of a game, if the total score is higher from the player current top score<br>
	 * Sets player top score
	 * @param topScore the score the player achieved during the last game
	 */
	public static void setTopScore(int topScore) {
		Player.topScore = topScore;
	}
	
	/**
	 * @return list containing all the facts the user played in specific recent time. The value of recent time is determined<br>
	 * in the Configuration file and is set by default to 1 week.<br>
	 * This is used for improving game experience, by not presenting the user facts he already saw.
	 * 
	 */
	public static ArrayList<Integer> getPlayedFacts() {
		return playedFacts;
	}

	/**Updates the nation thats played this round in the player's nations history list.<br>
	 * The process is done by a different thread.
	 * @param nationID the ID of the current round nation
	 * @return Runnable - use to start new thread via the Executer.
	 */
	public static Runnable updatePlayerNationsHistory(int nationID) {
		return new Runnable() {
			
			public void run() {
				if (!playedNations.contains(nationID))
					playedNations.add(nationID);
			}	
		};
	}

	/**Updates the fact that displayed to the player in the player's facts history list.<br>
	 * Every fact in this list will not be presented to the player anymore.<br>
	 * The process is done by a different thread.<br>
	 * @param factID the ID of the current displayed fact
	 * @return Runnable - use to start new thread via the Executer.
	 */
	public static Runnable updatePlayerFactsHistory(int factID) {
		return new Runnable() {
			
			public void run() {
				playedNations.add(factID);		
			}
		};
	}
	

	/**Called immediately after user sign up.<br>
	 * In case the user entered email, an activation email is sent to the user.<br>
	 * @return Runnable - use to start new thread via the Executer.
	 */
	public static Runnable sendAccountActivatedEmail() {
		return new Runnable() {
			public void run() {
				if (email.equals("")) 		//user did not signed up with mail
					return;
				
				//set configurations to create connection to the email address
				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.port", "587");
		 
				//Perform authentication on the NATION_MANIA_EMAIL
				Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(NATION_MANIA_EMAIL, NATION_MANIA_PASSWORD);
					}
				  });
		 
				//send activation message the the email the user entered
				try {
					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress(NATION_MANIA_EMAIL));
					message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(email));
					message.setSubject("Your Nation Mania Account has been activated!");
					message.setText("Dear " + username + ","
						+ "\n\n We welcome you to our worldwide trivia game\n We hope you will enjoy it like us :)");
		 
					Transport.send(message);
		 
				} catch (MessagingException e) {
					//do nothing - ignore failed attemps to send email
				}
			}
		};
	}

}
