package org.wwald.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class DataStore {
	
	private static final String url = "jdbc:hsqldb:mem:mymemdb";
	private static final String user = "SA";
	private static final String password = "";
	private Connection conn;
	
	public DataStore(Connection conn) {
		this.conn = conn;
		try {
			initData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DataStore() {		
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			conn = DriverManager.getConnection(url, user, password);
			initData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Course> getAllCourses() {
		List<Course> courses = null;
		
		String sqlToFetchAllCourses = "SELECT * FROM COURSE;";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sqlToFetchAllCourses);
			
			courses = buildCourseObjectsFromResultSet(rs);
			buildCompetenciesForCourses(courses);
			buildMentorsForCourses(courses);
		} catch(SQLException sqle) {
			
		}
		
		return courses;
	}

	public Course getCourse(String id) {
		Course course = null;
		try {
			String sqlToGetCourseById = "SELECT * FROM COURSE WHERE id=%s";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sqlToGetCourseById, wrapForSQL(id)));
			while(rs.next()) {
				String title = rs.getString(2);
				String description = rs.getString(3);
				course = new Course(id, title, description);
				if(course != null) {
					List<Course> courses = new ArrayList<Course>();
					courses.add(course);
					buildCompetenciesForCourses(courses);
					buildMentorsForCourses(courses);
					course = courses.get(0);
				}
			}
		} catch(SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return course;
	}
	
	private void initData() throws SQLException {
		createTables();
		populateTables();
	}

	private List<Course> buildCourseObjectsFromResultSet(ResultSet rs) throws SQLException {
		if(rs == null) {
			return null;
		}
		List<Course> courses = new ArrayList<Course>();
		while(rs.next()) {
			String id = rs.getString(1);
			String title = rs.getString(2);
			String description = rs.getString(3);
			Course course = new Course(id, title, description);
			courses.add(course);
		}
		return courses;
	}
	
	private void buildCompetenciesForCourses(List<Course> courses) throws SQLException {
		String sqlToGetCompetencyIdsForCourse = "SELECT (competency_id) FROM COURSE_COMPETENCY WHERE course_id = %s";
		for(Course course : courses) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sqlToGetCompetencyIdsForCourse, wrapForSQL(course.getId())));
			List<Competency> competencies = buildCompetencyObjectsFromResultSet(rs);
			course.setCompetencies(competencies);
			
		}
	}

	private void buildMentorsForCourses(List<Course> courses) throws SQLException {
		String sqlToGetMentorIdsForCourse = "SELECT (mentor_id) FROM COURSE_MENTORS WHERE course_id=%s";
		for(Course course : courses) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sqlToGetMentorIdsForCourse, wrapForSQL(course.getId())));
			List<Mentor> mentors = buildMentorObjectsFromResultSet(rs);
			course.setMentor(mentors.get(0));
		}
	}

	private List<Competency> buildCompetencyObjectsFromResultSet(ResultSet rs) throws SQLException {
		if(rs == null) {
			return null;
		}
		List<Competency> competencies = new ArrayList<Competency>();
		while(rs.next()) {
			String competency_id = rs.getString(1);
			
			String sqlToFetchCompetency = "SELECT * FROM COMPETENCY WHERE COMPETENCY.id=%s";
			
			Statement stmt = conn.createStatement();
			ResultSet rsForCompetencies = stmt.executeQuery(String.format(sqlToFetchCompetency, wrapForSQL(competency_id)));
			
			while(rsForCompetencies.next()) {
				String id = rsForCompetencies.getString(1);
				String title = rsForCompetencies.getString(2);
				String description = rsForCompetencies.getString(3);
				String resource = rsForCompetencies.getString(4);
				Competency competency = new Competency(id, title, description, resource);
				competencies.add(competency);
			}
		}
		return competencies;
	}

	private List<Mentor> buildMentorObjectsFromResultSet(ResultSet rs) throws SQLException {
		List<Mentor> mentors = new ArrayList<Mentor>();
		while(rs.next()) {
			int mentorId = rs.getInt(1);
			String sqlToGetMentor = "SELECT * FROM MENTOR WHERE id=%s";
			Statement stmt = conn.createStatement();
			ResultSet mentorsResultSet = stmt.executeQuery(String.format(sqlToGetMentor, String.valueOf(mentorId)));
			while(mentorsResultSet.next()) {
				String firstName = mentorsResultSet.getString(2);
				String middleInitial = mentorsResultSet.getString(3);
				String lastName = mentorsResultSet.getString(4);
				String shortBio = mentorsResultSet.getString(5);
				Mentor mentor = new Mentor(mentorId, firstName, middleInitial, lastName, shortBio);
				mentors.add(mentor);
			}
		}
		return mentors;
	}
	
	private void createTables() throws SQLException {
		Statement s = conn.createStatement();
		String sql = null;
		
		sql = getSqlToCreateCourseTable();
		s.executeUpdate(sql);
		
		sql = getSqlToCreateCompetencyTable();
		s.executeUpdate(sql);
		
		sql = getSqlToCreateMentorTable();
		s.executeUpdate(sql);		
	}
	
	private void populateTables() throws SQLException {
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

	private String getSqlToCreateMentorTable() {
		String createMentorTable = 
			" CREATE TABLE MENTOR (" +
			" 	id INTEGER NOT NULL PRIMARY KEY," +
			"	first_name VARCHAR(32)," +
			"	middle_initial VARCHAR(1)," +
			"	last_name VARCHAR(32)," +
			"	short_bio VARCHAR(2048));";
		
		String createCourseMentorTable = 
			" CREATE TABLE COURSE_MENTORS (" +
			" 	course_id VARCHAR(16) NOT NULL," +
			"	mentor_id INTEGER NOT NULL," +
			"		PRIMARY KEY(course_id, mentor_id)," +
			"		CONSTRAINT course_mentors_col1_fk FOREIGN KEY (course_id) REFERENCES COURSE(id)," +
			"		CONSTRAINT course_mentors_col2_fk FOREIGN KEY (mentor_id) REFERENCES MENTOR(id));";
		
		return createMentorTable + "\n" + createCourseMentorTable;
	}

	private String getSqlToCreateCompetencyTable() {
		String createCompetencyTable = 
			" CREATE TABLE COMPETENCY (" +
			" 	id VARCHAR(16) NOT NULL PRIMARY KEY," +
			"	title VARCHAR(128) NOT NULL," +
			"	description VARCHAR(2048)," +
			"	resources VARCHAR(2048));";
		
		String createCourseToCompetencyTable = 
			" CREATE TABLE COURSE_COMPETENCY (" +
			" 	course_id VARCHAR(16) NOT NULL," +
			" 	competency_id VARCHAR(16) NOT NULL," +
			" 		PRIMARY KEY(course_id, competency_id)," +
			" 		CONSTRAINT course_competency_col1_fk FOREIGN KEY (course_id) REFERENCES COURSE(id)," +
			" 		CONSTRAINT course_competency_col2_fk FOREIGN KEY (competency_id) REFERENCES COMPETENCY(id));";
		
		return createCompetencyTable + "\n" + createCourseToCompetencyTable;
	}

	private String getSqlToCreateCourseTable() {
		return " CREATE TABLE COURSE (" +
			   " 	id VARCHAR(16) NOT NULL PRIMARY KEY," +
			   " 	title VARCHAR(128) NOT NULL," +
			   " 	description VARCHAR(1024));";
	}

	private String wrapForSQL(String s) {
		return "'" + s + "'";
	}
	
	public static void main(String args[]) {
		DataStore dataStore = new DataStore();
		
	}

}
