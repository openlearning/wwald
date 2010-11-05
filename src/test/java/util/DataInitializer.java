package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.wwald.model.Competency;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Course;
import org.wwald.model.Mentor;
import org.wwald.model.User;
import org.wwald.service.Sql;

public class DataInitializer {

	private ClassLoader appClassLoader;
	
	public static final String LINE_SEPERATOR = System.getProperty("line.separator");
	
	private static final String BASE_PATH = "data/";
	private static final String COURSES_BASE_PATH = BASE_PATH + "course/";
	private static final String PAGES_BASE_PATH = BASE_PATH + "pages/";
	private static final String TABLES_DATA_FILE = "create_tables_sql.txt";
	private static final String MENTORS_DATA_FILE = "mentors.txt";
	
	public DataInitializer() {
		this.appClassLoader = DataInitializer.class.getClassLoader();
	}
	
	public void initData(Connection conn) throws IOException, SQLException, DataFileSyntaxException {		
		createTables(conn);
//		populateTables(conn);
	}

	private void populateTables(Connection conn) throws IOException, DataFileSyntaxException, SQLException {
		populateUsers(conn);
		populateCourses(conn);		
		populatePages(conn);
	}

	private void populateUsers(Connection conn) throws IOException, DataFileSyntaxException, SQLException {
		System.out.println("populating users");
		URL url = appClassLoader.getResource(BASE_PATH + "users.txt");
		UsersFileParser parser = new UsersFileParser(url);
		User users[] = parser.parse();
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
		if(users != null) {
			for(User user : users) {
				String sql = String.format(Sql.INSERT_USER, 
										   wrapForSQL(user.getFirstName()),
										   wrapForSQL(user.getMi()),
										   wrapForSQL(user.getLastName()),
										   wrapForSQL(user.getUsername()),
										   wrapForSQL(user.getPassword()),
										   wrapForSQL(df.format(user.getJoinDate())),
										   wrapForSQL(user.getRole().toString()));
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				System.out.println("Created user " + user);
			}
		}
		else {
			System.out.println("coulnd not insert users in the database because users[] is null");
		}
	}
	
