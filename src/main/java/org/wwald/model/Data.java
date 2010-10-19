package org.wwald.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Data {
	private static String video1 = "<embed src=\"http://blip.tv/play/gtQk6o5MkPxE\" type=\"application/x-shockwave-flash\" width=\"500\" height=\"311\" allowscriptaccess=\"always\" allowfullscreen=\"true\"></embed><p style=\"font-size:11px;font-family:tahoma,arial\">Watch it on <a style=\"text-decoration:underline\" href=\"http://academicearth.org/lectures/malan-hardware/\">Academic Earth</a></p>";
	private static String video2 = "<object width=\"480\" height=\"385\"><param name=\"movie\" value=\"http://www.youtube.com/v/zWg7U0OEAoE?fs=1&amp;hl=en_US\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/zWg7U0OEAoE?fs=1&amp;hl=en_US\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"480\" height=\"385\"></embed></object>";
	
	public static String courseWiki = "INTROCS | Introduction To Computer Science\n" +
									  "INTROCSPROG | Introduction to Computer Science and Programming (using Python)\n" +									  
									  "PROGPAR | Programming Paradigms\n" +
									  "UCATI | Understanding Computers And The Internet\n";
	
	public static String courses[][] = {
			{"INTROCS", "Introduction To Computer Science", "Introduction to Computer Science I is a first course in computer science at Harvard College for concentrators and non-concentrators alike."},
			{"INTROCSPROG", "Introduction to Computer Science and Programming (using Python)", "This subject is aimed at students with little or no programming experience."},
			{"PROGPAR", "Programming Paradigms", "Lecture by Professor Jerry Cain for Programming Paradigms (CS107) in the Stanford University Computer Science department. Professor Cain provides an overview of the course."},
			{"UCATI", "Understanding Computers And The Internet", "This course is all about understanding: understanding what is going on inside your computer when you flip on the switch"},
		};

	public static String competencies[][] = {
			{"1","Lecture 1","Description of lecture 1",video1},
			{"2","Lecture 2","Description of lecture 2",video2},
			{"3","Lecture 3","Description of lecture 3",video1},
		  };

	public static String mentors[][] = {
			{"1", "David", "J", "Malan", "Professor at Harvard"}
		 };

	public static String courseCompetencies[][] = {
						{"UCATI","1"},	
						{"UCATI","2"},
						{"UCATI","3"},
						{"INTROCS","1"},	
						{"INTROCS","2"},
						{"INTROCS","3"},
						{"INTROCSPROG","1"},	
						{"INTROCSPROG","2"},
						{"INTROCSPROG","3"},
						{"PROGPAR","1"},	
						{"PROGPAR","2"},
						{"PROGPAR","3"},
					};
	
	public static String courseCompetenciesWiki[][] = {
		{"UCATI","Lecture 1\nLecture 2\nLecture 3"},
		{"INTROCS","Lecture 1\nLecture 2\nLecture 3"},
		{"INTROCSPROG","Lecture 1\nLecture 2\nLecture 3"},
		{"PROGPAR","Lecture 1\nLecture 2\nLecture 3"},
	};

	public static String courseMentors[][] = {
					{"UCATI","1"},
					{"INTROCS","1"},
					{"INTROCSPROG","1"},
					{"PROGPAR","1"},
			   };
	
	public static String users[][] = {
		{"John", "M", "Woods", "jwoods", "jwoods", "2010-10-01", "STUDENT"},
		{"Bill", "", "Forrest", "bforrest", "bforrest", "2010-10-01", "MENTOR"},
		{"Steve", "", "Meadows", "smeadows", "smeadows", "2010-10-01", "ADMIN"},
	};
	
	public static String courseEnrollmentStatus[][] = {
		{"1", "UNENROLLED"},
		{"2", "ENROLLED"},
		{"3", "COMPLETED"},
	};
	
	public static void init(Connection conn) {
		try {
			createTables(conn);
			populateTables(conn);
		} catch (SQLException e) {
			//TODO: This method needs to go away and should be put instead in test code
			e.printStackTrace();
		}
	}
	
	private static void createTables(Connection conn) throws SQLException {
		Statement s = conn.createStatement();
		String sql = null;
		
		s.executeUpdate(Sql.CREATE_COURSES_WIKI);
		
		s.executeUpdate(Sql.CREATE_COURSE);
		
		s.executeUpdate(Sql.CREATE_COURSE_COMPETENCIES_WIKI);
		
		s.executeUpdate(Sql.CREATE_USER);
		
		s.executeUpdate(Sql.CREATE_COURSE_ENROLLMENT_ACTIONS);
		
		s.executeUpdate(Sql.CREATE_COURSE_ENROLLMENT_STATUS_MASTER);
		
		sql = getSqlToCreateCompetencyTable();
		s.executeUpdate(sql);
		
		sql = getSqlToCreateMentorTable();
		s.executeUpdate(sql);		
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
			" 	id INTEGER NOT NULL PRIMARY KEY," +
			"	title VARCHAR(128) NOT NULL," +
			"	description LONGVARCHAR," +
			"	resources LONGVARCHAR," +
			"		CONSTRAINT competency_unique UNIQUE (title));";
		
		String createCourseToCompetencyTable = 
			" CREATE TABLE COURSE_COMPETENCY (" +
			" 	course_id VARCHAR(16) NOT NULL," +
			" 	competency_id INTEGER NOT NULL," +
			" 		PRIMARY KEY(course_id, competency_id)," +
			" 		CONSTRAINT course_competency_col1_fk FOREIGN KEY (course_id) REFERENCES COURSE(id)," +
			" 		CONSTRAINT course_competency_col2_fk FOREIGN KEY (competency_id) REFERENCES COMPETENCY(id));";
		
		return createCompetencyTable + "\n" + createCourseToCompetencyTable;
	}
	
	private static void populateTables(Connection conn) throws SQLException {
		Statement stmt = null;
		//create courses wiki content
		String sqlToAddCoursesWiki = "INSERT INTO COURSES_WIKI (id, content) VALUES (1, %s);";
		stmt = conn.createStatement();
		stmt.execute(String.format(sqlToAddCoursesWiki, wrapForSQL(Data.courseWiki)));
		
		//create courses
		String sqlToAddCourses = 
			"INSERT INTO COURSE(id, title, description) VALUES (%s,%s,%s)";
		stmt = conn.createStatement();
		for(int i = 0; i < Data.courses.length; i++) {
			stmt.execute(String.format(sqlToAddCourses, 
									   wrapForSQL(Data.courses[i][0]), wrapForSQL(Data.courses[i][1]), wrapForSQL(Data.courses[i][2])));
		}
		
		//create competencies
		String sqlToAddCompetencies = 
			"INSERT INTO COMPETENCY(id, title, description, resources) VALUES (%s,%s,%s,%s)";
		stmt = conn.createStatement();
		for(int i = 0; i < Data.competencies.length; i++) {
			stmt.execute(String.format(sqlToAddCompetencies, 
										Data.competencies[i][0], wrapForSQL(Data.competencies[i][1]), wrapForSQL(Data.competencies[i][2]), wrapForSQL(Data.competencies[i][3])));
		}
		
		//create mentors
		String sqlToAddMentors = 
			"INSERT INTO MENTOR(id, first_name, middle_initial, last_name, short_bio) VALUES (%s,%s,%s,%s,%s)";
		stmt = conn.createStatement();
		for(int i = 0; i < Data.mentors.length; i++) {
			stmt.execute(String.format(sqlToAddMentors, 
										Data.mentors[i][0], wrapForSQL(Data.mentors[i][1]), wrapForSQL(Data.mentors[i][2]), wrapForSQL(Data.mentors[i][3]), wrapForSQL(Data.mentors[i][4])));
		}
		
		//create course_competencies table
		String sqlToAddCourseCompetency = 
			"INSERT INTO COURSE_COMPETENCY(course_id, competency_id) VALUES (%s,%s);";
		stmt = conn.createStatement();
		for(int i = 0; i < Data.courseCompetencies.length; i++) {
			stmt.execute(String.format(sqlToAddCourseCompetency, 
										wrapForSQL(Data.courseCompetencies[i][0]), Data.courseCompetencies[i][1]));
		}
		
		//add data to course competency wiki
		String sqlToAddToCourseCompetencyWiki = 
			"INSERT INTO COURSE_COMPETENCIES_WIKI(course_id, contents) VALUES (%s, %s);";
		stmt = conn.createStatement();
		for(int i = 0; i < Data.courseCompetenciesWiki.length; i++) {
			stmt.execute(String.format(sqlToAddToCourseCompetencyWiki, wrapForSQL(Data.courseCompetenciesWiki[i][0]), wrapForSQL(Data.courseCompetenciesWiki[i][1])));
		}
		
		//create course_mentors table
		String sqlToAddCourseMentors = 
			"INSERT INTO COURSE_MENTORS(course_id, mentor_id) VALUES (%s,%s);";
		stmt = conn.createStatement();
		for(int i = 0; i < Data.courseMentors.length; i++) {
			stmt.execute(String.format(sqlToAddCourseMentors, 
										wrapForSQL(Data.courseMentors[i][0]), Data.courseMentors[i][1]));
		}
		
		//create users
		String sqlToAddUser = 
			"INSERT INTO USER VALUES(%s, %s, %s, %s, %s, %s, %s);";
		stmt = conn.createStatement();
		for(int i = 0; i < Data.users.length; i++) {
			String addUserSql = String.format(sqlToAddUser, wrapForSQL(Data.users[0][0]),
					  wrapForSQL(Data.users[i][1]),
					  wrapForSQL(Data.users[i][2]),
					  wrapForSQL(Data.users[i][3]),
					  wrapForSQL(Data.users[i][4]),
					  wrapForSQL(Data.users[i][5]),
					  wrapForSQL(Data.users[i][6]));
			stmt.executeUpdate(addUserSql);
			
		}
		
		String sqlToAddCourseEnrollmentActionMaster = "INSERT INTO COURSE_ENOLLMENT_STATUS_MASTER VALUES(%s, %s);";
		stmt = conn.createStatement();
		for(int i=0; i<Data.courseEnrollmentStatus.length; i++) {
			String addCourseEnrollmentActionSql = 
				String.format(sqlToAddCourseEnrollmentActionMaster, 
							  Data.courseEnrollmentStatus[i][0],
							  wrapForSQL(Data.courseEnrollmentStatus[i][1]));
			stmt.executeUpdate(addCourseEnrollmentActionSql);
		}
	}

	public static String wrapForSQL(String s) {
		return "'" + s + "'";
	}
	
}
