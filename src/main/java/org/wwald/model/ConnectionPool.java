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
						getResourceAsStream("org/wwald/db.properties");
			Properties dbProps = new Properties();
			dbProps.load(propStream);
			
			String url = dbProps.getProperty("db.url");
			String username = dbProps.getProperty("db.user");
			String password = dbProps.getProperty("db.password");
			
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			conn = DriverManager.getConnection(url, username, password);
		} catch(Exception e) {
			System.out.println("COULD NOT INITIALIZE DATABASE CONNECTION " + e);
		}
	}
	
	public static synchronized Connection getConnection() {
		return conn;
	}
}
