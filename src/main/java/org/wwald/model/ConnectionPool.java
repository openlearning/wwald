package org.wwald.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.util.PropertyDirMap;
import org.wwald.util.WWALDProperties;


public class ConnectionPool {
	
	private static Map<String, Connection> connections;
	private static Logger cLogger = Logger.getLogger(ConnectionPool.class);
	
	static {
		try {
			connections = new HashMap<String, Connection>();			
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
		} catch(Exception e) {
			cLogger.error("Could not initialize database connection ", e);
		}
	}
	
	public static synchronized Connection getConnection(String id) {
		Connection conn = connections.get(id);
		if(conn == null) {
			try {
				Properties dbProps = new WWALDProperties(id, WWALDProperties.DATABASE_PROPERTIES);
				String url = dbProps.getProperty("db.url");
				String username = dbProps.getProperty("db.user");
				String password = dbProps.getProperty("db.password");
				cLogger.info("Creating connection '" + url + "', '" + username + "', " + password + "'");
				conn = DriverManager.getConnection(url, username, password);
				if(conn != null) {
					connections.put(id, conn);
				}
				else {
					String couldNotObtainConnMsg = "Could not obtain database connection for id '" + id + "'";
					cLogger.error(couldNotObtainConnMsg);
				}
			} catch(IOException ioe) {
				String msg = "Could not load property file for id '" + id + "'";
				cLogger.error(msg, ioe);
			} catch(SQLException sqle) {
				String msg = "Could not create connection for id '" + id + "'";
				cLogger.error(msg, sqle);
			}
		}
		
		return conn;
	}
	
	public static void closeConnection(String id) throws SQLException {
		Connection conn = connections.get(id);
		if(conn != null) {
			conn.close();
			connections.remove(id);
		}
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
	
	public static String getDatabaseIdFromRequest(ServletWebRequest servletWebRequest) {
		String requestUrl = servletWebRequest.getHttpServletRequest().getRequestURL().toString();
		String databaseId = getDatabaseIdFromRequestUrl(requestUrl);
		return databaseId;
	}
}
