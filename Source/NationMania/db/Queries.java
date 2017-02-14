package NationMania.db;

import java.sql.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Calendar;

import NationMania.core.Config;
import NationMania.core.Fact;
import NationMania.core.Game;
import NationMania.core.Nations;
import NationMania.core.Player;
import NationMania.core.SortedFacts;
import NationMania.core.State;
import NationMania.ui.HighScore;

/**
 * DB Queries Class - holds all the Queries related to java core implementation.
 * Performs updates,inserts and selects on DB, and connect data with java Data Structures
 * Gets connections for Connection Pool
 * 	 */

public final class Queries {

	public static String URL = Config.GET_VAR_STR("URL").equals("NA") ? "jdbc:mysql://localhost:3305/DbMysql05" : Config.GET_VAR_STR("URL");
	public static String USER = Config.GET_VAR_STR("USER").equals("NA") ? "DbMysql05" : Config.GET_VAR_STR("USER");
	public static String PASSWORD =  Config.GET_VAR_STR("PASSWORD").equals("NA") ? "DbMysql05" :  Config.GET_VAR_STR("PASSWORD");
	public static String HISTORY_TIME = Config.GET_VAR_STR("HISTORY_TIME").equals("NA") ? "1 WEEK" : Config.GET_VAR_STR("HISTORY_TIME");
	public static int TOP_SCORE_TABLE_SIZE = Config.GET_VAR_INT("TOP_SCORE_TABLE_SIZE") == -1 ? 10 : Config.GET_VAR_INT("TOP_SCORE_TABLE_SIZE");
	
	
	public static String CONNECTION_FAILED_MESSAGE = "ERROR: Unable to connect to SQL Server!";
	public static String QUERY_FAILED_MESSAGE = "";
	
	public static DBConnectionPool Connections = new DBConnectionPool(URL,USER,PASSWORD,DBConnectionPool.MAX_CONNECTIONS);
	public static Connection conn;
	 
