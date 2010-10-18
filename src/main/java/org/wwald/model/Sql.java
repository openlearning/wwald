package org.wwald.model;

public class Sql {
	public static String CREATE_COURSE_COMPETENCIES_WIKI = 
		" CREATE TABLE COURSE_COMPETENCIES_WIKI ( " +
	   	"	course_id VARCHAR(16) NOT NULL PRIMARY KEY," +
	   	"	contents LONGVARCHAR," +
	   	"	CONSTRAINT course_competencies_wiki_fk1 FOREIGN KEY (course_id) REFERENCES COURSE(id));";
	
	public static String CREATE_COURSES_WIKI = 
		" CREATE TABLE COURSES_WIKI (" +
		" 	id INTEGER NOT NULL PRIMARY KEY," +
		"	content LONGVARCHAR);";
	
	public static String CREATE_COURSE = 
		" CREATE TABLE COURSE (" +
		" 	id VARCHAR(16) NOT NULL PRIMARY KEY," +
		" 	title VARCHAR(128) NOT NULL," +
		" 	description LONGVARCHAR);";
	
	public static String CREATE_USER = 
		" CREATE TABLE USER (" +
		" 	first_name VARCHAR(32)," +
		"	mi VARCHAR(1)," +
		"	last_name VARCHAR(32)," +
		"	username VARCHAR(16) NOT NULL," +
		"	password VARCHAR(16) NOT NULL," +
		"	join_date DATE NOT NULL," +
		"	role VARCHAR(32)" + 
		" );";
	
	public static String RETREIVE_USER =
		"SELECT * FROM USER WHERE username=%s AND password=%s;";
}
