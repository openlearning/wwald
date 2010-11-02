package org.wwald.model;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionPool {
	
	private static Connection conn;
	
	static {
		try {
			InputStream propStream = 
				ConnectionPool.class.
					getClassLoader().
						getResourceAsStream("db.properties");
			Properties dbProps = new Properties();
			dbProps.load(propStream);
			
			String url = dbProps.getProperty("db.url");
			String username = dbProps.getProperty("db.user");
			String password = dbProps.getProperty("db.password");
			
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			
			System.out.println("Connecting to the database");
			System.out.println("url - '" + url + "'");
			System.out.println("user - '" + username + "'");
			System.out.println("password - '" + password + "'");
			
			conn = DriverManager.getConnection(url, username, password);
		} catch(Exception e) {
			System.out.println("COULD NOT INITIALIZE DATABASE CONNECTION " + e);
		}
	}
	
	public static synchronized Connection getConnection() {
		return conn;
	}
}
