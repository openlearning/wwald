package org.wwald.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Data {
	private static String video1 = "<embed src=\"http://blip.tv/play/gtQk6o5MkPxE\" type=\"application/x-shockwave-flash\" width=\"500\" height=\"311\" allowscriptaccess=\"always\" allowfullscreen=\"true\"></embed><p style=\"font-size:11px;font-family:tahoma,arial\">Watch it on <a style=\"text-decoration:underline\" href=\"http://academicearth.org/lectures/malan-hardware/\">Academic Earth</a></p>";
	private static String video2 = "<object width=\"480\" height=\"385\"><param name=\"movie\" value=\"http://www.youtube.com/v/zWg7U0OEAoE?fs=1&amp;hl=en_US\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/zWg7U0OEAoE?fs=1&amp;hl=en_US\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"480\" height=\"385\"></embed></object>";
	
	public static String courses[][] = {
			{"INTROCS", "Introduction To Computer Science", "Introduction to Computer Science I is a first course in computer science at Harvard College for concentrators and non-concentrators alike."},
			{"INTROCSPROG", "Introduction to Computer Science and Programming (using Python)", "This subject is aimed at students with little or no programming experience."},
			{"PROGPAR", "Programming Paradigms", "Lecture by Professor Jerry Cain for Programming Paradigms (CS107) in the Stanford University Computer Science department. Professor Cain provides an overview of the course."},
			{"UCATI", "Understanding Computers And The Internet", "This course is all about understanding: understanding what is going on inside your computer when you flip on the switch"},
		};

	public static String competencies[][] = {
			{"L1","Lecture 1","Description of lecture 1",video1},
			{"L2","Lecture 2","Description of lecture 2",video2},
			{"L3","Lecture 3","Description of lecture 3",video1},
		  };

	public static String mentors[][] = {
			{"1", "David", "J", "Malan", "Professor at Harvard"}
		 };

	public static String courseCompetencies[][] = {
						{"UCATI","L1"},	
						{"UCATI","L2"},
						{"UCATI","L3"},
						{"INTROCS","L1"},	
						{"INTROCS","L2"},
						{"INTROCS","L3"},
						{"INTROCSPROG","L1"},	
						{"INTROCSPROG","L2"},
						{"INTROCSPROG","L3"},
						{"PROGPAR","L1"},	
						{"PROGPAR","L2"},
						{"PROGPAR","L3"},
					};

	public static String courseMentors[][] = {
					{"UCATI","1"},
					{"INTROCS","1"},
					{"INTROCSPROG","1"},
					{"PROGPAR","1"},
			   };
	
	public static void init(Connection conn) {
		try {
			createTables(conn);
			populateTables(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void createTables(Connection conn) throws SQLException {
		Statement s = conn.createStatement();
		String sql = null;
		
		sql = getSqlToCreateCourseTable();
		s.executeUpdate(sql);
		
		sql = getSqlToCreateCompetencyTable();
		s.executeUpdate(sql);
		
		sql = getSqlToCreateMentorTable();
		s.executeUpdate(sql);		
	}
	
	private static String getSqlToCreateCourseTable() {
		return " CREATE TABLE COURSE (" +
			   " 	id VARCHAR(16) NOT NULL PRIMARY KEY," +
			   " 	title VARCHAR(128) NOT NULL," +
			   " 	description LONGVARCHAR);";
	}
	
	private static String getSqlToCreateMentorTable() {
		String createMentorTable = 
			" CREATE TABLE MENTOR (" +
			" 	id INTEGER NOT NULL PRIMARY KEY," +
			"	first_name VARCHAR(32)," +
			"	middle_initial VARCHAR(1)," +
			"	last_name VARCHAR(32)," +
			"	short_bio LONGVARCHAR);";
		
		String createCourseMentorTable = 
			" CREATE TABLE COURSE_MENTORS (" +
			" 	course_id VARCHAR(16) NOT NULL," +
			"	mentor_id INTEGER NOT NULL," +
			"		PRIMARY KEY(course_id, mentor_id)," +
			"		CONSTRAINT course_mentors_col1_fk FOREIGN KEY (course_id) REFERENCES COURSE(id)," +
			"		CONSTRAINT course_mentors_col2_fk FOREIGN KEY (mentor_id) REFERENCES MENTOR(id));";
		
		return createMentorTable + "\n" + createCourseMentorTable;
	}
	
	private static String getSqlToCreateCompetencyTable() {
		String createCompetencyTable = 
			" CREATE TABLE COMPETENCY (" +
			" 	id VARCHAR(16) NOT NULL PRIMARY KEY," +
			"	title VARCHAR(128) NOT NULL," +
			"	description LONGVARCHAR," +
			"	resources LONGVARCHAR);";
		
		String createCourseToCompetencyTable = 
			" CREATE TABLE COURSE_COMPETENCY (" +
			" 	course_id VARCHAR(16) NOT NULL," +
			" 	competency_id VARCHAR(16) NOT NULL," +
			" 		PRIMARY KEY(course_id, competency_id)," +
			" 		CONSTRAINT course_competency_col1_fk FOREIGN KEY (course_id) REFERENCES COURSE(id)," +
			" 		CONSTRAINT course_competency_col2_fk FOREIGN KEY (competency_id) REFERENCES COMPETENCY(id));";
		
		return createCompetencyTable + "\n" + createCourseToCompetencyTable;
	}
	
	private static void populateTables(Connection conn) throws SQLException {
		//create courses
		String sqlToAddCourses = 
			"INSERT INTO COURSE(id, title, description) VALUES (%s,%s,%s)";
		Statement stmt = conn.createStatement();
		for(int i = 0; i < Data.courses.length; i++) {
			stmt.execute(String.format(sqlToAddCourses, 
									   wrapForSQL(Data.courses[i][0]), wrapForSQL(Data.courses[i][1]), wrapForSQL(Data.courses[i][2])));
		}
		
		//create competencies
		String sqlToAddCompetencies = 
			"INSERT INTO COMPETENCY(id, title, description, resources) VALUES (%s,%s,%s,%s)";
		Statement stmt1 = conn.createStatement();
		for(int i = 0; i < Data.competencies.length; i++) {
			stmt1.execute(String.format(sqlToAddCompetencies, 
										wrapForSQL(Data.competencies[i][0]), wrapForSQL(Data.competencies[i][1]), wrapForSQL(Data.competencies[i][2]), wrapForSQL(Data.competencies[i][3])));
		}
		
		//create mentors
		String sqlToAddMentors = 
			"INSERT INTO MENTOR(id, first_name, middle_initial, last_name, short_bio) VALUES (%s,%s,%s,%s,%s)";
		Statement stmt2 = conn.createStatement();
		for(int i = 0; i < Data.mentors.length; i++) {
			stmt2.execute(String.format(sqlToAddMentors, 
										Data.mentors[i][0], wrapForSQL(Data.mentors[i][1]), wrapForSQL(Data.mentors[i][2]), wrapForSQL(Data.mentors[i][3]), wrapForSQL(Data.mentors[i][4])));
		}
		
		//create course_competencies table
		String sqlToAddCourseCompetency = 
			"INSERT INTO COURSE_COMPETENCY(course_id, competency_id) VALUES (%s,%s);";
		Statement stmt3 = conn.createStatement();
		for(int i = 0; i < Data.courseCompetencies.length; i++) {
			stmt3.execute(String.format(sqlToAddCourseCompetency, 
										wrapForSQL(Data.courseCompetencies[i][0]), wrapForSQL(Data.courseCompetencies[i][1])));
		}
		
		//create course_mentors table
		String sqlToAddCourseMentors = 
			"INSERT INTO COURSE_MENTORS(course_id, mentor_id) VALUES (%s,%s);";
		Statement stmt4 = conn.createStatement();
		for(int i = 0; i < Data.courseMentors.length; i++) {
			stmt3.execute(String.format(sqlToAddCourseMentors, 
										wrapForSQL(Data.courseMentors[i][0]), Data.courseMentors[i][1]));
		}
	}

	public static String wrapForSQL(String s) {
		return "'" + s + "'";
	}
	

}
