package org.wwald.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class DataFacade {
	
	private static final String url = "jdbc:hsqldb:mem:mymemdb";
	private static final String user = "SA";
	private static final String password = "";
	private Connection conn;
	
	private static Logger cLogger = Logger.getLogger(DataFacade.class);
	
	public DataFacade(Connection conn) {
		this.conn = conn;
		initData();
	}
	
	public DataFacade() {		
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			conn = DriverManager.getConnection(url, user, password);
			initData();
		} catch (ClassNotFoundException cnfe) {
			cLogger.error("Could not load database driver", cnfe);
		} catch(SQLException sqle) {
			cLogger.error("Could not obtain database connection", sqle);
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
			//TODO: At some point of time we need to send this exception to the view page which should then
			//redirect to a standard error page with an error message
			cLogger.error("Could not build list of courses", sqle);
		}
		
		return courses;
	}

	public Course getCourse(String id) {
		Course course = null;
		try {
			String sqlToGetCourseById = "SELECT * FROM COURSE WHERE id=%s";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sqlToGetCourseById, Data.wrapForSQL(id)));
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
			//TODO: At some point of time we need to send this exception to the view page which should then
			//redirect to a standard error page with an error message
			cLogger.error("Could not get course", sqle);
		}
		
		return course;
	}
	
	private void initData() {
		Data.init(conn);
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
			ResultSet rs = stmt.executeQuery(String.format(sqlToGetCompetencyIdsForCourse, Data.wrapForSQL(course.getId())));
			List<Competency> competencies = buildCompetencyObjectsFromResultSet(rs);
			course.setCompetencies(competencies);
			
		}
	}

	private void buildMentorsForCourses(List<Course> courses) throws SQLException {
		String sqlToGetMentorIdsForCourse = "SELECT (mentor_id) FROM COURSE_MENTORS WHERE course_id=%s";
		for(Course course : courses) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sqlToGetMentorIdsForCourse, Data.wrapForSQL(course.getId())));
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
			ResultSet rsForCompetencies = stmt.executeQuery(String.format(sqlToFetchCompetency, Data.wrapForSQL(competency_id)));
			
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

}
