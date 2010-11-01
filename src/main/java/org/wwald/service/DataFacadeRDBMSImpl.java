package org.wwald.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.wwald.model.Competency;
import org.wwald.model.Course;
import org.wwald.model.CourseEnrollmentStatus;
import org.wwald.model.Mentor;
import org.wwald.model.NonExistentCourse;
import org.wwald.model.Role;
import org.wwald.model.StatusUpdate;
import org.wwald.model.User;
import org.wwald.model.UserCourseStatus;


public class DataFacadeRDBMSImpl implements IDataFacade {
	
	private static final String url = "jdbc:hsqldb:mem:mymemdb";
	private static final String user = "SA";
	private static final String password = "";
	
	//private static int nextCompetencyId = 10;
	
	private static Logger cLogger = Logger.getLogger(DataFacadeRDBMSImpl.class);
	
	public DataFacadeRDBMSImpl() {		
			
	}

	public List<Course> retreiveCourses(Connection conn) throws DataException {
		List<Course> courses = null;
		
		String sqlToFetchAllCourses = "SELECT * FROM COURSE;";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sqlToFetchAllCourses);
			
			courses = buildCourseObjectsFromResultSet(rs);
			buildCompetenciesForCourses(conn, courses);
			buildMentorsForCourses(conn, courses);
		} catch(SQLException sqle) {
			String msg = "Could not build courses";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		
		return courses;
	}

	public String retreiveCourseWiki(Connection conn) throws DataException {
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
			String msg = "Could not fetch courses wiki contents from the database";
			cLogger.warn(msg, sqle);
			throw new DataException(msg, sqle);
		}
		return wikiContents;
	}

	public Course retreiveCourse(Connection conn, String id) throws DataException {
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
					buildCompetenciesForCourses(conn, courses);
					buildMentorsForCourses(conn, courses);
					course = courses.get(0);
				}
			}
		} catch(SQLException sqle) {
			String msg = "Could not retreive course '" + id + "'";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		
		return course;
	}
	
	public void insertCourse(Connection conn, Course course) throws DataException {
		Statement stmt = null;
		int rowsUpdated = 0;
		try {
			//create course
			String sqlToCreateCourse = "INSERT INTO COURSE (id, title) VALUES (%s,%s)";
			stmt = conn.createStatement();
			rowsUpdated = stmt.executeUpdate(String.format(sqlToCreateCourse, wrapForSQL(course.getId()), wrapForSQL(course.getTitle())));
			cLogger.info("Rows updated after inserting course " + rowsUpdated);
			
			//create course_competency_wiki 
			stmt = conn.createStatement();
			String sqlToCreateCourseCompetency = "INSERT INTO COURSE_COMPETENCIES_WIKI (course_id, contents) VALUES (%s,'');";
			stmt.executeUpdate(String.format(sqlToCreateCourseCompetency, wrapForSQL(course.getId())));
			cLogger.info("Rows updated after inserting course_competencies_wiki " + rowsUpdated);
		} catch(SQLException sqle) {
			cLogger.error("Could not create new course " + course, sqle);
		}
	}

	public void updateCourseWiki(Connection conn, String wikiContents) throws DataException {
		//TODO: If we change the course title then the changes should be reflected in the db
		//Also we may want to do some basic validating parsing here... or somewhere before we
		//save the contents
		String coursesWikiContents = (String)wikiContents;
		String sql = "UPDATE COURSES_WIKI SET content=%s WHERE id=1";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			int rowsUpdated = stmt.executeUpdate(String.format(sql, wrapForSQL(coursesWikiContents)));
			if(rowsUpdated > 0) cLogger.info("CoursesWiki updated");
			else cLogger.info("CoursesWiki not updated");
		} catch(SQLException sqle) {
			String msg = "Could not update CoursesWiki with new data '" + wikiContents + "'" ;
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}
	
	public CourseEnrollmentStatus getCourseEnrollmentStatus(Connection conn, User user, Course course) throws DataException {
		String sqlTemplate = "SELECT * FROM COURSE_ENROLLMENT_ACTIONS WHERE course_id=%s AND username=%s ORDER BY tstamp DESC;";
		String sql = String.format(sqlTemplate,
								   wrapForSQL(course.getId()),
								   wrapForSQL(user.getUsername()));
		List<CourseEnrollmentStatus> statuses = new ArrayList<CourseEnrollmentStatus>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				String courseId = rs.getString("course_id");
				String username = rs.getString("username");
				int userCourseStatusId = rs.getInt("course_enrollment_action_id");
				Timestamp tstamp = rs.getTimestamp("tstamp");
				statuses.add(new CourseEnrollmentStatus(courseId, username, UserCourseStatus.getUserCourseStatus(userCourseStatusId), tstamp));
			}
			
		} catch(SQLException sqle) {
			String msg = "Could not get course enrollment status for course_id " + 
						 course.getId() + " username " + user.getUsername();
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		if(statuses.size() > 0) {
			Collections.sort(statuses, CourseEnrollmentStatus.getTimestampComparator());
			return statuses.get(statuses.size()-1);
		}
		else {
			return new CourseEnrollmentStatus(course.getId(), user.getUsername(), UserCourseStatus.UNENROLLED, null);
		}
	}
	
	public void addCourseEnrollmentAction(Connection conn, CourseEnrollmentStatus courseEnrollmentStatus) throws DataException {
		String sqlTemplate = "INSERT INTO COURSE_ENROLLMENT_ACTIONS VALUES (%s, %s, %s, %s);";
		//TODO: Remove hardcoded date
		Timestamp timestamp = new Timestamp((new Date()).getTime());
		String sql = String.format(sqlTemplate, 
								   wrapForSQL(courseEnrollmentStatus.getCourseId()),
								   wrapForSQL(courseEnrollmentStatus.getUsername()),
								   courseEnrollmentStatus.getUserCourseStatus().getId(),
								   wrapForSQL(timestamp.toString()));
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch(SQLException sqle) {
			String msg = "Could not add CourseEnrollmentStatus " + courseEnrollmentStatus;
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}

	public String retreiveCompetenciesWiki(Connection conn, String courseId) {
		String wikiContents = "";
		String sql = "SELECT * FROM COURSE_COMPETENCIES_WIKI WHERE course_id=%s;";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sql, wrapForSQL(courseId)));
			if(rs.next()) {
				wikiContents = rs.getString(2);
			}
		} catch(SQLException sqle) {
			cLogger.error("Could not get contents of CompetenciesWiki table", sqle);
		}
		return wikiContents;
	}

	public Competency retreiveCompetency(Connection conn, String courseId, String sCompetencyId) throws DataException {
		//TODO: We should not retreive Course to get Competency
		Competency competency = null;
		Course course = retreiveCourse(conn, courseId);
		competency = course.getCompetency(sCompetencyId);
		return competency;
	}

	public void updateCompetenciesWikiContents(Connection conn, String courseId, String contents) {
		String competenciesWikiContents = (String)contents;
		String sql = "UPDATE COURSE_COMPETENCIES_WIKI SET contents=%s WHERE course_id=%s;";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			int rowsUpdated = stmt.executeUpdate(String.format(sql, wrapForSQL(competenciesWikiContents), wrapForSQL(courseId)));
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
	
	public void updateCompetency(Connection conn, String courseId, Competency competency) {
		String sql = "UPDATE COMPETENCY SET COMPETENCY.description=%s, COMPETENCY.resources=%s WHERE COMPETENCY.id=%s";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String finalSql = String.format(sql, wrapForSQL(competency.getDescription()), wrapForSQL(competency.getResource()), String.valueOf(competency.getId()));
			stmt.executeUpdate(finalSql);
		} catch (SQLException e) {
			cLogger.error("Could not update competency with these new values ", e);
		}
		
	}

	public Competency insertCompetency(Connection conn, Course course, String competencyTitle) throws DataException {
		Competency competency = new Competency(competencyTitle, "", "");
		String sql = "INSERT INTO COMPETENCY (id, course_id, title, description, resources) VALUES (%s, %s, %s, '', '');"; 
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			int rowsUpdated = stmt.executeUpdate(String.format(sql, 
															   competency.getId(),
															   wrapForSQL(course.getId()),
															   wrapForSQL(competencyTitle)));
			if(rowsUpdated == 0) {
				String msg = "Could not insert competency '" + competencyTitle + "' in course '" + course.getId() + "'";
				throw new DataException(msg);
			}
		} catch(SQLException sqle) {
			String msg = "Could not insert competency for title '" + competencyTitle + "'";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		return competency;
	}
	
	public void deleteCompetency(Connection conn, Competency competency) {
		throw new RuntimeException("method not implemented");
	}

	public void deleteMentor(Connection conn, Mentor mentor) {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}

	public void insertMentor(Connection conn, Mentor mentor) {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}

	public List<Mentor> retreiveMentorsForCompetency(Connection conn) {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	public List<Competency> retreiveAllCompetencies(Connection conn) {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	public List<Mentor> retreiveAllMentors(Connection conn) {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	public List<Competency> retreiveCompetenciesForCourse(Connection conn, Course course) {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	public List<Mentor> retreiveMentorsForCourse(Connection conn) {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	public void updateCourse(Connection conn, Course course) {
		throw new RuntimeException("method not implemented");
		
	}

	public void updateMentor(Connection conn, Mentor mentor) {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}

	public void upsertCompetency(Connection conn, Competency competency) {
		throw new RuntimeException("method not implemented");
	}

	public void upsertCourse(Connection conn, Course course) {
		throw new RuntimeException("method not implemented");
	}

	public void upsertMentor(Connection conn, Mentor mentor) {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}
	
	public List<StatusUpdate> getStatusUpdates(Connection conn) {
		List<CourseEnrollmentStatus> courseEnrollmentStatuses = getAllCourseEnrollmentStatuses(conn);
		List<StatusUpdate> statusUpdates = new ArrayList<StatusUpdate>();
		for(CourseEnrollmentStatus courseEnrollmentStatus : courseEnrollmentStatuses) {			
			statusUpdates.add(new StatusUpdate(courseEnrollmentStatus.getUsername() + " " + courseEnrollmentStatus.getUserCourseStatus() + " course " + courseEnrollmentStatus.getCourseId() + " at " + courseEnrollmentStatus.getTimestamp()));
		}    	
    	return statusUpdates; 
	}
	
	public User retreiveUser(Connection conn, String username, String password) throws DataException {
		User user = null;
		try {
			Statement stmt = conn.createStatement();
			String query = String.format(Sql.RETREIVE_USER, wrapForSQL(username), wrapForSQL(password));
			ResultSet rs  = stmt.executeQuery(query);
			if(rs.next()) {
				String firstName = rs.getString("first_name");
				String mi = rs.getString("mi");
				String lastName = rs.getString("last_name");
				String userName = rs.getString("username");
				Date dateJoined = rs.getDate("join_date");
				String role = rs.getString("role");
				user = new User(firstName,mi,lastName,userName,dateJoined,Role.valueOf(role));
			}
		} catch(SQLException sqle) {
			String msg = "Could not retreive User from database";
			throw new DataException(msg, sqle);
		}
		return user; 
	}

	/**
	 * This method parses the courses wiki contents and returns a List of Course objects. 
	 * Every course which exists in the database is returned as a {@link Course} object in  
	 * the returned List, whereas every course which does not exist in the database is
	 * represented as a {@link NonExistentCourse} object.
	 * NOTE: This method should never return a null.
	 * @param conn Connection to the database
	 * @param wikiContent The contents of the wiki as a String
	 * @return courses A List of Course objects which were mentioned in the wiki contents 
	 * @throws IOException
	 */
	private List<Course> buildCourseObjectsFromCoursesWikiContent(Connection conn,
														  String wikiContent) throws IOException, DataException {
		List<Course> courses = new ArrayList<Course>();
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
				Course course = retreiveCourse(conn, courseId.trim());
				if (course == null) {
					course = new NonExistentCourse(courseId, courseTitle);
				}
				
				courses.add(course);
			}

		}
		return courses;
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
	
	private void buildCompetenciesForCourses(Connection conn, List<Course> courses) throws SQLException, DataException {
//		String sqlToGetCompetencyIdsForCourse = "SELECT (competency_id) FROM COURSE_COMPETENCY WHERE course_id = %s";
		String sql = "SELECT (contents) FROM COURSE_COMPETENCIES_WIKI WHERE course_id = %s";
		for(Course course : courses) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sql, wrapForSQL(course.getId())));
			List<Competency> competencies = buildCompetencyObjectsFromResultSet(conn, course, rs);
			course.setCompetencies(competencies);
			
		}
	}

	private void buildMentorsForCourses(Connection conn, List<Course> courses) throws SQLException {
		String sqlToGetMentorIdsForCourse = "SELECT (mentor_id) FROM COURSE_MENTORS WHERE course_id=%s";
		for(Course course : courses) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sqlToGetMentorIdsForCourse, wrapForSQL(course.getId())));
			List<Mentor> mentors = buildMentorObjectsFromResultSet(conn, rs);
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
//			ResultSet rsForCompetencies = stmt.executeQuery(String.format(sqlToFetchCompetency, wrapForSQL(competency_id)));
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
	//TODO: course is being pased all round the place.... make safe
	private List<Competency> buildCompetencyObjectsFromResultSet(Connection conn, Course course, ResultSet rs) throws SQLException, DataException {
		if(rs == null) {
			return null;
		}
		List<Competency> competencies = new ArrayList<Competency>();
		if(rs.next()) {
			String competencyWikiContents = rs.getString(1);
			String competencyTitles[] = parseForCompetencyTitles(competencyWikiContents);
			
			for(String competencyTitle : competencyTitles) {
				String sqlToFetchCompetency = "SELECT * FROM COMPETENCY WHERE COMPETENCY.course_id=%s AND COMPETENCY.title=%s";
				Statement stmt = conn.createStatement();
				String finalSql = String.format(sqlToFetchCompetency, 
												wrapForSQL(course.getId()),
												wrapForSQL(competencyTitle));
				ResultSet rsForCompetencies = stmt.executeQuery(finalSql);
				if(rsForCompetencies.next()) {
					int id = rsForCompetencies.getInt(1);
					String title = rsForCompetencies.getString(3);
					String description = rsForCompetencies.getString(4);
					String resource = rsForCompetencies.getString(5);
					Competency competency = new Competency(id, title, description, resource);
					competencies.add(competency);
				}
				else {
					Competency competency = insertCompetency(conn, course, competencyTitle);
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

	private List<Mentor> buildMentorObjectsFromResultSet(Connection conn, ResultSet rs) throws SQLException {
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
	
	private List<CourseEnrollmentStatus> getAllCourseEnrollmentStatuses(Connection conn) {
		//TODO: Limit this query to 5 rows
		String sql = "SELECT * FROM COURSE_ENROLLMENT_ACTIONS;";
		List<CourseEnrollmentStatus> statuses = new ArrayList<CourseEnrollmentStatus>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				String courseId = rs.getString("course_id");
				String username = rs.getString("username");
				int userCourseStatusId = rs.getInt("course_enrollment_action_id");
				Timestamp tstamp = rs.getTimestamp("tstamp");
				statuses.add(new CourseEnrollmentStatus(courseId, username, UserCourseStatus.getUserCourseStatus(userCourseStatusId), tstamp));
			}
			
		} catch(SQLException sqle) {
			String msg = "Could not get all course enrollment statuses";
			cLogger.error(msg, sqle);
		}
		return statuses;
	}
	
	public static String wrapForSQL(String s) {
		return "'" + s + "'";
	}

}
