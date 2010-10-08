package org.wwald.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
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
	
	private static int nextCompetencyId = 10;
	
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

	public List<Course> getAllCoursesToDisplay() {
		List<Course> courses = new ArrayList<Course>();
		String wikiContent = getCoursesWikiContents();
		try {
			if(wikiContent != null && !wikiContent.trim().equals("")) {
				buildCourseObjectsFromCoursesWikiContent(courses, wikiContent);
			}
		} catch(IOException ioe) {
			cLogger.warn("Could not parse wiki contents", ioe);
		}
		return courses;
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

	public String getCoursesWikiContents() {
		String wikiContents = "";
		String sql = "SELECT * FROM COURSES_WIKI;";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				wikiContents = rs.getString(2);
			}
		} catch(SQLException sqle) {
			cLogger.warn("Could not fetch courses wiki contents from the database", sqle);
		}
		return wikiContents;
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
	
	public Course createCourse(Course course) {
		Course newCourse = null;
		Statement stmt = null;
		int rowsUpdated = 0;
		try {
			//create course
			String sqlToCreateCourse = "INSERT INTO COURSE (id, title) VALUES (%s,%s)";
			stmt = conn.createStatement();
			rowsUpdated = stmt.executeUpdate(String.format(sqlToCreateCourse, Data.wrapForSQL(course.getId()), Data.wrapForSQL(course.getTitle())));
			cLogger.info("Rows updated after inserting course " + rowsUpdated);
			
			//create course_competency_wiki 
			stmt = conn.createStatement();
			String sqlToCreateCourseCompetency = "INSERT INTO COURSE_COMPETENCIES_WIKI (course_id, contents) VALUES (%s,'');";
			stmt.executeUpdate(String.format(sqlToCreateCourseCompetency, Data.wrapForSQL(course.getId())));
			cLogger.info("Rows updated after inserting course_competencies_wiki " + rowsUpdated);
			
			newCourse = getCourse(course.getId());
		} catch(SQLException sqle) {
			cLogger.error("Could not create new course " + course, sqle);
		}
		return newCourse;
	}

	public void updateCoursesWikiContents(Object modelObject) {
		String coursesWikiContents = (String)modelObject;
		String sql = "UPDATE COURSES_WIKI SET content=%s WHERE id=1";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			int rowsUpdated = stmt.executeUpdate(String.format(sql, Data.wrapForSQL(coursesWikiContents)));
			if(rowsUpdated > 0) cLogger.info("CoursesWiki updated");
			else cLogger.info("CoursesWiki not updated");
		} catch(SQLException sqle) {
			cLogger.error("Could not update CoursesWiki with new data", sqle);
		}
	}

	public String getCompetenciesWikiContents(String courseId) {
		String wikiContents = "";
		String sql = "SELECT * FROM COURSE_COMPETENCIES_WIKI WHERE course_id=%s;";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sql, Data.wrapForSQL(courseId)));
			if(rs.next()) {
				wikiContents = rs.getString(2);
			}
		} catch(SQLException sqle) {
			cLogger.error("Could not get contents of CompetenciesWiki table", sqle);
		}
		return wikiContents;
	}

	public Competency getCompetency(String courseId, String sCompetencyId) {
		Competency competency = null;
		Course course = getCourse(courseId);
		competency = course.getCompetency(sCompetencyId);
		return competency;
	}

	public void updateCompetenciesWikiContents(String courseId, Object modelObject) {
		String competenciesWikiContents = (String)modelObject;
		String sql = "UPDATE COURSE_COMPETENCIES_WIKI SET contents=%s WHERE course_id=%s;";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			int rowsUpdated = stmt.executeUpdate(String.format(sql, Data.wrapForSQL(competenciesWikiContents), Data.wrapForSQL(courseId)));
			if(rowsUpdated > 0) { 
				cLogger.info("CompetenciesWiki updated");
			}
			else { 
				cLogger.info("CompetenciesWiki not updated");
			}
		} catch(SQLException sqle) {
			cLogger.error("Could not update CompetenciesWiki with new data", sqle);
		}
	}
	
	public void updateCompetency(String courseId, Competency competency) {
		String sql = "UPDATE COMPETENCY SET COMPETENCY.description=%s, COMPETENCY.resources=%s WHERE COMPETENCY.id=%s";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String finalSql = String.format(sql, Data.wrapForSQL(competency.getDescription()), Data.wrapForSQL(competency.getResource()), String.valueOf(competency.getId()));
			stmt.executeUpdate(finalSql);
		} catch (SQLException e) {
			cLogger.error("Could not update competency with these new values ", e);
		}
		
	}

	public Competency buildCompetencyFromTitle(String competencyTitle) {
		Competency competency = null;
		String sql = "INSERT INTO COMPETENCY (id, title, description, resources) VALUES (%s, %s, '', '');";
		String sCompetencyId = String.valueOf(++nextCompetencyId);
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			int rowsUpdated = stmt.executeUpdate(String.format(sql, sCompetencyId, Data.wrapForSQL(competencyTitle)));
			if(rowsUpdated > 0) {
				competency = new Competency(nextCompetencyId, competencyTitle, "", "");
			}
		} catch(SQLException sqle) {
			cLogger.error("Could not insert competency for title '" + competencyTitle + "'", sqle);
		}
		return competency;
	}

	private void buildCourseObjectsFromCoursesWikiContent(List<Course> courses,
														  String wikiContent) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new StringReader(
				wikiContent));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			String lineTokens[] = line.split("\\|");
			String courseId = lineTokens[0];
			String courseTitle = lineTokens[1];
			// It does not matter if the courseTitle is null, but as a rule we
			// want the wikiContent
			// to contain both for each course
			if (courseId != null && courseTitle != null) {
				Course course = getCourse(courseId.trim());
				if (course == null) {
					course = new NonExistentCourse(courseId, courseTitle);
				}
				// TODO If we change the courseTitle in the wiki page then there
				// should be a way to change it
				// in the database as well
				courses.add(course);
			}

		}
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
//		String sqlToGetCompetencyIdsForCourse = "SELECT (competency_id) FROM COURSE_COMPETENCY WHERE course_id = %s";
		String sql = "SELECT (contents) FROM COURSE_COMPETENCIES_WIKI WHERE course_id = %s";
		for(Course course : courses) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sql, Data.wrapForSQL(course.getId())));
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
			if(mentors.size() != 0) {
				course.setMentor(mentors.get(0));
			}
			else {
				cLogger.warn("Mentors were not found for course : " + course.getId());
			}
		}
	}

