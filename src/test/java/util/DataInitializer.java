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
import java.util.ArrayList;
import java.util.List;

import org.wwald.model.Competency;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Course;
import org.wwald.model.Forum;
import org.wwald.model.Mentor;
import org.wwald.model.Question;
import org.wwald.model.User;
import org.wwald.model.UserMeta;
import org.wwald.service.ApplicationFacade;
import org.wwald.service.DataException;
import org.wwald.service.DataFacadeRDBMSImpl;
import org.wwald.service.IDataFacade;
import org.wwald.service.Sql;

import util.KvTableFileParser.KvTableItem;
import util.UsersFileParser.UserUserMeta;

public class DataInitializer {

	private ClassLoader appClassLoader;
	
	public static final String LINE_SEPERATOR = System.getProperty("line.separator");
	
	private static final String BASE_PATH = "data/";
	private static final String COURSES_BASE_PATH = BASE_PATH + "course/";
	private static final String PAGES_BASE_PATH = BASE_PATH + "pages/";
	private static final String TABLES_DATA_FILE = "create_tables_sql.txt";
	private static final String MENTORS_DATA_FILE = "mentors.txt";
	
	private ApplicationFacade appFacade;
	private IDataFacade dataFacade;
	
	public DataInitializer() {
		this.appClassLoader = DataInitializer.class.getClassLoader();
		this.dataFacade = new DataFacadeRDBMSImpl();
		this.appFacade = new ApplicationFacade(dataFacade);
	}
	
	public void initData(Connection conn) throws IOException, SQLException, DataFileSyntaxException, DataException {
		if(conn == null) {
			throw new IllegalArgumentException("conn cannot be null");
		}
		
		createTables(conn);
		populateTables(conn);
	}

	private void populateTables(Connection conn) throws IOException, DataFileSyntaxException, SQLException, DataException {
		populateUsers(conn);
		populateCourses(conn);		
		populatePages(conn);
		populateKvTableClob(conn);
	}

	private void populateUsers(Connection conn) throws IOException, DataFileSyntaxException, SQLException {
		System.out.println("populating users");
		URL url = appClassLoader.getResource(BASE_PATH + "users.txt");
		UsersFileParser parser = new UsersFileParser(url);
		UsersFileParser.UserUserMeta users[] = parser.parse();
//		DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
		if(users != null) {
			for(UserUserMeta userUserMeta : users) {
				User user = userUserMeta.user;
				UserMeta userMeta = userUserMeta.userMeta;
				
				String sql = String.format(Sql.INSERT_USER, 
										   wrapForSQL(user.getUsername()),
										   wrapForSQL(user.getEncryptedPassword()),
										   wrapForSQL(user.getEmail()),
										   wrapForSQL(userMeta.getIdentifier()),
										   wrapForSQL(userMeta.getLoginVia().toString()),
										   wrapForSQL(userMeta.getRole().toString()));
				Statement stmt = conn.createStatement();
				System.out.println("Executing SQL to create user");
				System.out.println(sql);
				try {
					stmt.executeUpdate(sql);
				} catch(SQLException sqle) {
					System.out.println("Caught Exception while inserting user and user_meta into the database " + sqle);
					throw sqle;
				}
				System.out.println("Created user " + user);
			}
		}
		else {
			System.out.println("coulnd not insert users in the database because users[] is null");
		}
	}
	
	private void populatePages(Connection conn) {
		System.out.println("Populating pages");
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
	
	private void populateKvTableClob(Connection conn) throws SQLException, IOException {
		System.out.println("populating kvtableclob");
		URL url = appClassLoader.getResource(BASE_PATH + "kvtableclob.txt");
		KvTableFileParser kvTableFileParser = new KvTableFileParser(url);
		KvTableFileParser.KvTableItem items[] = kvTableFileParser.parse();
		if(items != null) {
			Statement stmt = conn.createStatement();
			for(KvTableItem item : items) {
				String sql = String.format(Sql.INSERT_KVTABLE_CLOB,
										   wrapForSQL(item.k),
										   wrapForSQL(item.v));
				stmt.executeUpdate(sql);
				System.out.println("Populated: '"+item.k + "' - '"+item.v + "'");
			}
		}
		else {
			System.out.println("coulnd not insert kv table items in the " +
							   "database because items[] is null");
		}
	}
	
	private void populateCourses(Connection conn) throws IOException, DataFileSyntaxException, SQLException, DataException {
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
		
		populateForums(coursesList, conn);
		
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
						//System.out.println("----- populating competency " + competency.getTitle() + " -----");
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
								mentor.getUserid());
						stmt = conn.createStatement();
						stmt.executeUpdate(sql);
					} catch(SQLException sqle) {
						System.out.println("Could not populate mentor " + mentor.getUserid() + " for course " + course.getId() + " " + sqle);
					}
				}
				
			}
		}
	}

	private void populateForums(List<Course> coursesList, Connection conn) 
		throws SQLException, DataException {
		UserMeta student = TestObjectsRepository.getInstance().getUserUserMeta("dvidakovich").userMeta;
		
		for(Course course : coursesList) {
			//create the forum
			Forum forum = new Forum(course.getId(), course.getTitle(), course.getTitle());
			this.dataFacade.insertDiscussionForum(conn, forum);
//			String sql = String.format(Sql.INSERT_DISCUSSION_FORUM, 
//									   wrapForSQL(course.getId()),
//									   wrapForSQL(course.getTitle()),
//									   wrapForSQL(course.getTitle()));
//			Statement stmt = conn.createStatement();
//			stmt.executeUpdate(sql);
			System.out.println("Added forum for '" + course.getId() + "'");
			//add some questions to the forum
			for(int i=0; i<3; i++) {
				Question question = new Question(0, 
												 "Question " + String.valueOf(i),
												 "Contents for question " + String.valueOf(i),
												 course.getId());
				question.setUserMeta(student);
				appFacade.askQuestion(conn, question);
				
//				String questionSql = String.format(Sql.INSERT_QUESTION,
//						   student.getUserid(),
//						   wrapForSQL(course.getId()),
//						   wrapForSQL("Question " + String.valueOf(i)),
//						   wrapForSQL("Contents for question " + String.valueOf(i)));
//				stmt = conn.createStatement();
//				int rows = stmt.executeUpdate(questionSql);
				System.out.println("Added question for '" + course.getId() + "'");
				
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

	public final void createTables(Connection conn)
							  throws IOException {
		URL url = appClassLoader.getResource(BASE_PATH + TABLES_DATA_FILE);		
		String sqls[] = (new CreateTablesFileParser(url)).parse();
		System.out.println("----- creating tables -----");
		for(String sql : sqls) {
			if(sql != null && !sql.trim().equals("")) {
				try {
//					System.out.println("Executing table creation sql");
//					System.out.println(sql);
					
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
