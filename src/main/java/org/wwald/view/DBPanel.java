package org.wwald.view;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;
import org.wwald.model.ConnectionPool;

public class DBPanel extends BasePanel {
	
	private String sqlResult;
	private String sql;
	
	public DBPanel(String id) {
		super(id);
		add(getSqlForm());
		add(getResultsArea());
	}

	private Component getResultsArea() {
		return new Label("sql_result", new PropertyModel(this, "sqlResult")).setEscapeModelStrings(false); 
	}

	private Component getSqlForm() {
		Form sqlForm = new Form("sql_form") {			
			@Override
			public void onSubmit() {
				try {
					String databaseId = getDatabaseId();
					Connection conn = ConnectionPool.getConnection(databaseId);
					Statement stmt = conn.createStatement();
					boolean rsOrNot = stmt.execute(sql);
					if(rsOrNot) {
						ResultSet rs = stmt.getResultSet();
						ResultSetMetaData metaData = rs.getMetaData();
						int colCnt = metaData.getColumnCount();
						StringBuffer buffer = new StringBuffer();
						buffer.append("<table border='1'>");
						buffer.append("<tr>");
						for(int j=1; j<=colCnt; j++) {
							buffer.append("<td>");
							buffer.append(metaData.getColumnLabel(j));
							buffer.append("</td>");
						}
						buffer.append("</tr>");
						while(rs.next()) {
							buffer.append("<tr>");
							for(int i=1; i<=colCnt; i++) {
								buffer.append("<td>");
								buffer.append(rs.getObject(i).toString());
								buffer.append("</td>");
							}
							buffer.append("</tr>");
						}
						buffer.append("</table>");
						sqlResult = buffer.toString();
					} else {
						int updateCount = stmt.getUpdateCount();
						sqlResult = updateCount + " rows updated";
					}
				} catch(SQLException sqle) {
					StringBuffer buffer = 
						new StringBuffer("Could not execute Sql\n");
					buffer.append(sqle.getMessage() + "\n");
					StackTraceElement stackTraceElements[] = sqle.getStackTrace();
					for(StackTraceElement element : stackTraceElements) {
						buffer.append(element.toString() + "\n");
					}
					sqlResult = buffer.toString(); 
				}
				
			}
		};
		sqlForm.add(new TextArea("sql_form_textarea", 
								 new PropertyModel(this, "sql")));
		return sqlForm;
	}

}
