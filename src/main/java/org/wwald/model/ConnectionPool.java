package org.wwald.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionPool {
	
	private static final String url = "jdbc:hsqldb:mem:mymemdb";
	private static final String user = "SA";
	private static final String password = "";
	private static Connection conn;
	
	static {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			conn = DriverManager.getConnection(url, user, password);
		} catch(Exception e) {
			System.out.println("COULD NOT INITIALIZE DATABASE CONNECTION");
		}
	}
	
	public static synchronized Connection getConnection() {
		return conn;
	}
}
