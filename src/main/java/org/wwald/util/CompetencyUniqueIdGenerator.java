package org.wwald.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.wwald.service.Sql;

public class CompetencyUniqueIdGenerator {
	
	private static int id = 0;
	private static boolean initialized;
	private static Logger cLogger = Logger.getLogger(CompetencyUniqueIdGenerator.class);
	
	private static void init(Connection c) {
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(Sql.RETREIVE_COMPETENCIES);
			while(rs.next()) {id++;}
		} catch(SQLException sqle) {
			String msg = "Could not build the next competency id from the database. " +
						 "It will not be possible to add new lectures/competencies to " +
						 "the application";
			cLogger.error(msg);
		} finally {
			initialized = true;
		}
	}
	
	public static final synchronized int getNextCompetencyId(Connection conn) {
		if(!initialized) {
			if(conn == null) {
				initialized = true;
			}
			else {
				init(conn);
			}
		}
		
		return id++;
	}
	
	
}
