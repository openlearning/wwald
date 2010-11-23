package org.wwald.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.wwald.WWALDApplication;


public class ConnectionPool {
	
	private static Map<String, Connection> connections;
	private static Map<String, String> dirmappings;
	private static String username;
	private static String password;
	private static String preurl;
	private static Properties dbProps;
	private static Logger cLogger = Logger.getLogger(ConnectionPool.class);
	
	static {
		try {
			connections = new HashMap<String, Connection>();
			
			dirmappings = new HashMap<String, String>();
			InputStream dirmapIs = new FileInputStream(WWALDApplication.WWALDDIR + "dirmap.properties");
			Properties dirmapProps = new Properties();
			dirmapProps.load(dirmapIs);
			Set dirmapKeys = dirmapProps.keySet();
			cLogger.info("Building directory mappings");
			for(Object dirmapKey : dirmapKeys) {
				String val = dirmapProps.getProperty((String)dirmapKey);
				dirmappings.put((String)dirmapKey, val);
				cLogger.info((String)dirmapKey + "," + val);
			}
			cLogger.info("Completed building directory mappings");
			
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
		} catch(Exception e) {
			cLogger.error("Could not initialize database connection ", e);
		}
	}
	
	public static synchronized Connection getConnection(String id) {
		Connection conn = connections.get(id);
		if(conn == null) {
			try {
				Properties dbProps = getDbPropertyFileById(id);
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

	private static Properties getDbPropertyFileById(String id) throws IOException {
		String filePath = WWALDApplication.WWALDDIR + dirmappings.get(id) + "/db.properties";
		InputStream is = new FileInputStream(filePath);
		Properties props = new Properties();
		props.load(is);
		return props;
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
