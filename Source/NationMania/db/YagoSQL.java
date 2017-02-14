package NationMania.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

import NationMania.core.Config;
import NationMania.parsing.YagoCaption;
import NationMania.parsing.YagoEntryFact;
import NationMania.parsing.YagoEntryNation;
import NationMania.parsing.YagoParser;
import NationMania.parsing.YagoUpdate;

/**
 * This Class contains all the YagoUpdate Queries
 *
 */
public class YagoSQL {
	
	/**
	 * The name of the TAU server - used to prevent unauthorized update to main server
	 */
	private static final String MYSQL_SERVER =  !Config.GET_VAR_STR("MYSQL_SERVER").equals("NA") ? Config.GET_VAR_STR("MYSQL_SERVER") : "anna" ;
	Connection conn; // DB connection
	
	/**
	 * Check if connected to server - if so, don't allow update!
	 * @return <code>true</code> if connected to MYSQL_SERVER
	 * 		   <code>false</code> else
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 */
	public boolean CheckIfServer() throws SQLException {
		
		try {
			getConnection();
		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;
		}	
		
		//conn = Queries.Connections.getConnection();
		
		String query = "SELECT @@hostname";
		
		try(Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);) {

			if (rs.next() == true) {
				return rs.getString(1).equals(MYSQL_SERVER);
			}
			
		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;		}
		finally {
			Queries.Connections.freeConnection(conn);
		}
		return false;
	}
	
	/**
	 * Inserts all nations to the database <br>
	 * (a default continent "El mundo" is inserted first)
	 * @param ye YagoEntryNation
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 */
	public void yagoInsertNations(YagoEntryNation[] ye) throws SQLException {
		
		try {
			getConnection();
		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;		}	
		
		//conn = Queries.Connections.getConnection();
		String query = "INSERT INTO continents (continent_name) VALUES (\"El mundo\")";
		try(Statement stmt = conn.createStatement();) {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;
			} finally {
			Queries.Connections.freeConnection(conn);
		}
		
		try (PreparedStatement pstmt = conn.prepareStatement("REPLACE INTO nations(nation_name,nation_caption, nation_weight, continent_id) VALUES(?,?,?,?)");) {
			conn.setAutoCommit(false);

			int i = 0;
			while (i < ye.length) {
				if(ye[i] == null) {
					break;
				}
				String nation_name = ye[i].nationName;
				String nation_caption = YagoCaption.getCaption(nation_name);
				pstmt.setString(1,nation_name );
				pstmt.setString(2,nation_caption );
				pstmt.setInt(3, ye[i].weight);
				pstmt.setInt(4, 1); //Implement Continents
				pstmt.addBatch();
				i++;
			}

			pstmt.executeBatch();
			conn.commit();

		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;
		} finally {
			safelySetAutoCommit();
			Queries.Connections.freeConnection(conn);
		}
	}
	
	/**
	 * Inserts all facts to the database <br>
	 * There are generally a lot of facts to insert ~300K<br>
	 * So this is done in batches
	 * @param ye YagoEntryFact
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 */
	public void yagoInsertFacts(YagoEntryFact[] ye) throws SQLException {
		
		try {
			getConnection();
		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;
		}	

		try (PreparedStatement pstmt = conn.prepareStatement("INSERT IGNORE INTO facts(fact_name,fact_caption, fact_weight, nation_id, relation_type_id, fact_type_id, fact_reported) VALUES(?,?,?,(SELECT nation_id FROM nations WHERE nation_name=?),(SELECT relation_type_id FROM relation_types WHERE relation_type_name=?),(SELECT fact_type_id FROM fact_types WHERE fact_type_name=?),?)");) {
			conn.setAutoCommit(false);

			int i = 0;
			while (i < ye.length) {	
				if(ye[i] == null) {
					break;
				}
				pstmt.clearBatch();
				while (i < ye.length) {
					if(ye[i] == null) {
						break;
					}
					String fact_name = ye[i].factName;
					String fact_caption = YagoCaption.getCaption(fact_name);
					pstmt.setString(1,fact_name);
					pstmt.setString(2,fact_caption);
					pstmt.setInt(3, ye[i].weight);
					pstmt.setString(4,ye[i].nation);
					pstmt.setString(5, ye[i].relation);
					pstmt.setString(6, ye[i].factType);
					pstmt.setInt(7, 0); //hardcoded - fact_reported at the beginning is 0 always 
					pstmt.addBatch();
					i++;
					if(i%25000==0){
						YagoUpdate.setProgress(70 + i / 25000);
					}
					if(i%10000==0){
						//insert in batches
						break;
						
					}
				}
				pstmt.executeBatch();
				conn.commit();
				YagoUpdate.setStatus2(String.format("commited - %d\n",i));
			}
		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;
			
		} finally {
			safelySetAutoCommit();
			Queries.Connections.freeConnection(conn);
		}
	}
	
	/**
	 * Inserts all relation types to the database <br>
	 * This method first uniquifies the types, then inserts them
	 * @param ye YagoEntryFact
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 */
	public void yagoInsertRelationTypes(YagoEntryFact[] ye) throws SQLException {
		
		try {
			getConnection();
		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;
		}	
		

		try (PreparedStatement pstmt = conn.prepareStatement("INSERT IGNORE INTO relation_types(relation_type_name, relation_type_caption, relation_type_weight) VALUES(?,?,?)");) {
			conn.setAutoCommit(false);

			int i = 0;
			int j = 0;
			HashMap<String, Integer> relationType = new HashMap<String, Integer>();
			pstmt.clearBatch();
			while (i < ye.length) {
				if(ye[i] == null) {
					break;
				}
				
				String type_name = ye[i].relation;
				String type_caption = YagoCaption.getCaption(type_name);

				if ( !relationType.containsKey(type_name)){
				//Type not found yet
					pstmt.setString(1,type_name);
					pstmt.setString(2,type_caption);
					pstmt.setInt(3, (YagoParser.waiverWeightHash.containsKey(type_name))? (int) YagoParser.waiverWeightHash.get(type_name) : 5);
					pstmt.addBatch();
					
					relationType.put(type_name,1);
					j++;
				}
				i++;

			}
			pstmt.executeBatch();
			conn.commit();
			YagoUpdate.setStatus2(String.format("commited - %d\n",j));

		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;
		} finally {
			safelySetAutoCommit();
			Queries.Connections.freeConnection(conn);
		}
	}
	
	/**
	 * Inserts all fact types to the database <br>
	 * This method first uniquifies the types, then inserts them
	 * @param ye YagoEntryFact
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 */
	public void yagoInsertFactTypes(YagoEntryFact[] ye) throws SQLException {
		
		try {
			getConnection();
		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;
		}	
 
		try (PreparedStatement pstmt = conn.prepareStatement("INSERT IGNORE INTO fact_types(fact_type_name, fact_type_caption, fact_type_weight) VALUES(?,?,?)");) {
			conn.setAutoCommit(false);

			int i = 0;
			int j = 0;
			HashMap<String, Integer> factType = new HashMap<String, Integer>();
			pstmt.clearBatch();
			while (i < ye.length) {
				if(ye[i] == null) {
					break;
				}
				
				String type_name = ye[i].factType;
				String type_caption = YagoCaption.getCaption(type_name);

				if ( !factType.containsKey(type_name)){
				//Type not found yet
					pstmt.setString(1,type_name);
					pstmt.setString(2,type_caption);
					pstmt.setInt(3, 1);
					pstmt.addBatch();
					
					factType.put(type_name,1);
					j++;
					
				}
				i++;

			}
			pstmt.executeBatch();
			conn.commit();
			YagoUpdate.setStatus2(String.format("commited - %d\n",j));

		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;
		} finally {
			safelySetAutoCommit();
			Queries.Connections.freeConnection(conn);
		}
	}
	
	/**
	 * Attempts to set the connection back to auto-commit, ignoring errors.
	 */
	private void safelySetAutoCommit() {
		try {
			conn.setAutoCommit(true);
		} catch (Exception e) {
		}
	}




	/**Runs a given SQL script on the server
	 * @param path path to a SQL script
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 */
	public void yagoRunSQLScript(String path) throws SQLException {
		
		try {
			getConnection();
		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw e;
		}	
		
		YagoSQLScript runner = new YagoSQLScript(conn, false, true);

		try{
			runner.runScript(new BufferedReader(new FileReader(path)));
	
		} catch (SQLException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw new SQLException("ERROR Running Script " + path + " - " + e.getMessage());
		}catch (IOException e) {
			//YagoUpdate.secureUpdate will catch this exception
			throw new SQLException("ERROR Running Script " + path + " - " + e.getMessage());
		} finally {
			Queries.Connections.freeConnection(conn);
		}
		
		/**
		 * test if there is connection to DB at the start of the application.
		 * If received no callback from server - throw exception
		 */
	}
	
	/**
	 * Receives a connection from the connection pool.
	 * @throws SQLException Signals that the SQL query or connection failed, A message follows.
	 */
	public void getConnection() throws SQLException {
		conn = Queries.Connections.getConnection();
		if (conn == null) {
			throw new SQLException("ERROR: Unable to connect to SQL Server!");
		}
	}
}
