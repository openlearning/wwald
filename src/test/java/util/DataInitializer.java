package util;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.wwald.model.ConnectionPool;
import org.wwald.model.Mentor;
import org.wwald.model.Sql;

public class DataInitializer {
	
	public void initData(Connection conn) throws IOException, SQLException {
		ClassLoader appClassLoader = DataInitializer.class.getClassLoader(); 
		URL tableCreationSqlFileUrl = appClassLoader.getResource("data/create_tables_sql.txt");
		URL mentorsCreationSqlFileUrl = appClassLoader.getResource("data/mentors.txt");

		createTables(conn, tableCreationSqlFileUrl);
		populateTables(conn, mentorsCreationSqlFileUrl);
	}

	private void populateTables(Connection conn, URL mentorsCreationSqlFileUrl) {
		populateMentors(conn, mentorsCreationSqlFileUrl);
	}

	private void populateMentors(Connection conn, URL url) {
		MentorsFileParser parser = new MentorsFileParser(url);
		try {
			Mentor mentors[] = parser.parse();
			if(mentors != null) {
				for(Mentor mentor : mentors) {
					Statement stmt = conn.createStatement();
					String sql = String.format(Sql.INSERT_MENTOR, 
											   wrapForSQL(mentor.getFirstName()),
											   wrapForSQL(mentor.getMiddleInitial()),
											   wrapForSQL(mentor.getLastName()),
											   wrapForSQL(mentor.getShortBio()));
					stmt.executeUpdate(sql);
				}
			}
			else {
				String msg = "Trying to populate mentors but the array of mentors is null";
				System.out.println(msg);
			}
		} catch(IOException ioe) {
			System.out.println("Could not populate mentors " + ioe);
		} catch(SQLException sqle) {
			System.out.println("Could not populate mentors " + sqle);
		}
	}

//	public void populateTables(Connection conn) throws SQLException {
//		Statement stmt = null;
//		//create courses wiki content
//		String sqlToAddCoursesWiki = "INSERT INTO COURSES_WIKI (id, content) VALUES (1, %s);";
//		stmt = conn.createStatement();
//		stmt.execute(String.format(sqlToAddCoursesWiki, wrapForSQL(courseWiki)));
//		
//		//create courses
//		String sqlToAddCourses = 
//			"INSERT INTO COURSE(id, title, description) VALUES (%s,%s,%s)";
//		stmt = conn.createStatement();
//		for(int i = 0; i < courses.length; i++) {
//			stmt.execute(String.format(sqlToAddCourses, 
//									   wrapForSQL(courses[i][0]), wrapForSQL(courses[i][1]), wrapForSQL(courses[i][2])));
//		}
//		
//		//create competencies
//		String sqlToAddCompetencies = 
//			"INSERT INTO COMPETENCY(id, course_id, title, description, resources) VALUES (%s,%s,%s,%s,%s)";
//		stmt = conn.createStatement();
//		for(int i = 0; i < competencies.length; i++) {
//			stmt.execute(String.format(sqlToAddCompetencies, 
//										competencies[i][0], 
//										wrapForSQL(competencies[i][1]),
//										wrapForSQL(competencies[i][2]),
//										wrapForSQL(competencies[i][3]), 
//										wrapForSQL(competencies[i][4])));
//		}
//		
//		//create mentors
//		String sqlToAddMentors = 
//			"INSERT INTO MENTOR(id, first_name, middle_initial, last_name, short_bio) VALUES (%s,%s,%s,%s,%s)";
//		stmt = conn.createStatement();
//		for(int i = 0; i < mentors.length; i++) {
//			stmt.execute(String.format(sqlToAddMentors, 
//										mentors[i][0], wrapForSQL(mentors[i][1]), wrapForSQL(mentors[i][2]), wrapForSQL(mentors[i][3]), wrapForSQL(mentors[i][4])));
//		}
//		
//		//add data to course competency wiki
//		String sqlToAddToCourseCompetencyWiki = 
//			"INSERT INTO COURSE_COMPETENCIES_WIKI(course_id, contents) VALUES (%s, %s);";
//		stmt = conn.createStatement();
//		for(int i = 0; i < courseCompetenciesWiki.length; i++) {
//			stmt.execute(String.format(sqlToAddToCourseCompetencyWiki, wrapForSQL(courseCompetenciesWiki[i][0]), wrapForSQL(courseCompetenciesWiki[i][1])));
//		}
//		
//		//create course_mentors table
//		String sqlToAddCourseMentors = 
//			"INSERT INTO COURSE_MENTORS(course_id, mentor_id) VALUES (%s,%s);";
//		stmt = conn.createStatement();
//		for(int i = 0; i < courseMentors.length; i++) {
//			stmt.execute(String.format(sqlToAddCourseMentors, 
//										wrapForSQL(courseMentors[i][0]), courseMentors[i][1]));
//		}
//		
//		//create users
//		String sqlToAddUser = 
//			"INSERT INTO USER VALUES(%s, %s, %s, %s, %s, %s, %s);";
//		stmt = conn.createStatement();
//		for(int i = 0; i < users.length; i++) {
//			String addUserSql = String.format(sqlToAddUser, wrapForSQL(users[0][0]),
//					  wrapForSQL(users[i][1]),
//					  wrapForSQL(users[i][2]),
//					  wrapForSQL(users[i][3]),
//					  wrapForSQL(users[i][4]),
//					  wrapForSQL(users[i][5]),
//					  wrapForSQL(users[i][6]));
//			stmt.executeUpdate(addUserSql);
//			
//		}
//		
//		String sqlToAddCourseEnrollmentActionMaster = "INSERT INTO COURSE_ENOLLMENT_STATUS_MASTER VALUES(%s, %s);";
//		stmt = conn.createStatement();
//		for(int i=0; i<courseEnrollmentStatus.length; i++) {
//			String addCourseEnrollmentActionSql = 
//				String.format(sqlToAddCourseEnrollmentActionMaster, 
//							  courseEnrollmentStatus[i][0],
//							  wrapForSQL(courseEnrollmentStatus[i][1]));
//			stmt.executeUpdate(addCourseEnrollmentActionSql);
//		}
//	}