//	private List<Competency> buildCompetencyObjectsFromResultSet(ResultSet rs) throws SQLException {
//		if(rs == null) {
//			return null;
//		}
//		List<Competency> competencies = new ArrayList<Competency>();
//		while(rs.next()) {
//			String competency_id = rs.getString(1);
//			
//			String sqlToFetchCompetency = "SELECT * FROM COMPETENCY WHERE COMPETENCY.id=%s";
//			
//			Statement stmt = conn.createStatement();
//			ResultSet rsForCompetencies = stmt.executeQuery(String.format(sqlToFetchCompetency, Data.wrapForSQL(competency_id)));
//			
//			while(rsForCompetencies.next()) {
//				int id = rsForCompetencies.getInt(1);
//				String title = rsForCompetencies.getString(2);
//				String description = rsForCompetencies.getString(3);
//				String resource = rsForCompetencies.getString(4);
//				Competency competency = new Competency(id, title, description, resource);
//				competencies.add(competency);
//			}
//		}
//		return competencies;
//	}
	
	private List<Competency> buildCompetencyObjectsFromResultSet(ResultSet rs) throws SQLException {
		if(rs == null) {
			return null;
		}
		List<Competency> competencies = new ArrayList<Competency>();
		if(rs.next()) {
			String competencyWikiContents = rs.getString(1);
			String competencyTitles[] = parseForCompetencyTitles(competencyWikiContents);
			
			for(String competencyTitle : competencyTitles) {
				String sqlToFetchCompetency = "SELECT * FROM COMPETENCY WHERE COMPETENCY.title=%s";
				Statement stmt = conn.createStatement();
				String finalSql = String.format(sqlToFetchCompetency, Data.wrapForSQL(competencyTitle));
				ResultSet rsForCompetencies = stmt.executeQuery(finalSql);
				if(rsForCompetencies.next()) {
					int id = rsForCompetencies.getInt(1);
					String title = rsForCompetencies.getString(2);
					String description = rsForCompetencies.getString(3);
					String resource = rsForCompetencies.getString(4);
					Competency competency = new Competency(id, title, description, resource);
					competencies.add(competency);
				}
				else {
					Competency competency = buildCompetencyFromTitle(competencyTitle);
					if(competency != null) {
						competencies.add(competency);
					}
				}
			}
		}
		return competencies;
	}

	private String[] parseForCompetencyTitles(String competencyWikiContents) {
		List<String> titlesList = new ArrayList<String>();
		BufferedReader bufferedReader = new BufferedReader(new StringReader(competencyWikiContents));
		String line = null;
		try {
			while((line = bufferedReader.readLine()) != null) {
				titlesList.add(line.trim());
			}
		} catch(IOException ioe) {
			
		}
		return titlesList.toArray(new String[titlesList.size()]);
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
