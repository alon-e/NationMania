//BASED ON CODE: http://pastebin.com/f10584951

package NationMania.db;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.*;

/**
 * This class is a tool in order to run SQL script files <br>
 * It is based on iBATIS Apache project.<br>
 * 
 */
public class YagoSQLScript {

	private static final String DEFAULT_DELIMITER = ";";

	private Connection connection;

	private boolean stopOnError;
	private boolean autoCommit;

	private String delimiter = DEFAULT_DELIMITER;
	private boolean fullLineDelimiter = false;

	/**
	 * Default constructor
	 * @param connection connection
	 * @param autoCommit autoCommit flag
	 * @param stopOnError stopOnError flag
	 */
	public YagoSQLScript(Connection connection, boolean autoCommit,
			boolean stopOnError) {
		this.connection = connection;
		this.autoCommit = autoCommit;
		this.stopOnError = stopOnError;
	}

	/**
	 * Runs an SQL script (read in using the Reader parameter)
	 * 
	 * @param reader
	 *            - the source of the script
	 * @throws IOException Signals that an I/O exception of some sort has occurred. 
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 */
	public void runScript(Reader reader) throws IOException, SQLException {
		try {
			boolean originalAutoCommit = connection.getAutoCommit();
			try {
				if (originalAutoCommit != this.autoCommit) {
					connection.setAutoCommit(this.autoCommit);
				}
				runScript(connection, reader);
			} finally {
				connection.setAutoCommit(originalAutoCommit);
			}
		} catch (IOException e) {
			throw e;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Error running script.  Cause: " + e, e);
		}
	}

	/**
	 * Runs an SQL script (read in using the Reader parameter) using the
	 * connection passed in
	 * 
	 * @param conn
	 *            - the connection to use for the script
	 * @param reader
	 *            - the source of the script
	 * @throws SQLException
	 *             if any SQL errors occur
	 * @throws IOException
	 *             if there is an error reading from the Reader
	 */
	private void runScript(Connection conn, Reader reader) throws IOException,
			SQLException {
		StringBuffer command = null;
		try {
			LineNumberReader lineReader = new LineNumberReader(reader);
			String line = null;
			while ((line = lineReader.readLine()) != null) {
				if (command == null) {
					command = new StringBuffer();
				}
				String trimmedLine = line.trim();
				if (trimmedLine.startsWith("--")) {
					// Do nothing
				} else if (trimmedLine.length() < 1
						|| trimmedLine.startsWith("#")) {
					// Do nothing
				} else if (trimmedLine.length() < 1
						|| trimmedLine.startsWith("--")) {
					// Do nothing
				} else if (!fullLineDelimiter
						&& trimmedLine.endsWith(getDelimiter())
						|| fullLineDelimiter
						&& trimmedLine.equals(getDelimiter())) {
					command.append(line.substring(0, line
							.lastIndexOf(getDelimiter())));
					command.append(" ");
					Statement statement = conn.createStatement();
					if (stopOnError) {
						statement.execute(command.toString());
					} else {
						try {
							statement.execute(command.toString());
						} catch (SQLException e) {
							throw e;
						}
					}
					if (autoCommit && !conn.getAutoCommit()) {
						conn.commit();
					}
					command = null;
					try {
						statement.close();
					} catch (Exception e) {
						// Ignore to workaround a bug in Jakarta DBCP
					}
					Thread.yield();
				} else {
					command.append(line);
					command.append(" ");
				}
			}
			if (!autoCommit) {
				conn.commit();
			}
		} catch (SQLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			conn.rollback();
		}
	}

	private String getDelimiter() {
		return delimiter;
	}
}