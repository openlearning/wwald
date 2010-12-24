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
import org.wwald.model.StaticPagePOJO;
import org.wwald.model.StatusUpdate;
import org.wwald.model.User;
import org.wwald.model.UserCourseStatus;
import org.wwald.model.UserMeta;
import org.wwald.util.CompetencyUniqueIdGenerator;
import org.wwald.view.UserForm;
import org.wwald.view.UserForm.Field;


public class DataFacadeRDBMSImpl implements IDataFacade {
	
	private static final String url = "jdbc:hsqldb:mem:mymemdb";
	private static final String user = "SA";
	private static final String password = "";
	
	//private static int nextCompetencyId = 10;
	private final String NULL_CONN_ERROR_MSG = "conn cannot be null";
	
	private static Logger cLogger = Logger.getLogger(DataFacadeRDBMSImpl.class);
	
	public DataFacadeRDBMSImpl() {		
			
	}

	/**
	 * Retrieves the list of courses
	 * @param conn The database connection
	 * @throws NullPointerException if conn is null
	 * @throws DataException if the JDBC code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 * @return The list of courses
	 */
	public List<Course> retreiveCourses(Connection conn) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
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

	/**
	 * Retreives the courses wiki which contains all courses which are to be
	 * displayed on the main page
	 * @param conn The database connection
	 * @throws NullPointerException if conn is null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 * @return The courses wiki
	 */
	public String retreiveCourseWiki(Connection conn) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
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