	/**
	 * this function will be called once at the beginning. upload all countries from DB to class
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 */
	public static void getCountries() throws SQLException {
		
		String query = "SELECT nation_id,nation_caption,nation_weight,continents.continent_name FROM nations,continents "
				+ "WHERE nations.continent_id = continents.continent_id "
				+ "ORDER BY nation_weight";
		
		try {
			getConnection();
		} catch (SQLException e) {
			throw e;
		}

		try(Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query);) {
					
			if (!rs.isBeforeFirst()) {
				QUERY_FAILED_MESSAGE = "ERROR: No Countries where found! Please Update Database!";
				return;
			}
			
			while (rs.next() == true) {
					new State(rs.getInt(1),rs.getString(2),rs.getDouble(3),rs.getString(4));
			}

		} catch (SQLException e) {
			QUERY_FAILED_MESSAGE = "ERROR: Unable to load nations from Database! Exiting...";
			throw e;
		}
		finally {
			Connections.freeConnection(conn);
		}
	}
	
	/**
	 * log user from DB as the current player
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 * @param username user name
	 */
	public static void logUser(String username) throws SQLException {
		
		String query = "SELECT user_id,username,email,nations.nation_caption,top_score "
				+ "FROM users,nations "
				+ "WHERE nations.nation_id = users.home_nation_id "
				+ "AND username = ?";
		
		try { 
			getConnection();
		} catch (SQLException e) {
			throw e;
		}
		
		try (PreparedStatement pstmt = conn.prepareStatement(query);)
		{
			pstmt.setString(1,username);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next() == true) {
				new Player(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getInt(5));
			}

		} catch (SQLException e) {
			throw e;
		}
		finally {
			Connections.freeConnection(conn);
			updateUserLogin(username);
		}
	}
	
	/**
	 * Called at the end of a game, or after pressing "quit" on game screen.
	 * logs the final statistics of the game - total score and number of nations discovered
	 * 
	 *@param GameID Game ID
	 */
	public static void logGameStats(int GameID) {
		
		//update the last nation played
		logState(Nations.getMyLand().getID());
		
		String query = "UPDATE games SET user_id = ?, game_score = ?, nations_discovered = ? WHERE game_id = ?";
		
		try {
			getConnection();
		} catch (SQLException e) {
			//ignore invalid connection on logging game to DB
			return;
		}
		
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			
			pstmt.setInt(1,Player.getID());
			pstmt.setInt(2,Game.getTotalScore());
			pstmt.setInt(3,Game.getStatesDiscovered());
			pstmt.setInt(4,GameID);
			
			pstmt.executeUpdate();

		} catch (SQLException e) {
			//not mandatory for game
		}
		finally {
			Connections.freeConnection(conn);
		}
	}
	
	/**
	 * Called in the start of every new game. Creates a new entry in the DB about the game, and setting it with id.
	 * 
	 */
	public static void logGame() {
		
		String query = "INSERT INTO games(user_id,game_score,nations_discovered) VALUES(?,?,?)";

		try {
			getConnection();
		} catch (SQLException e) {
			//ignore invalid connection on logging game to DB
			return;
		}
		
		try (PreparedStatement pstmt = conn.prepareStatement(query,new String[] { "ID" })) {
			
			pstmt.setInt(1,Player.getID());
			pstmt.setInt(2,Game.getTotalScore());
			pstmt.setInt(3,Game.getStatesDiscovered());

			pstmt.executeUpdate();
			
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			Game.setGameID(rs.getInt(1));

		} catch (SQLException e) {
			//not mandatory for game
		}
		finally {
			Connections.freeConnection(conn);
		}
	}
	
	/**
	 * Called after the end of every round.
	 * Log the nation and it's facts that appeared in this round of the game
	 * @param stateID State ID
	 */
	public static void logState(int stateID) {
		
		String query = "INSERT INTO game_info(game_id,nation_id,fact_id) VALUES(?,?,?)";
		
		try {
			getConnection();
		} catch (SQLException e) {
			//not mandatory for game
		}

		try (PreparedStatement pstmt = conn.prepareStatement(query);) {

			conn.setAutoCommit(false);

			ArrayList<Integer> playedFacts = Game.getGameNationsAndFacts().get(stateID);

			//log all facts except the last one (was set but not displayed)
			for (int i = 0; i < playedFacts.size() - 1; i++) {
				pstmt.setInt(1,Game.getGameID());
				pstmt.setInt(2,stateID);
				pstmt.setInt(3,playedFacts.get(i));
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();

		} catch (SQLException e) {
			//not mandatory for game
			
		} finally {
			safelySetAutoCommit();
			Connections.freeConnection(conn);
		}
	}
	
	/**Called immediatly after the player report on a fact during the game.
	 * Increases by 1 the number of reports on the fact in DB.
	 * @param factID the ID of the reported fact
	 */
	public static void updateFactsReported(int factID) { 
		String query = "UPDATE facts SET fact_reported = fact_reported + 1 WHERE fact_id = ?";
		
		try {
			getConnection();
		} catch (SQLException e) {
			//ignore invalid connection on reporting facts
			return;
		}
		
		try (PreparedStatement pstmt = conn.prepareStatement(query);) {
				
			pstmt.setInt(1,factID);
					
			pstmt.executeUpdate();
				
		} catch (SQLException e) {
			//not mandatory for game
		}
		finally {
			Connections.freeConnection(conn);
		}
	}
	
	/**
	 * Called if the player sets a new best score.
	 * Updates his best score in the DB.
	 */
	public static void updateTopScore() {	

		String query = "UPDATE users SET top_score = ? WHERE user_id = ?";
		
		try {
			getConnection();
		} catch (SQLException e) {
			//ignore invalid connection on updating top score
			return;
		}
		
		try (PreparedStatement pstmt = conn.prepareStatement(query);) {
			
			pstmt.setInt(1,Game.getTotalScore());
			pstmt.setInt(2,Player.getID());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			//not mandatory for game
		}
		finally {
			Connections.freeConnection(conn);
		}
}
	
	/**
	 * get from DB all categories for the chosen state 
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 * @param nation nation
	 */
	public static void getStateFacts(State nation) throws SQLException {	
		
		String query = "SELECT fact_id,fact_caption,fact_types.fact_type_caption, "
				+ "relation_types.relation_type_caption, "
				+ "fact_weight * fact_types.fact_type_weight * relation_types.relation_type_weight AS total_weight, "
				+ "nations.nation_weight * nations.nation_weight AS num_rows "
				+ "FROM facts,fact_types,relation_types,nations "
				+ "WHERE facts.fact_type_id = fact_types.fact_type_id "
				+ "AND facts.relation_type_id = relation_types.relation_type_id "
				+ "AND facts.nation_id = nations.nation_id "
				+ "AND facts.nation_id = '" + nation.getID() +"'"
				+ "AND fact_reported < " + SortedFacts.MIN_REPORTED + " "
				+ "ORDER BY total_weight DESC";
		
		try {
			getConnection();
		} catch (SQLException e) {
			throw e;
		}
		
		try(Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query);) {
			
			if (!rs.isBeforeFirst()) {
				QUERY_FAILED_MESSAGE = "ERROR: No Facts where found!";
				throw new SQLException();
			}
			
			while (rs.next() == true) {
				
				//first fact holds max weight
				if (rs.isFirst()) {
					nation.getStateFacts().setMaxWeight(rs.getInt(5));
					nation.getStateFacts().setEasyFactsSize(rs.getInt(6));
				}
				
				Fact fact = new Fact(rs.getInt(1), rs.getString(2), rs.getString(3),rs.getString(4),rs.getInt(5));
				
				nation.getStateFacts().addFact(fact);
				}

		} catch (SQLException e) {
			QUERY_FAILED_MESSAGE = "ERROR: Unable to load state's facts!";
			throw e;
		}
		finally {
			Connections.freeConnection(conn);
		}
	}
	
	/**
	 * Sign new user
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 * @param username user name
	 * @param password user password
	 * @param email email
	 * @param homeNation player's home nation
	 */
	public static void addUser(String username,String password,String email,String homeNation) throws SQLException {

		String query = "INSERT INTO users(username,password,email,top_score,home_nation_id) VALUES(?,?,?,?,?)";
		
		try {
			getConnection();
		} catch (SQLException e) {
			throw e;
		}
		
		try (PreparedStatement pstmt = conn.prepareStatement(query);) {
			
			int homeNationdId = getIDfromName(homeNation);
			
			pstmt.setString(1,username);
			pstmt.setString(2,MD5(password));
			pstmt.setString(3,email);
			pstmt.setInt(4,0);
			pstmt.setInt(5,homeNationdId);
			
			pstmt.executeUpdate();

		} catch (SQLException e) {
			QUERY_FAILED_MESSAGE = "ERROR: Unable to register user!";
			throw e;
		}
		finally {
			Connections.freeConnection(conn);
			logUser(username);
		}
	}
	
	/**
	 * Edit user account - can change password,email,home nation
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 * 
	 * @param password user password
	 * @param email email
	 * @param homeNation player's home nation
	 */
	public static void editUser(String password,String email,String homeNation) throws SQLException {
		
		String query = "UPDATE users SET password = ?, email = ?, home_nation_id = ? "
				+ "WHERE username = ?";
		
		try {
			getConnection();
		} catch (SQLException e) {
			throw e;
		}
		
		try (PreparedStatement pstmt = conn.prepareStatement(query);) {
//					
			int homeNationID = getIDfromName(homeNation);
			
			pstmt.setString(1,MD5(password));
			pstmt.setString(2,email);
			pstmt.setInt(3,homeNationID);
			pstmt.setString(4,Player.getUsername());
			
			pstmt.executeUpdate();

			//update values on player class too
			Player.setNation(homeNation);
			Player.setEmail(email);

		} catch (SQLException e) {
			throw e;
		}
		finally {
			Connections.freeConnection(conn);
			logUser(Player.getUsername());
		}
	}

	/**
	 * Match State ID to input State name
	 * @param name Name of nation
	 * @return nation's ID
	 */
	private static int getIDfromName(String name) {
		
		int ID = -1;
		String query = "SELECT nation_id FROM nations WHERE nation_caption = '" + name + "'";
	
		try(Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query);) {
			
			if (!rs.isBeforeFirst()) {
				return ID;
			}
			
			if (rs.next() == true) {
				ID = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			//not mandatory for game - default value -1
			return ID;
		}
		finally {
			Connections.freeConnection(conn);
		}
		return ID;
	}
	
	/**
	 * Called in Sign In
	 * Sign In - return true if username was found and password was a match
	 * username is NOT CASE SENSITIVE!
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 * @return <code>true</code> if user found
	 * 		   <code>false</code> else
	 * @param username user name
	 * @param password user password

	 */
	public static boolean findUser(String username, String password) throws SQLException {

		boolean flag = false;
		String query = "SELECT password FROM users WHERE username = ?";
		
		try {
			getConnection();
		} catch (SQLException e) {
			throw e;
		}
				
		try (PreparedStatement pstmt = conn.prepareStatement(query);)
		{

			pstmt.setString(1,username);
			ResultSet rs = pstmt.executeQuery();
				
			if (rs.next() == true) {
				flag = rs.getString(1).equals(MD5(password));
			}

		} catch (SQLException e) {
			QUERY_FAILED_MESSAGE = "ERROR: Unable to validate user!";
			throw e;
		}
		finally {
			Connections.freeConnection(conn);
		}
		return flag;
	}
	
	/**
	 * Called in Sign Up.
	 * return true if username not found in DB. NOT CASE SENSITIVE!
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 * @return <code>true</code> if user exists
	 * 		   <code>false</code> else
	 * @param username user name
	 */
	public static boolean isUserExist(String username) throws SQLException {
		
		boolean flag = true;
		String query = "SELECT username FROM users WHERE username = ?";
		
		try {
			getConnection();
		} catch (SQLException e) {
			throw e;
		}
		
		try (PreparedStatement pstmt = conn.prepareStatement(query);)
		{
			
			pstmt.setString(1,username);
			ResultSet rs = pstmt.executeQuery();
			
			flag = rs.first();
			
		} catch (SQLException e) {
			QUERY_FAILED_MESSAGE = "ERROR: Unable to validate user!";
			throw e;
		}
		finally {
			Connections.freeConnection(conn);
		}
		return flag;
	}
	
	/**
	 * Calls in the end of login - update user login timestamp to now
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 * @param username user name

	 */
	private static void updateUserLogin(String username) throws SQLException {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String query = "UPDATE users SET last_login = ? WHERE username = ?";
			
		try {
			getConnection();
		} catch (SQLException e) {
			throw e;
		}
//		
		try (PreparedStatement pstmt = conn.prepareStatement(query);) {
			
			//update user last login to now
			pstmt.setString(1,dateFormat.format(cal.getTime()));
			pstmt.setString(2,username);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			//not mandatory for game
		}
		finally {
			Connections.freeConnection(conn);
			//Queries are independant - if first fails preform other anyway
			loadPlayerHistory(Player.getID());
		}
	}
	
	/**
	 * load player games history
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 * @param playerID Player's unique ID
	 */
	private static void loadPlayerHistory(int playerID) throws SQLException {
		
		//load to history all fact user played in the recent time
		String query = "SELECT nation_id,fact_id FROM game_info,games"
				+ " WHERE game_info.game_id = games.game_id "
				+ "AND time_stp BETWEEN DATE_SUB(NOW(), INTERVAL + " + HISTORY_TIME + ") AND NOW() "
				+ "AND user_id = '" + playerID + "'"
				+ "ORDER BY nation_id";
		
		try {
			getConnection();
		} catch (SQLException e) {
			throw e;
		}
			
		try(Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query);) {
			
			while (rs.next() == true) {
				Player.updatePlayerNationsHistory(rs.getInt(1)).run();
				Player.updatePlayerFactsHistory(rs.getInt(2)).run();
			}
			
		} catch (SQLException e) {
			//not mandatory for game
		}
		finally {
			Connections.freeConnection(conn);
		}
	}
	
	/**Called when user press the "highscore" button
	 * Gets the top distincts tuples of (user,score) from the DB and prints it to the High Score screen
	 * The size of the top score table is set by the TOP_SCORE_TABLE_SIZE Configuration value
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 */
	public static void getTopScores() throws SQLException {

		String[] topScores = new String[TOP_SCORE_TABLE_SIZE];
		for (int i = 0; i < TOP_SCORE_TABLE_SIZE; i++)
			topScores[i] = "";
		
		String query = "SELECT DISTINCT users.username,game_score "	
					+ "FROM games,users "
					+ "WHERE users.user_id = games.user_id "
					+ "ORDER BY game_score DESC";
		
		try {
			getConnection();
		} catch (SQLException e) {
			HighScore.setHighScoresLabels(topScores);
			throw e;
		}

		try(Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);) {
		
			while (rs.next() == true && rs.getRow() <= TOP_SCORE_TABLE_SIZE) {		
				topScores[rs.getRow() - 1] = rs.getString(1) + " " + rs.getInt(2);
			}
			
		} catch (SQLException e) {
			HighScore.setHighScoresLabels(topScores);
		}
		finally {
			Connections.freeConnection(conn);
		}
		HighScore.setHighScoresLabels(topScores);
	}
	
	/**
	 * test if there is connection to DB at the start of every query.
	 * If recieved no callback from server - throw exception
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.

	 */
	public static void getConnection() throws SQLException {
		conn = Connections.getConnection();
		if (conn == null) {
			QUERY_FAILED_MESSAGE = CONNECTION_FAILED_MESSAGE;
			throw new SQLException();
		}
	}
	
	/**
	 * ShutDown Connection pool<br>
	 * Close all connections
	 */
	public static void closeAllConnections() {
		Connections.shutDown();
	}
	
	
	/**
	 * Attempts to set the connection back to auto-commit, ignoring errors.
	 */
	private static void safelySetAutoCommit() {
	
		try {
			conn.setAutoCommit(true);
		} catch (Exception e) {
		}
	}
	
	/**
	 * Hash function for users passwords
	 * @param md5 raw string
	 * @return md5 digest of string
	 */
	private static String MD5(String md5) {
		   try {
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(md5.getBytes());
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    }
		    return null;
		}
}
