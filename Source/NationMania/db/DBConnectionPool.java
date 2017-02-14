package NationMania.db;
import java.sql.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import NationMania.core.Config;

/**
 * Class Containing implementation of Connection Pool for getting connections to communicate with DB affectively.
 * 	 */
public final class DBConnectionPool {
	
	/**
	 * Configuration Constant - sets the maximum number of parallel connections to the DB 
	 */
	public static int MAX_CONNECTIONS =  Config.GET_VAR_INT("MAX_CONNECTIONS") == -1 ? 10 : Config.GET_VAR_INT("MAX_CONNECTIONS");
	

	/**
	 * Vector at the size of MAX_CONNECTIONS that represents the number of free connections to use
	 */
	private Vector<Connection> freeConnections;		//Vector of free connections ready to use
	private String URL;								//URL of SQL DB to connect
	private String user;							//credentials for DB - user
	private String password;						//credentials for DB - password
	private int maxConnections;						//max number of connections at the same time
	private int busy;								//non free connections
	
    /**Constructor
     * @param URL SQL server URL
     * @param user SQL server user name
     * @param password SQL server password
     * @param maxConnections amount of connections
     */
    public DBConnectionPool(String URL, String user,
            String password, int maxConnections) {
        this.URL = URL;
        this.user = user;
        this.password = password;
        this.maxConnections = maxConnections;
        busy = 0;
        freeConnections = new Vector<Connection>();
    }
	
    //get free connection from pool
    /**Called by every query and in any new attemp to connect with the DB.
     * If there is available free connection that is already open in the freeConnections vector - it uses it.
     * Else, try to open a new connection to the DB
     * @return the connection if connecting succeeded, and null otherwise
     */
    public synchronized Connection getConnection() {
        Connection con = null;
        if (freeConnections.size() > 0) {
            // Pick the first Connection in the Vector
            con = freeConnections.firstElement();
            freeConnections.removeElementAt(0);
            try {
            	
            	//test if connection is still valid
            	if (con == null || !con.isValid(1000)) {
                    // Try again recursively
                    con = getConnection();
                }
            }
            catch (SQLException e) {
            }
        }
        else if (maxConnections == 0 || busy < maxConnections) {
            con = newConnection();
        }
        if (con != null) {
        		busy++;
        }
        return con;
    }
    
    //no connections available - create new connection
    private Connection newConnection() {
        Connection con = null;
        try {
            if (user == null) {
                con = DriverManager.getConnection(URL);
            }
            else {
                con = DriverManager.getConnection(URL, user, password);
            }
        }
        catch (SQLException e) {
            return null;
        }
        return con;
    }
    
    //optional - wait for connection but abort if timeout
    public synchronized Connection getConnection(long timeout) {
        long startTime = new Date().getTime();
        Connection con;
        while ((con = getConnection()) == null) {
            try {
            	 wait(timeout);
            }
            catch (InterruptedException e) {}
            if ((new Date().getTime() - startTime) >= timeout) {
                // Timeout has expired
                return null;
            }
        }
        return con;
    }
    

    /**Called at the end of every query.
     * Free the connection by sending it back to the pool, making it available for use
     * @param con connection to be freed
     */
    public synchronized void freeConnection(Connection con) {
        // Put the connection at the end of the Vector
        freeConnections.addElement(con);
        busy--;
        notifyAll();
    }
    

    /**
     * Called when user hit the "quit" button in the Main Menu screen.
     * Close all open connections to the DB
     */
    public synchronized void shutDown() {
        Enumeration<Connection> allConnections = freeConnections.elements();
        while (allConnections.hasMoreElements()) {
            Connection con = (Connection) allConnections.nextElement();
            try {
                con.close();
            }
            catch (SQLException e) {
    			System.out.println("Unable to close the connection - " + e.getMessage());
            }
        }
        freeConnections.removeAllElements();
    }
}