	public final void createTables(Connection conn,
							  URL tableCreationSqlFileUrl)
							  throws IOException {
		String sqls[] = (new CreateTablesFileParser(tableCreationSqlFileUrl)).parse();
		for(String sql : sqls) {
			if(sql != null && !sql.trim().equals("")) {
				try {
					Statement stmt = conn.createStatement();
					stmt.executeUpdate(sql);
				} catch(SQLException sqle) {
					System.out.println("Could not execute statement to create table " + sql);
					System.out.println("Exception: " + sqle);
				}
			}
		}
	}
	
	public static String wrapForSQL(String s) {
		return "'" + s + "'";
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println("starting data initializer");
		DataInitializer dataInitializer = new DataInitializer();
		dataInitializer.initData(ConnectionPool.getConnection());
	}
	
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
			{"1","UCATI","Lecture 1","Description of lecture 1",video1},
			{"2","UCATI","Lecture 2","Description of lecture 2",video2},
			{"3","UCATI","Lecture 3","Description of lecture 3",video1},
			{"1","INTROCS","Lecture 1","Description of lecture 1",video1},
			{"2","INTROCS","Lecture 2","Description of lecture 2",video2},
			{"3","INTROCS","Lecture 3","Description of lecture 3",video1},
			{"1","INTROCSPROG","Lecture 1","Description of lecture 1",video1},
			{"2","INTROCSPROG","Lecture 2","Description of lecture 2",video2},
			{"3","INTROCSPROG","Lecture 3","Description of lecture 3",video1},
			{"1","PROGPAR","Lecture 1","Description of lecture 1",video1},
			{"2","PROGPAR","Lecture 2","Description of lecture 2",video2},
			{"3","PROGPAR","Lecture 3","Description of lecture 3",video1},
		  };

	public static String mentors[][] = {
			{"1", "David", "J", "Malan", "Professor at Harvard"}
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
		{"4", "DROPPED"},
	};

}