	private void populatePages(Connection conn) {
		File[] files = getFilesInDirectory(PAGES_BASE_PATH);		
		for(File file : files) {
			String fileName = file.getName();
			InputStream is = appClassLoader.getResourceAsStream(PAGES_BASE_PATH + fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuffer buff = new StringBuffer();
			String line = null;
			try {
				while((line = reader.readLine()) != null) {
					buff.append(line + "\n");
				}
				String sql = String.format(Sql.INSERT_STATIC_PAGE, wrapForSQL(fileName), wrapForSQL(buff.toString()));
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(sql);
			} catch(Exception ioe) {
				System.out.println("Could not populate data for static page in file " + file + " " + ioe);
			} 
		}
	}	
	
	private void populateCourses(Connection conn) throws IOException, DataFileSyntaxException, SQLException {
		URL coursesDirUrl = appClassLoader.getResource(COURSES_BASE_PATH);
		File coursesDataDir = new File(coursesDirUrl.getPath());
		File courseDataFiles[] = coursesDataDir.listFiles();
		List<Course> coursesList = new ArrayList<Course>();
		for(File courseDataFile : courseDataFiles) {
			String courseFileName = courseDataFile.getName();
			URL courseDataFileUrl = appClassLoader.getResource(COURSES_BASE_PATH + courseFileName);
			CourseFileParser parser = new CourseFileParser(courseDataFileUrl);
			Course course = parser.parse();
			coursesList.add(course);
		}
		
		populateCoursesWiki(coursesList, conn);
		
		populateCourse(coursesList, conn);
		
		populateCourseCompetenciesWiki(coursesList, conn);
		
		populateCompetency(coursesList, conn);
	}

	private void populateCompetency(List<Course> coursesList, Connection conn) throws SQLException {
		System.out.println("----- populating course competencies -----");
		for(Course course : coursesList) {
			System.out.println("----- populating competencies for course " + course.getId() + " -----");
			List<Competency> competencies = course.getCompetencies();
			if(competencies != null) {
				for(Competency competency : competencies) {
					if(competency != null) {
						System.out.println("----- populating competency " + competency.getTitle() + " -----");
						String sql = String.format(Sql.INSERT_COMPETENCY, 
								   				   String.valueOf(competency.getId()), 
								   				   wrapForSQL(course.getId()),
								   				   wrapForSQL(competency.getTitle()),
								   				   wrapForSQL(competency.getDescription()),
								   				   wrapForSQL(competency.getResource()));
						Statement stmt = conn.createStatement();
						stmt.executeUpdate(sql);
					}
				}
			}
			else {
				System.out.println("WARNING: competencies were null");
			}
		}

	}

	private void populateCourseCompetenciesWiki(List<Course> coursesList, Connection conn) throws SQLException {
		System.out.println("----- populating course competency wiki -----");
		for(Course course : coursesList) {
			System.out.println("----- populating competency for course " + course.getId() + " -----");
			StringBuffer buff = new StringBuffer("");
			List<Competency> competencies = course.getCompetencies();
			if(competencies != null) {
				for(Competency competency : competencies) {
					if(competency != null) {					
						buff.append(competency.getTitle());
						buff.append(LINE_SEPERATOR);
					}
				}
			}
			else {
				System.out.println("WARNING: Competencies are null for course " + course.getId());
			}
			String sql = String.format(Sql.INSERT_COURSE_COMPETENCIES_WIKI, wrapForSQL(course.getId()), wrapForSQL(buff.toString()));
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		}
		
	}

	private void populateCourse(List<Course> coursesList, Connection conn) throws SQLException {
		System.out.println("----- populating courses -----");
		//SqlUtil.printTableContents("MENTOR", conn);
		
		for(Course course : coursesList) {
			if(course != null) {
				System.out.println("----- populating course " + course.getId() + " -----");
				String sql = String.format(Sql.INSERT_COURSE,
										   wrapForSQL(course.getId()),
										   wrapForSQL(course.getTitle()),
										   wrapForSQL(course.getDescription()));
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				
				Mentor mentor = course.getMentor();
				
				if(mentor != null) {
					try {
						sql = String.format(Sql.INSERT_COURSE_MENTOR, 
								wrapForSQL(course.getId()), 
								wrapForSQL(mentor.getUsername()));
						stmt = conn.createStatement();
						stmt.executeUpdate(sql);
					} catch(SQLException sqle) {
						System.out.println("Could not populate mentor " + mentor.getUsername() + " for course " + course.getId() + " " + sqle);
					}
				}
				
			}
		}
	}

	
	private void populateCoursesWiki(List<Course> coursesList, Connection conn) throws SQLException {
		System.out.println("----- populating courses wiki -----");
		StringBuffer buff = new StringBuffer();
		String sep = " | ";
		for(Course course : coursesList) {
			buff.append(course.getId());
			buff.append(sep);
			buff.append(course.getTitle());
			buff.append(LINE_SEPERATOR);
		}
		
		String sql = String.format(Sql.INSERT_COURSES_WIKI,
								   "1",
								   wrapForSQL(buff.toString()));
		
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
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

	public final void createTables(Connection conn)
							  throws IOException {
		URL url = appClassLoader.getResource(BASE_PATH + TABLES_DATA_FILE);		
		String sqls[] = (new CreateTablesFileParser(url)).parse();
		System.out.println("----- creating tables -----");
		for(String sql : sqls) {
			if(sql != null && !sql.trim().equals("")) {
				try {
					System.out.println("Executing table creation sql");
					System.out.println(sql);
					
					Statement stmt = conn.createStatement();
					stmt.executeUpdate(sql);					
				} catch(SQLException sqle) {
					System.out.println("Exception caught while creating table " + sqle);
				}
			}
		}
	}
	
	public static String wrapForSQL(String s) {
		if(s == null) {
			s = "";
		}
		String escapedStr = s.replaceAll("'", "''");
		return "'" + escapedStr + "'";
	}
	
	private File[] getFilesInDirectory(String path) {
		URL dirUrl = appClassLoader.getResource(path);
		File dataDir = new File(dirUrl.getPath());
		File dataFiles[] = dataDir.listFiles();
		return dataFiles;
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println("starting data initializer");
		DataInitializer dataInitializer = new DataInitializer();
		dataInitializer.initData(ConnectionPool.getConnection());
	}
	
//	private static String video1 = "<embed src=\"http://blip.tv/play/gtQk6o5MkPxE\" type=\"application/x-shockwave-flash\" width=\"500\" height=\"311\" allowscriptaccess=\"always\" allowfullscreen=\"true\"></embed><p style=\"font-size:11px;font-family:tahoma,arial\">Watch it on <a style=\"text-decoration:underline\" href=\"http://academicearth.org/lectures/malan-hardware/\">Academic Earth</a></p>";
//	private static String video2 = "<object width=\"480\" height=\"385\"><param name=\"movie\" value=\"http://www.youtube.com/v/zWg7U0OEAoE?fs=1&amp;hl=en_US\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/zWg7U0OEAoE?fs=1&amp;hl=en_US\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"480\" height=\"385\"></embed></object>";
//	
//	public static String courseWiki = "INTROCS | Introduction To Computer Science\n" +
//									  "INTROCSPROG | Introduction to Computer Science and Programming (using Python)\n" +									  
//									  "PROGPAR | Programming Paradigms\n" +
//									  "UCATI | Understanding Computers And The Internet\n";
//	
//	public static String courses[][] = {
//			{"INTROCS", "Introduction To Computer Science", "Introduction to Computer Science I is a first course in computer science at Harvard College for concentrators and non-concentrators alike."},
//			{"INTROCSPROG", "Introduction to Computer Science and Programming (using Python)", "This subject is aimed at students with little or no programming experience."},
//			{"PROGPAR", "Programming Paradigms", "Lecture by Professor Jerry Cain for Programming Paradigms (CS107) in the Stanford University Computer Science department. Professor Cain provides an overview of the course."},
//			{"UCATI", "Understanding Computers And The Internet", "This course is all about understanding: understanding what is going on inside your computer when you flip on the switch"},
//		};
//
//	public static String competencies[][] = {
//			{"1","UCATI","Lecture 1","Description of lecture 1",video1},
//			{"2","UCATI","Lecture 2","Description of lecture 2",video2},
//			{"3","UCATI","Lecture 3","Description of lecture 3",video1},
//			{"1","INTROCS","Lecture 1","Description of lecture 1",video1},
//			{"2","INTROCS","Lecture 2","Description of lecture 2",video2},
//			{"3","INTROCS","Lecture 3","Description of lecture 3",video1},
//			{"1","INTROCSPROG","Lecture 1","Description of lecture 1",video1},
//			{"2","INTROCSPROG","Lecture 2","Description of lecture 2",video2},
//			{"3","INTROCSPROG","Lecture 3","Description of lecture 3",video1},
//			{"1","PROGPAR","Lecture 1","Description of lecture 1",video1},
//			{"2","PROGPAR","Lecture 2","Description of lecture 2",video2},
//			{"3","PROGPAR","Lecture 3","Description of lecture 3",video1},
//		  };
//
//	public static String mentors[][] = {
//			{"1", "David", "J", "Malan", "Professor at Harvard"}
//		 };
//	
//	public static String courseCompetenciesWiki[][] = {
//		{"UCATI","Lecture 1\nLecture 2\nLecture 3"},
//		{"INTROCS","Lecture 1\nLecture 2\nLecture 3"},
//		{"INTROCSPROG","Lecture 1\nLecture 2\nLecture 3"},
//		{"PROGPAR","Lecture 1\nLecture 2\nLecture 3"},
//	};
//
//	public static String courseMentors[][] = {
//					{"UCATI","1"},
//					{"INTROCS","1"},
//					{"INTROCSPROG","1"},
//					{"PROGPAR","1"},
//			   };
//	
//	public static String users[][] = {
//		{"John", "M", "Woods", "jwoods", "jwoods", "2010-10-01", "STUDENT"},
//		{"Bill", "", "Forrest", "bforrest", "bforrest", "2010-10-01", "MENTOR"},
//		{"Steve", "", "Meadows", "smeadows", "smeadows", "2010-10-01", "ADMIN"},
//	};
//	
//	public static String courseEnrollmentStatus[][] = {
//		{"1", "UNENROLLED"},
//		{"2", "ENROLLED"},
//		{"3", "COMPLETED"},
//		{"4", "DROPPED"},
//	};

}
