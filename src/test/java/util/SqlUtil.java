package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlUtil {
	public static void printTableContents(String tableName, Connection conn) {
		System.out.println("Printing contents of table '" + tableName + "'");
		try {
			String sql = "SELECT * FROM " + tableName + ";";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				StringBuffer buff = new StringBuffer();
				ResultSetMetaData meta = rs.getMetaData();
				int colCnt = meta.getColumnCount();
				for(int i = 1; i <= colCnt; i++) {
					buff.append(rs.getObject(i));
					buff.append(" ---- ");
				}
				System.out.println(buff);
			}
			
		} catch(SQLException sqle) {
			System.out.println("Could not print table contents due to an Exception " + sqle);
		}
	}

}
