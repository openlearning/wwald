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
		if(conn == null) {
			throw new IllegalArgumentException("conn cannot be null");
		}
		createTables(conn);
		populateTables(conn);
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
										   wrapForSQL(user.getUsername()),
										   wrapForSQL(user.getEncryptedPassword()),
										   wrapForSQL(user.getEmail()),
										   wrapForSQL(user.getRole().toString()));
				Statement stmt = conn.createStatement();
				System.out.println("Executing SQL to create user");
				System.out.println(sql);
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
		String databaseId = System.getProperty("databaseId");
		if(databaseId != null) {
			System.out.println("Initializing database " + databaseId);
			DataInitializer dataInitializer = new DataInitializer();
			dataInitializer.initData(ConnectionPool.getConnection(databaseId));
		}
		else {
			System.out.println("Cannot initialize the database because databaseId has not been specified as a VM property");
		}
	}	
}
