package org.wwald.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
		//TODO: Remove hardcoded date
		Timestamp timestamp = new Timestamp((new Date()).getTime());
		String sql = String.format(Sql.INSERT_COURSE_ENROLLMENT_STATUS,
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

	public String retreiveCompetenciesWiki(Connection conn, String courseId) throws DataException {
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
			String msg = "Could not get contents of CompetenciesWiki table";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
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

	public void updateCompetenciesWikiContents(Connection conn, String courseId, String contents) throws DataException {
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
	
	public void updateCompetency(Connection conn, String courseId, Competency competency) throws DataException {
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
	
	public void deleteCompetency(Connection conn, Competency competency) throws DataException {
		throw new RuntimeException("method not implemented");
	}

	public void deleteMentor(Connection conn, Mentor mentor) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}

	public void insertMentor(Connection conn, Mentor mentor) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}

	public List<Mentor> retreiveMentorsForCompetency(Connection conn) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	public List<Competency> retreiveAllCompetencies(Connection conn) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	public List<Mentor> retreiveAllMentors(Connection conn) throws DataException {
		List<Mentor> mentors = new ArrayList<Mentor>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(Sql.RETREIVE_ALL_MENTORS);
			while(rs.next()) {
				Mentor mentor = new Mentor();
				mentor.setUsername(rs.getString("username"));
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

	public List<Competency> retreiveCompetenciesForCourse(Connection conn, Course course) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	public List<Mentor> retreiveMentorsForCourse(Connection conn) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
		return null;
	}

	public void updateCourse(Connection conn, Course course) throws DataException {
		String sql = String.format(Sql.UPDATE_COURSE, 
								   wrapForSQL(course.getTitle()), 
								   wrapForSQL(course.getDescription()), 
								   wrapForSQL(course.getId()));
		try {
			cLogger.info("Executing SQL '''" + sql + "'''");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			
			//TODO: Good Samaritan is the default mentor for every course... if the mentor was removed then add this one
			sql = String.format(Sql.UPDATE_COURSE_MENTORS, wrapForSQL(course.getMentor().getUsername()), wrapForSQL(course.getId()));
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch(SQLException sqle) {
			String msg = "Could not update course " + course.getId();
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}

	public void updateMentor(Connection conn, Mentor mentor) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}

	public void upsertCompetency(Connection conn, Competency competency) throws DataException {
		throw new RuntimeException("method not implemented");
	}

	public void upsertCourse(Connection conn, Course course) throws DataException {
		throw new RuntimeException("method not implemented");
	}

	public void upsertMentor(Connection conn, Mentor mentor) throws DataException {
		if(true) {
			throw new RuntimeException("method not implemented");
		}
	}
	
	public List<StatusUpdate> getStatusUpdates(Connection conn) throws DataException {
		try {
			List<CourseEnrollmentStatus> courseEnrollmentStatuses = getAllCourseEnrollmentStatuses(conn);
			List<StatusUpdate> statusUpdates = new ArrayList<StatusUpdate>();
//			DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
			for(CourseEnrollmentStatus courseEnrollmentStatus : courseEnrollmentStatuses) {			
				statusUpdates.add(new StatusUpdate(courseEnrollmentStatus.getTimestamp() + 
												   " - " +
												   courseEnrollmentStatus.getUsername() + " " + 
												   getEnrollmentStatusWithSurroundingText(courseEnrollmentStatus.getUserCourseStatus()) + 
												   " course " + 
												   courseEnrollmentStatus.getCourseId()));
			}    	
	    	return statusUpdates;
		} catch(SQLException sqle) {
			String msg = "Could not get status updates due to an exception";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}

	public void insertUser(Connection conn, User user, UserMeta userMeta) throws DataException {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Statement stmt = conn.createStatement();
			String sql = String.format(Sql.INSERT_USER, 
									   wrapForSQL(user.getUsername()),
									   wrapForSQL(user.getEncryptedPassword()),
									   wrapForSQL(user.getEmail()),
									   wrapForSQL(user.getRole().toString()),
									   wrapForSQL(userMeta.getIdentifier()),
									   wrapForSQL(userMeta.getLoginVia().toString()));
			cLogger.info("Executing SQL '" + sql + "'");
			int rowsUpdated = stmt.executeUpdate(sql);			
		} catch(SQLException sqle) {
			String msg = "Could not insert User due to an exception";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
	}
	
	public void updateUser(Connection conn, User user, UserForm.Field... userFields) throws DataException {
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
						   wrapForSQL(user.getRole().toString()),
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
				case ROLE:
					userFieldValues.add(wrapForSQL(user.getRole().toString()));
					break;
			}
		}
		userFieldValues.add(wrapForSQL(user.getUsername()));
		retVal = new String[userFieldValues.size()];
		retVal = userFieldValues.toArray(retVal);
		return retVal;
	}

	public List<User> retreiveAllUsers(Connection conn) throws DataException {
		List<User> users = new ArrayList<User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(Sql.RETREIVE_ALL_USERS);
			while(rs.next()) {
				String userName = rs.getString("username");
				String email = rs.getString("email");
				String role = rs.getString("role");
				User newUser = new User(userName,Role.valueOf(role));
				newUser.setEmail(email);
				users.add(newUser);
			}
			return users;
		} catch(SQLException sqle) {
			String msg = "Could not retreive Users from the database";
			throw new DataException(msg, sqle);
		}
		
	}
	
	public String retreivePassword(Connection conn, String username) throws DataException {
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
	
	public User retreiveUserByUsername(Connection conn, String username) throws DataException {
		User user = null;
		try {
			Statement stmt = conn.createStatement();
			String query = String.format(Sql.RETREIVE_USER_BY_USERNAME, wrapForSQL(username));
			ResultSet rs  = stmt.executeQuery(query);
			if(rs.next()) {
				String userName = rs.getString("username");				
				String email = rs.getString("email");
				String role = rs.getString("role");
				user = new User(userName,Role.valueOf(role));
				user.setEmail(email);
			}
		} catch(SQLException sqle) {
			String msg = "Could not retreive User from database";
			throw new DataException(msg, sqle);
		}
		return user; 
	}
	
	public UserMeta retreiveUserMetaByIdentifierLoginVia(Connection conn,
													 String identifer, 
													 UserMeta.LoginVia loginVia) 
			throws DataException {
		UserMeta userMeta = null;
		try {
			Statement stmt = conn.createStatement();
			String query = String.format(Sql.RETREIVE_USER_META_BY_IDETIFIER_LOGIN_VIA, 
										 wrapForSQL(identifer), 
										 wrapForSQL(loginVia.toString()));
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()) {
				int userid = rs.getInt("userid");
				String userMetaIdentifer = rs.getString("identifier");
				String userMetaLoginVia = rs.getString("login_via");
				userMeta = new UserMeta();
				userMeta.setUserid(userid);
				userMeta.setIdentifier(userMetaIdentifer);
				userMeta.setLoginVia(UserMeta.LoginVia.valueOf(userMetaLoginVia));
			}
		} catch(SQLException sqle) {
			String msg = "Could not retreive UserMeta for '" + identifer + "' '" + loginVia + "'";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		return userMeta;
	}
	
	public StaticPagePOJO retreiveStaticPage(Connection c, String id) throws DataException {
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
	
	public void upsertStaticPage(Connection conn, StaticPagePOJO page) throws DataException {
		StaticPagePOJO preexistingPage = retreiveStaticPage(conn, page.getId());
		if(preexistingPage == null) {
			insertStaticPage(conn, page);
		}
		else {
			updateStaticPage(conn, page);
		}
	}
	
	public String retreiveFromKvTable(Connection c, String k) throws DataException {
		return null;
	}
	
	public void upsertKvTable(Connection c, String k, String v) throws DataException {
		
	}
	
	public String retreiveFromKvTableClob(Connection c, String k) throws DataException {
		String retVal = null;
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(Sql.RETREIVE_KVTABLE_CLOB,wrapForSQL(k)));
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
	
	public void upsertKvTableClob(Connection c, String k, String v) throws DataException {
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
		String sqlToGetMentorIdsForCourse = "SELECT (mentor_username) FROM COURSE_MENTORS WHERE course_id=%s";
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
			String mentorUsername = rs.getString("mentor_username");
			Statement stmt = conn.createStatement();
			ResultSet mentorsResultSet = stmt.executeQuery(String.format(Sql.RETREIVE_USER_BY_USERNAME, wrapForSQL(mentorUsername)));
			while(mentorsResultSet.next()) {
				String email = mentorsResultSet.getString("email");
				String role = mentorsResultSet.getString("role");
				Mentor mentor = new Mentor();
				mentor.setUsername(mentorUsername);
				mentor.setEmail(email);
				mentor.setRole(Role.valueOf(role));
				if(mentor.getRole().equals(Role.MENTOR)) {
					mentors.add(mentor);
				}
				else {
					cLogger.warn("Found a user in COURSE_MENTOR table who is not a mentor " + mentorUsername);
				}
			}
		}
		return mentors;
	}
	
	private List<CourseEnrollmentStatus> getAllCourseEnrollmentStatuses(Connection conn) throws SQLException {
		//TODO: Limit this query to 5 rows
		String sql = "SELECT * FROM COURSE_ENROLLMENT_ACTIONS;";
		List<CourseEnrollmentStatus> statuses = new ArrayList<CourseEnrollmentStatus>();
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			String courseId = rs.getString("course_id");
			String username = rs.getString("username");
			int userCourseStatusId = rs.getInt("course_enrollment_action_id");
			Timestamp tstamp = rs.getTimestamp("tstamp");
			statuses.add(new CourseEnrollmentStatus(courseId, username, UserCourseStatus.getUserCourseStatus(userCourseStatusId), tstamp));
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
