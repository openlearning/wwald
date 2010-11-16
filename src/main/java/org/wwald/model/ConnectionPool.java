package org.wwald.model;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;


public class ConnectionPool {
	
	private static Map<String, Connection> connections;
	private static String username;
	private static String password;
	private static String preurl;
	private static Properties dbProps;
	private static Logger cLogger = Logger.getLogger(ConnectionPool.class);
	
	static {
		try {
			connections = new HashMap<String, Connection>();
			
			InputStream propStream = 
				ConnectionPool.class.
					getClassLoader().
						getResourceAsStream("db.properties");
			dbProps = new Properties();
			dbProps.load(propStream);
			
			preurl = dbProps.getProperty("db.url");
			username = dbProps.getProperty("db.user");
			password = dbProps.getProperty("db.password");
			
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
		} catch(Exception e) {
			cLogger.error("Could not initialize database connection ", e);
		}
	}
	
	public static synchronized Connection getConnection(String id) {
		Connection conn = connections.get(id);
		if(conn == null) {
			String dbId = dbProps.getProperty(id);
			if(dbId != null && !dbId.equals("")) {
				String couldNotObtainConnMsg = "Could not get connection to database mapped to '" + id + "'"; 
				String url = preurl + dbId;
				try {
					conn = DriverManager.getConnection(url, username, password);
				} catch(SQLException sqle) {
					cLogger.error(couldNotObtainConnMsg, sqle);
				}
				if(conn != null) {
					connections.put(id, conn);
				}
				else {
					cLogger.error(couldNotObtainConnMsg);
				}
			}
			
		}
		
		return conn;
	}

	public static String getDatabaseIdFromRequestUrl(String requestUrl) {
		String HTTP = "http://";
		String HTTPS = "https://";
		if(requestUrl == null) {
			throw new IllegalArgumentException("RequestUrl cannot be null");
		}
		String id = "";
		if(requestUrl.startsWith(HTTP)) {
			id = requestUrl.substring(HTTP.length(), requestUrl.length());
		}
		else if(requestUrl.startsWith(HTTPS)) {
			id = requestUrl.substring(HTTPS.length(), requestUrl.length());
		}
		else {
			cLogger.warn("requestUrl does not start with " + HTTP + " or " + HTTPS + " '" + requestUrl + "'");
		}
		int index = -1;
		index = id.indexOf(':');
		if(index != -1) {
			id = id.substring(0, index);
		}
		index = id.indexOf('/');
		if(index != -1) {
			id = id.substring(0, index);
		}
		
		return id;
	}
}