	/**
	 * Retrieves the course for the specified course id
	 * @param conn The database connection
	 * @param id The course id
	 * @throws NullPointerException If conn or id is null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException 
	 * @return The course object for the specified course id, or null if the 
	 * course with the specified id does not exist
	 */
	public Course retreiveCourse(Connection conn, String id) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(id == null) {
			throw new NullPointerException("id cannot be null");
		}
		Course course = null;
		try {
			//TODO: USe the sql from Sql.java
			String sqlToGetCourseById = "SELECT * FROM COURSE WHERE id=%s";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sqlToGetCourseById, 
											 wrapForSQL(id)));
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
	
	/**
	 * Creates a new course object in the database with the bare minimum 
	 * details, which are 'id' and 'title' of the course
	 * @param conn The database connection
	 * @param course The course object to be inserted (Only the 'id' and 'title'
	 * properties are considered while inserting)
	 * @throws NullPointerException if conn, or course is null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public void insertCourse(Connection conn, Course course) 
														throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(course == null) {
			throw new NullPointerException("course cannot be null");
		}
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

	/**
	 * Updates the courses wiki
	 * @param conn The database connection
	 * @param wikiContents The wiki contents
	 * @throws NullPointerException if conn, or wikiContents are null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	//TODO: Document the wiki contents
	//TODO: Refactor method name to updateCoursesWiki
	public void updateCourseWiki(Connection conn, String wikiContents) 
														throws DataException {
		//TODO: If we change the course title then the changes should be reflected in the db
		//Also we may want to do some basic validating parsing here... or somewhere before we
		//save the contents
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(wikiContents == null) {
			throw new NullPointerException("wikiContents cannot be null");
		}
		String coursesWikiContents = (String)wikiContents;
		//TODO: Use Sql.java
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
	
	/**
	 * Retrieves the enrollment status for the specified course and user
	 * @param conn The database connection
	 * @param userMeta The userMeta object for the user whose enrollment status
	 * we want to retrieve
	 * @param course The course for which we want the enrollment status
	 * @throws NullPointerException if conn, userMeta, or course is null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 * @return The {@link CourseEnrollmentStatus} object
	 */
	public CourseEnrollmentStatus getCourseEnrollmentStatus(Connection conn, 
															UserMeta userMeta, 
															Course course) 
														throws DataException {
		String sqlTemplate = Sql.RETREIVE_COURSE_ENROLLMENT_STATUS;
		String sql = String.format(sqlTemplate,
								   wrapForSQL(course.getId()),
								   userMeta.getUserid());
		List<CourseEnrollmentStatus> statuses = new ArrayList<CourseEnrollmentStatus>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				String courseId = rs.getString("course_id");
				int userid = rs.getInt("userid");
				int userCourseStatusId = rs.getInt("course_enrollment_action_id");
				Timestamp tstamp = rs.getTimestamp("tstamp");
				statuses.add(new CourseEnrollmentStatus(courseId, userid, UserCourseStatus.getUserCourseStatus(userCourseStatusId), tstamp));
			}
			
		} catch(SQLException sqle) {
			String msg = "Could not get course enrollment status for course_id " + 
						 course.getId() + " userid " + userMeta.getUserid();
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		if(statuses.size() > 0) {
			Collections.sort(statuses, CourseEnrollmentStatus.getTimestampComparator());
			return statuses.get(statuses.size()-1);
		}
		else {
			return new CourseEnrollmentStatus(course.getId(), userMeta.getUserid(), UserCourseStatus.UNENROLLED, null);
		}
	}
	
	/**
	 * Adds a course enrollment action in the database. A course enrollment
	 * action specified an action performed by a user for either enrolling in
	 * or dropping out of of a course.
	 * @param conn The database connection
	 * @param courseEnrollmentStatus The CourseEnrollmentStatus object which is
	 * to be used for inserting the action in persistent storage
	 */
	public void addCourseEnrollmentAction(Connection conn, 
										  CourseEnrollmentStatus courseEnrollmentStatus) 
						throws DataException {
		Timestamp timestamp = new Timestamp((new Date()).getTime());
		String sql = 
			String.format(Sql.INSERT_COURSE_ENROLLMENT_STATUS,
						  wrapForSQL(courseEnrollmentStatus.getCourseId()),
						  courseEnrollmentStatus.getUserid(),
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

	/**
	 * Retreives the competencies wiki for the specified course id
	 * @param conn The database connection
	 * @param courseId The course id
	 * @throws NullPointerException if conn or courseId is null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 * @return The competencies wiki of the course or an empty string if the
	 * course does not exist or it does not have any contents for the 
	 * competency wiki
	 */
	//TODO: We return an empty string when the course does not exist as 
	//well as when the competency wiki for that course is empty. I think we
	//should return a null when the course does not exist
	public String retreiveCompetenciesWiki(Connection conn, 
										   String courseId) 
														throws DataException {		
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(courseId == null) {
			throw new NullPointerException("courseId cannot be null");
		}
		String wikiContents = "";
		String sql = Sql.RETREIVE_COMPETENCIES_WIKI;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = 
				stmt.executeQuery(String.format(sql, wrapForSQL(courseId)));
			if(rs.next()) {
				wikiContents = rs.getString(2);
			}
		} catch(SQLException sqle) {
			String msg = "Could not get contents of CompetenciesWiki table";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		return wikiContents;
	}

	/**
	 * Retreives the competency for the specified course and the specified
	 * competency id
	 * @param conn The database connection
	 * @param courseId The course id
	 * @param sCompetencyId The competency id as a String
	 * @throws NullPointerException if either conn, courseId, sCompetencyId is null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 * @return The competency or null if the course or competency does not exist
	 */
	public Competency retreiveCompetency(Connection conn, 
										 String courseId, 
										 String sCompetencyId) 
			throws DataException {
		//check method params
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(courseId == null) {
			throw new NullPointerException("courseId cannot be null");
		}
		if(sCompetencyId == null) {
			throw new NullPointerException("sCompetencyId cannot be null");
		}
		try {
			int competencyId = Integer.parseInt(sCompetencyId);
			if(competencyId < 0) {
				String msg = "sCompetencyId should be a positive integer";
				throw new IllegalArgumentException(msg);
			}
		} catch(NumberFormatException nfe) {
			String msg = "sCompetencyId should be a valid positive integer";
			throw new IllegalArgumentException(msg, nfe);
		}
		
		//TODO: We should not retreive Course to get Competency
		Competency competency = null;
		Course course = retreiveCourse(conn, courseId);
		competency = course.getCompetency(sCompetencyId);
		return competency;
	}

	/**
	 * Update the competencies wiki for the specified course
	 * @param conn The database connection
	 * @param courseId The course id
	 * @param contents The new contents of the course competency wiki
	 * @throws NullPointerException If either conn, or courseId, or contents 
	 * are null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public void updateCompetenciesWikiContents(Connection conn, String courseId, String contents) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(courseId == null) {
			throw new NullPointerException("courseId cannot be null");
		}
		if(contents == null) {
			throw new NullPointerException("contents cannot be null");
		}
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
			String msg = "Could not update CompetenciesWiki due to an Exception";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}

	/**
	 * Creates an empty competency object in the database for the specified
	 * course
	 * @param conn The database connection
	 * @param course The course for which the competency should be added
	 * @param competencyTitle The competency title
	 * @throws NullPointerException If either conn, course, or competencyTitle
	 * is null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 * @return The empty {@link Competency} object created for the specified
	 * competencyTitle 
	 */
	public Competency insertCompetency(Connection conn, 
									   Course course, 
									   String competencyTitle) 
														throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(course == null) {
			throw new NullPointerException("course should not be null");
		}
		if(competencyTitle == null) {
			throw new NullPointerException("competencyTitle should not be null");
		}
		
		//TODO CompetencyUniqueIdGenerator needs to go
		Competency competency = new Competency(CompetencyUniqueIdGenerator.getNextCompetencyId(conn), competencyTitle, "", "");
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
	
	/**
	 * Updates {@link Competency} with the spceified {@link Competency} object.
	 * @param conn The databse connection object
	 * @param courseId The courseId of the {@link Course} for which we want to 
	 * update the {@link Competency}
	 * @param competency The updated {@link Competency} object
	 * @throws NullPointerException If either conn, courseId, or competency are
	 * null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public void updateCompetency(Connection conn, 
								 String courseId, 
								 Competency competency) 
														throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(courseId == null) {
			throw new NullPointerException("courseId should not be null");
		}
		if(competency == null) {
			throw new NullPointerException("competency should not be null");
		}
		String sql = Sql.UPDATE_COMPETENCY;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String finalSql = String.format(sql, 
											wrapForSQL(competency.getDescription()), 
											wrapForSQL(competency.getResource()), 
											String.valueOf(competency.getId()));
			stmt.executeUpdate(finalSql);
		} catch (SQLException e) {
			cLogger.error("Could not update competency with these new values ", e);
		}
		
	}
	
	/**
	 * Deletes the specified {@link Competency} from the database
	 * @param conn The database {@link Connection}
	 * @param competency The {@link Competency} to delete
	 * @throws NullPointerException If either conn or competency is null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public void deleteCompetency(Connection conn, Competency competency) throws DataException {
		throw new RuntimeException("method not implemented");
	}

	/**
	 * Deletes the specified {@link Mentor} from the database
	 * @param conn The database connection
	 * @param mentor The {@link Mentor} to delete
	 * @throws NullPointerException If either conn or mentor is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * SQLException is wrapped in the {@link DataException}
	 */
	public void deleteMentor(Connection conn, Mentor mentor) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}

	/**
	 * Inserts the specified {@link Mentor} in the database
	 * @param conn The database connection
	 * @param mentor The {@link Mentor} to delete
	 * @throws NullPointerException If either conn or mentor is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void insertMentor(Connection conn, 
							 Mentor mentor) 
														throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}

	/**
	 * Retreives {@link Mentor}s for the specified {@link Competency}
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	//TOTO: Where is the Competency?
	public List<Mentor> retreiveMentorsForCompetency(Connection conn) 
														throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	/**
	 * Retrieves all the {@link Competency} from the database
	 * @param conn The database connection
	 * @throws NullPointerException If conn null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public List<Competency> retreiveAllCompetencies(Connection conn) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	/**
	 * Retrieves a list containing all the mentors in the system
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException} 
	 */
	public List<Mentor> retreiveAllMentors(Connection conn) throws DataException {
		List<Mentor> mentors = new ArrayList<Mentor>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(Sql.RETREIVE_ALL_MENTORS);
			while(rs.next()) {
				Mentor mentor = new Mentor();
				mentor.setUserid(rs.getInt("userid"));
				mentor.setIdentifier(rs.getString("identifier"));
				String role = rs.getString("role");
				mentor.setRole(Role.valueOf(role));
				mentors.add(mentor);
			}			
		} catch(SQLException sqle) {
			String msg = "Could not retreive mentors";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		
		return mentors;
	}

	/**
	 * Retrieves a list of competencies for the specified course
	 * @param conn The database connection
	 * @param course The course
	 * @throws NullPointerException If either conn or course is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return A {@link List} of {@link Competency} objects for the specified
	 * {@link Course}
	 */
	public List<Competency> retreiveCompetenciesForCourse(Connection conn, 
														  Course course) 
														  throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}
	
	/**
	 * Retrieves a {@link List} of {@link Mentor}s for the specified {@link Course}
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return The List of {@link Mentor}s for the specified {@link Course}
	 */
	public List<Mentor> retreiveMentorsForCourse(Connection conn) throws DataException {
		//TODO: Need to provide a Course object as a param
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	/**
	 * Updates the {@link Course}. Specifically the title and description
	 * properties of the {@link Course} are updated. The id cannot be updated.
	 * The course's Mentor is also updated.
	 * @param conn The database connection
	 * @param course The course with updated fields
	 * @throws If either conn or course is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void updateCourse(Connection conn, Course course) throws DataException {
		String sql = String.format(Sql.UPDATE_COURSE, 
								   wrapForSQL(course.getTitle()), 
								   wrapForSQL(course.getDescription()), 
								   wrapForSQL(course.getId()));
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(course == null) {
			throw new NullPointerException("course cannot be null");
		}
		try {
			cLogger.info("Executing SQL '''" + sql + "'''");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			
			//TODO: Good Samaritan is the default mentor for every course... if the mentor was removed then add this one
			sql = String.format(Sql.UPDATE_COURSE_MENTORS, course.getMentor().getUserid(), wrapForSQL(course.getId()));
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch(SQLException sqle) {
			String msg = "Could not update course " + course.getId();
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}

	/**
	 * Updates a {@link Mentor}
	 * @param conn The database connection
	 * @param mentor The {@link Mentor} object with updated fields
	 * @throws NullPointerException If either conn or mentor are null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException} 
	 */
	public void updateMentor(Connection conn, Mentor mentor) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}

	/**
	 * Updates or inserts the specified {@link Competency}
	 * @param conn The database connection
	 * @param competency The {@link Competency} object
	 * @throws NullPointerException If either conn or competency are null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException} 
	 */
	public void upsertCompetency(Connection conn, Competency competency) throws DataException {
		throw new RuntimeException("method not implemented");
	}

	/**
	 * Updates or inserts the specified {@link Course}
	 * @param conn The database connection
	 * @param course The {@link Course} object
	 * @throws NullPointerException If either conn or course are null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException} 
	 */
	public void upsertCourse(Connection conn, Course course) throws DataException {
		throw new RuntimeException("method not implemented");
	}

	/**
	 * Updates or inserts the specified {@link Mentor}
	 * @param conn The database connection
	 * @param mentor The {@link Mentor} object
	 * @throws NullPointerException If either conn or mentor are null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException} 
	 */
	public void upsertMentor(Connection conn, Mentor mentor) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}
	
	/**
	 * Returns a list of {@link StatusUpdate} objects
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return A {@link List} of {@link StatusUpdate} objects
	 */
	public List<StatusUpdate> getStatusUpdates(Connection conn) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		try {
			List<CourseEnrollmentStatus> courseEnrollmentStatuses = getAllCourseEnrollmentStatuses(conn);
			List<StatusUpdate> statusUpdates = new ArrayList<StatusUpdate>();
//			DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
			//TODO: Factor this out into a separate StatusUpdateFormat
			for(CourseEnrollmentStatus courseEnrollmentStatus : courseEnrollmentStatuses) {
				
				Timestamp timestamp = courseEnrollmentStatus.getTimestamp();
				int userid = courseEnrollmentStatus.getUserid();
				UserMeta userMeta = retreiveUserMeta(conn, userid);
				String enrollmentStatus = getEnrollmentStatusWithSurroundingText(courseEnrollmentStatus.getUserCourseStatus());
				String courseId = courseEnrollmentStatus.getCourseId();
				
				StatusUpdate statusUpdate = new StatusUpdate();
				statusUpdate.setTimestamp(timestamp);
				statusUpdate.setUserMeta(userMeta);
				statusUpdate.setEnrollmentStatus(enrollmentStatus);
				statusUpdate.setCourseId(courseId);
				
				statusUpdates.add(statusUpdate);
			}    	
	    	return statusUpdates;
		} catch(SQLException sqle) {
			String msg = "Could not get status updates due to an exception";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}

	/**
	 * Inserts a {@link User} and it's corresponding {@link UserMeta} object in 
	 * the database
	 * @param conn The database connection
	 * @param user The user object
	 * @param userMeta The UserMeta object to be inserted into the database
	 * @throws NullPointerException If either conn, user, ot userMeta is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void insertUser(Connection conn, 
						   User user, 
						   UserMeta userMeta) 
														throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(user == null) {
			throw new NullPointerException("user cannot be null");
		}
		if(userMeta == null) {
			throw new NullPointerException("userMeta cannot be null");
		}
		//TODO: This method must return the UserMeta object with the updated UserMeta
		try {
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Statement stmt = conn.createStatement();
			String sql = String.format(Sql.INSERT_USER, 
									   wrapForSQL(user.getUsername()),
									   wrapForSQL(user.getEncryptedPassword()),
									   wrapForSQL(user.getEmail()),
									   wrapForSQL(userMeta.getIdentifier()),
									   wrapForSQL(userMeta.getLoginVia().toString()),
									   wrapForSQL(Role.STUDENT.toString())); //forcing a new user to have the role of student
			cLogger.info("Executing SQL '" + sql + "'");
			stmt.executeUpdate(sql);			
		} catch(SQLException sqle) {
			String msg = "Could not insert User due to an exception";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}
	
	/**
	 * Updates the {@link User} object in the database. Valid fields for
	 * updating are 'email', and 'password'. The username cannot be updated.
	 * @param conn The database connection
	 * @param use The updated user object 
	 * @param userFields A varags array of {@link UserForm.Field} objects which
	 * denote which fields of the specified {@link User} object should be
	 * updated. If a value is not specified for this parameter then both fields
	 * 'email', and 'password' will be updated.
	 * @throws IllegalArgumentException If {@link Field.USERNAME} is specified 
	 * in userFields for the fields to be updated
	 * @throws NullPointerException If either conn or user are null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void updateUser(Connection conn, 
						   User user, 
						   UserForm.Field... userFields) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(user == null) {
			throw new NullPointerException("user cannot be null");
		}
		for(UserForm.Field field : userFields) {
			if(field.equals(UserForm.Field.USERNAME)) {
				throw new IllegalArgumentException("updateUser should never be given UserForm.Field.USERNAME");
			}
		}
		try {
			Statement stmt = conn.createStatement();
			String sql = null;
			if(userFields == null || userFields.length == 0) {
				sql = String.format(Sql.UPDATE_USER, 
						   wrapForSQL(user.getEmail()),
						   wrapForSQL(user.getEncryptedPassword()),
						   wrapForSQL(user.getUsername()));
			}
			else {
				sql = "UPDATE USER SET ";
				for(UserForm.Field field : userFields) {
					sql += field.getDbColName() + "=%s, ";
				}
				//remove the comma
				sql = sql.substring(0,sql.length()-2);
				sql = sql + " ";
				//get back to building the sql
				sql += "WHERE username=%s;";
				sql = String.format(sql, getUserFieldValues(userFields, user));
			}
			int rowsUpdated = stmt.executeUpdate(sql);
			if(rowsUpdated == 0) {
				cLogger.warn("Tried updating user but 0 rows were affected '" + user + "'");
			}
			else {
				cLogger.info("User updated new values '" + user + "'");
			}
			
		} catch(SQLException sqle) {
			String msg = "Could not update user due to an exception";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}
		
	private String[] getUserFieldValues(Field[] userFields, User user) {
		String retVal[] = null;
		List<String> userFieldValues = new ArrayList<String>();
		for(Field userField : userFields) {
			switch(userField) {
				case PASSWORD:
					userFieldValues.add(wrapForSQL(user.getEncryptedPassword()));
					break;
				case EMAIL:
					userFieldValues.add(wrapForSQL(user.getEmail()));
					break;
			}
		}
		userFieldValues.add(wrapForSQL(user.getUsername()));
		retVal = new String[userFieldValues.size()];
		retVal = userFieldValues.toArray(retVal);
		return retVal;
	}

	/**
	 * Retrieves all {@link User} objects from the database
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return A {@link List} of {@link User} objects
	 */
	public List<User> retreiveAllUsers(Connection conn) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		List<User> users = new ArrayList<User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(Sql.RETREIVE_ALL_USERS);
			while(rs.next()) {
				String userName = rs.getString("username");
				String email = rs.getString("email");
				User newUser = new User(userName, email);
				users.add(newUser);
			}
			return users;
		} catch(SQLException sqle) {
			String msg = "Could not retreive Users from the database";
			throw new DataException(msg, sqle);
		}
		
	}
	
	/**
	 * Retreives the encrypted for the specified username. The password is
	 * encrypted using JASYPT's BasicPasswordEncryptor
	 * @param conn The database connection
	 * @param username The username
	 * @throws NullPointerException If either conn or username is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return The encrypted password
	 */
	public String retreivePassword(Connection conn, String username) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(username == null) {
			throw new NullPointerException("username should not be null");
		}
		String password = null;
		String sqlTemplate = "SELECT password from USER where username=%s";
		String sql = String.format(sqlTemplate, wrapForSQL(username));
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				password = rs.getString("password");
			}
		} catch(SQLException sqle) {
			String msg = "Could not retreive password for user '" + username + "'";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		return password;
	}
	
	/**
	 * Retrieves the {@link User} object for the specified username
	 * @param conn The database connection
	 * @param username The username
	 * @throws NullPointerException Id either conn or username is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return The {@link User} object
	 */
	public User retreiveUserByUsername(Connection conn, String username) 
														throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(username == null) {
			throw new NullPointerException("username should not be null");
		}
		User user = null;
		try {
			Statement stmt = conn.createStatement();
			String query = String.format(Sql.RETREIVE_USER_BY_USERNAME, 
										 wrapForSQL(username));
			ResultSet rs  = stmt.executeQuery(query);
			if(rs.next()) {
				String userName = rs.getString("username");				
				String email = rs.getString("email");
				user = new User(userName, email);
			}
		} catch(SQLException sqle) {
			String msg = "Could not retreive User from database";
			throw new DataException(msg, sqle);
		}
		return user; 
	}
	
	/**
	 * Inserts the specified UserMeta object in the database
	 * @param conn The database connection
	 * @param userMeta The {@link UserMeta} object
	 * @throws NullPointerException If either conn or userMeta is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void insertUserMeta(Connection conn, UserMeta userMeta) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(userMeta == null) {
			throw new NullPointerException("userMeta should not be null");
		}
		String sql = String.format(Sql.INSERT_USER_META, 
								   wrapForSQL(userMeta.getIdentifier()), 								    
								   wrapForSQL(userMeta.getLoginVia().toString()),
								   wrapForSQL(userMeta.getRole().toString()));
		try {
			Statement stmt = conn.createStatement();
			cLogger.info("Executing SQL '" + sql + "'");
			int rows = stmt.executeUpdate(sql);
			System.out.println("rows updated " + rows);
		} catch(SQLException sqle) {
			String msg = "Could not insert UserMeta";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}
	
	/**
	 * Retrieves the {@link UserMeta} object from the database for the specified
	 * userid
	 * @param conn The database connection
	 * @param userid The userid The userid
	 * @throws NullPointerException If conn is null
	 * @throws IllegalArgumentException If userid is less than 0
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public UserMeta retreiveUserMeta(Connection conn, int userid) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(userid < 0) {
			throw new IllegalArgumentException("userid should be a positive integer");
		}
		UserMeta userMeta = null;
		String sql = String.format(Sql.RETREIVE_USER_META, String.valueOf(userid));
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				userMeta = new UserMeta();
				userMeta.setUserid(rs.getInt("userid"));
				userMeta.setIdentifier(rs.getString("identifier"));
				userMeta.setRole(Role.valueOf(rs.getString("role")));
				userMeta.setLoginVia(UserMeta.LoginVia.valueOf(rs.getString("login_via")));
			}
		} catch(SQLException sqle) {
			String msg = "Could not retreive UserMeta for userid '" + userid + "'";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		return userMeta;
	}
	
	/**
	 * Retrieves the {@link UserMeta} object identified by identifer and loginVia
	 * from the database
	 * @param conn The database connection
	 * @param identifier The UserMeta's identifier
	 * @param loginVia The {@link UserMeta.LoginVia} value for this user
	 * @throws NullPointerException If either conn or identifier or loginVia
	 * are null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return The {@link UserMeta} object
	 */
	public UserMeta retreiveUserMetaByIdentifierLoginVia(Connection conn,
													 	 String identifier, 
													 	 UserMeta.LoginVia loginVia) 
														throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(identifier == null) {
			throw new NullPointerException("identifier should not be null");
		}
		if(loginVia == null) {
			throw new NullPointerException("loginVia should not be null");
		}
		
		UserMeta userMeta = null;
		try {
			Statement stmt = conn.createStatement();
			String query = String.format(Sql.RETREIVE_USER_META_BY_IDETIFIER_LOGIN_VIA, 
										 wrapForSQL(identifier),
										 wrapForSQL(loginVia.toString()));
			cLogger.info("Executing SQL '" + query + "'");
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()) {
				int userid = rs.getInt("userid");
				String userMetaIdentifer = rs.getString("identifier");
				String userMetaLoginVia = rs.getString("login_via");
				userMeta = new UserMeta();
				userMeta.setUserid(userid);
				userMeta.setIdentifier(userMetaIdentifer);
				userMeta.setLoginVia(UserMeta.LoginVia.valueOf(userMetaLoginVia));
				userMeta.setRole(Role.valueOf(rs.getString("role")));
			}
		} catch(SQLException sqle) {
			String msg = "Could not retreive UserMeta for '" + identifier + "' '" + loginVia + "'";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		return userMeta;
	}
	
	/**
	 * Retrieves all {@link UserMeta} objects from the database
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return A {@link List} of {@link UserMeta} objects. If the database does
	 * not have any {@link UserMeta} objects then the list will be an empty list
	 */
	public List<UserMeta> retreiveAllUserMeta(Connection conn) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		List<UserMeta> allUserMeta = new ArrayList<UserMeta>();
		
		try {
			Statement stmt = conn.createStatement();
			cLogger.info("Executing Sql '" + Sql.RETREIVE_ALL_USER_META + "'");
			ResultSet rs = stmt.executeQuery(Sql.RETREIVE_ALL_USER_META);
			while(rs.next()) {
				UserMeta userMeta = new UserMeta();
				userMeta.setUserid(rs.getInt("userid"));
				userMeta.setIdentifier(rs.getString("identifier"));
				userMeta.setRole(Role.valueOf(rs.getString("role")));
				userMeta.setLoginVia(UserMeta.LoginVia.valueOf(rs.getString("login_via")));
				allUserMeta.add(userMeta);
			}
		} catch(SQLException sqle) {
			String msg = "Could not fetch all UserMeta objects";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		return allUserMeta;
	}
	
	/**
	 * Updates the {@link Role} of the specified {@link UserMeta} object
	 * @param conn The database connection
	 * @param userMeta The updated {@link UserMeta} object with the new {@link Role}
	 * @throws NullPointerException If either conn or userMeta is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return A {@link List} of {@link UserMeta} objects. If the database does
	 * not have any {@link UserMeta} objects then the list will be an empty list
	 */
	public void updateUserMetaRole(Connection conn, UserMeta userMeta) throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(userMeta == null) {
			throw new NullPointerException("userMeta should not be null");
		}
		String sql = String.format(Sql.UPDATE_USER_META_ROLE, 
								   wrapForSQL(userMeta.getRole().toString()),
								   userMeta.getUserid());
		try {
			Statement stmt = conn.createStatement();
			cLogger.info("Executing SQL '" + sql + "'");
			stmt.executeUpdate(sql);
		} catch(SQLException sqle) {
			String msg = "Could not update userMeta '" + userMeta + "'";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}
	
	/**
	 * Retrieves the static page for the specified id
	 * @param c The database connection
	 * @param id The id of the static page
	 * @throws NullPointerException If either c or id is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public StaticPagePOJO retreiveStaticPage(Connection c, String id) throws DataException {
		if(c == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(id == null) {
			throw new NullPointerException("id should not be null");
		}
		StaticPagePOJO page = null;
		String sql = String.format(Sql.RETREIVE_STATIC_PAGE, wrapForSQL(id));
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				String pageContents = rs.getString("contents");
				page = new StaticPagePOJO(id, pageContents);
			}
			return page;
		} catch(SQLException sqle) {
			String msg = "Could not retreive static page " + id;
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}
	
	/**
	 * Insert or update the static page.
	 * @param conn The database connection
	 * @param page The {@link StaticPagePOJO} to insert or update
	 * @throws NullPointerException If either conn or page is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void upsertStaticPage(Connection conn, 
								 StaticPagePOJO page) 
														throws DataException {
		if(conn == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(page == null) {
			throw new NullPointerException("page should not be null");
		}
		StaticPagePOJO preexistingPage = retreiveStaticPage(conn, page.getId());
		if(preexistingPage == null) {
			insertStaticPage(conn, page);
		}
		else {
			updateStaticPage(conn, page);
		}
	}
	
	/**
	 * Returns the value from KVTABLE for the specified key
	 * KVTABLE is a simple key value table where the key and value are of type
	 * VARCHAR. The key is a 64 char key while the value is a 128 char value.
	 * @param c The database connection
	 * @param k The key
	 * @throws NullPointerException If either c or k is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return The value for the specified key
	 */
	public String retreiveFromKvTable(Connection c, String k) throws DataException {
		if(true) {
			throw new RuntimeException("Method not implemented");
		}
		return null;
	}
	
	/**
	 * Upsert (update or insert) the specifed key and value in KVTABLE
	 * @param c The database connection
	 * @param k The key
	 * @param v The value
	 * @throws NullPointerException If either c, or k, or v is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void upsertKvTable(Connection c, String k, String v) throws DataException {
		throw new RuntimeException("Method not implemented");
	}
	
	/**
	 * Returns the value from KVTABLE_CLOB for the specified key
	 * KVTABLE_CLOB is a simple key value table where the key is a 64 char key 
	 * and the value is an arbitrarily long character sequence
	 * @param c The database connection
	 * @param k The key
	 * @throws NullPointerException If either c or k is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return The value for the specified key
	 */
	public String retreiveFromKvTableClob(Connection c, String k) 
														throws DataException {
		if(c == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(k == null) {
			throw new NullPointerException("k should not be null");
		}
		String retVal = null;
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = 
				stmt.executeQuery(String.format(Sql.RETREIVE_KVTABLE_CLOB,wrapForSQL(k)));
			if(rs.next()) {
				retVal = rs.getString("v");
			}
			return retVal;
		} catch(SQLException sqle) {
			String msg = "Could not retreive value from KVTABLE_CLOB";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}
	
	/**
	 * Upsert (update or insert) the specifed key and value in KVTABLE
	 * @param c The database connection
	 * @param k The key
	 * @param v The value
	 * @throws NullPointerException If either c, or k, or v is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void upsertKvTableClob(Connection c, String k, String v) throws DataException {
		if(c == null) {
			throw new NullPointerException(NULL_CONN_ERROR_MSG);
		}
		if(k == null) {
			throw new NullPointerException("k should not be null");
		}
		if(v == null) {
			throw new NullPointerException("v should not be null");
		}
		try {
			String existingVal = retreiveFromKvTableClob(c, k);
			Statement stmt = c.createStatement();
			String sql = null;
			if(existingVal == null) {
				sql = String.format(Sql.INSERT_KVTABLE_CLOB, wrapForSQL(k), wrapForSQL(v));
			}
			else {
				sql = String.format(Sql.UPDATE_KVTABLE_CLOB, wrapForSQL(v), wrapForSQL(k));
			}
			stmt.executeUpdate(sql);
		} catch(DataException de) {
			String msg = "Could not check if the key already exists before upserting the value in KVTABLE_CLOB ('" + k + "','" + v + "')";
			cLogger.error(msg, de);
			throw new DataException(msg, de);
		} catch(SQLException sqle) {
			String msg = "Could not upsert into KVTABLE_CLOB ('" + k + "','" + v + "')";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}

	private void insertStaticPage(Connection conn, StaticPagePOJO page) throws DataException {
		String sql = String.format(Sql.INSERT_STATIC_PAGE, 
								   wrapForSQL(page.getId()), 
								   wrapForSQL(page.getContents()));
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql); 
		} catch(SQLException sqle) {
			String msg = "Could not insert page " + page;
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}
	
	private void updateStaticPage(Connection conn, StaticPagePOJO page) throws DataException {
		String sql = String.format(Sql.UPDATE_STATIC_PAGE, 
								   wrapForSQL(page.getContents()), 
								   wrapForSQL(page.getId()));
		
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch(SQLException sqle) {
			String msg = "Could not update page " + page;
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
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
		String sqlToGetMentorIdsForCourse = Sql.RETREIVE_MENTORS_FOR_COURSE;
		for(Course course : courses) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(sqlToGetMentorIdsForCourse, wrapForSQL(course.getId())));
			
			List<Mentor> mentors = new ArrayList<Mentor>();
			while(rs.next()) {
				int mentorUserid = rs.getInt("mentor_userid");
				Statement stmt1 = conn.createStatement();
				ResultSet mentorsResultSet = stmt1.executeQuery(String.format(Sql.RETREIVE_USER_META, mentorUserid));
				while(mentorsResultSet.next()) {
					String role = mentorsResultSet.getString("role");
					Mentor mentor = new Mentor();
					mentor.setUserid(mentorUserid);
					mentor.setIdentifier(mentorsResultSet.getString("identifier"));
					mentor.setLoginVia(UserMeta.LoginVia.valueOf(mentorsResultSet.getString("login_via")));
					mentor.setRole(Role.valueOf(role));
					if(mentor.getRole().equals(Role.MENTOR)) {
						mentors.add(mentor);
					}
					else {
						cLogger.warn("Found a user in COURSE_MENTOR table who is not a mentor " + mentorUserid);
					}
				}
			}
			
			if(mentors.size() == 0) {
				cLogger.warn("Mentors were not found for course : " + course.getId());				
			}
			else if(mentors.size() == 1){
				course.setMentor(mentors.get(0));
			}
			else if(mentors.size() > 1) {
				course.setMentor(mentors.get(0));
				cLogger.warn("Found multiple mentors for course " + course.getId());
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

	private List<CourseEnrollmentStatus> getAllCourseEnrollmentStatuses(Connection conn) throws SQLException {
		//TODO: Limit this query to 5 rows
		String sql = "SELECT * FROM COURSE_ENROLLMENT_ACTIONS;";
		List<CourseEnrollmentStatus> statuses = new ArrayList<CourseEnrollmentStatus>();
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			String courseId = rs.getString("course_id");
			int userid = rs.getInt("userid");
			int userCourseStatusId = rs.getInt("course_enrollment_action_id");
			Timestamp tstamp = rs.getTimestamp("tstamp");
			statuses.add(new CourseEnrollmentStatus(courseId, userid, UserCourseStatus.getUserCourseStatus(userCourseStatusId), tstamp));
		}	
		 
		return statuses;
	}
	
	public static String wrapForSQL(String s) {
		if(s == null) {
			s = "";
		}
		String escapedStr = s.replaceAll("'", "''");
		return "'" + escapedStr + "'";
	}
	
	private String getEnrollmentStatusWithSurroundingText(
			UserCourseStatus userCourseStatus) {
		String retVal = "";
		switch(userCourseStatus) {
			case COMPLETED:
				retVal = UserCourseStatus.COMPLETED.toString();
				break;
			case DROPPED:
				retVal = UserCourseStatus.DROPPED.toString();
				break;
			case ENROLLED:
				retVal = UserCourseStatus.ENROLLED.toString() + " in ";
				break;
			default:
				retVal = userCourseStatus.toString();
		}
		return retVal;
	}	

}
