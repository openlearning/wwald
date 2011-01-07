package org.wwald.service;

import java.sql.Connection;
import java.util.List;

import org.wwald.model.Answer;
import org.wwald.model.Competency;
import org.wwald.model.Course;
import org.wwald.model.CourseEnrollmentStatus;
import org.wwald.model.Forum;
import org.wwald.model.Mentor;
import org.wwald.model.Question;
import org.wwald.model.StaticPagePOJO;
import org.wwald.model.StatusUpdate;
import org.wwald.model.User;
import org.wwald.model.UserMeta;
import org.wwald.view.UserForm;

public interface IDataFacade {
	
	//COURSE 
	/**
	 * Retrieves the list of courses
	 * @param conn The database connection
	 * @throws NullPointerException if conn is null
	 * @throws DataException if the JDBC code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 * @return The list of courses
	 */
	public List<Course> retreiveCourses(Connection c) throws DataException ;
	
	
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
	public Course retreiveCourse(Connection c, 
								 String courseId) 
		throws DataException;
	
	/**
	 * Retreives the courses wiki which contains all courses which are to be
	 * displayed on the main page
	 * @param conn The database connection
	 * @throws NullPointerException if conn is null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 * @return The courses wiki
	 */
	public String retreiveCourseWiki(Connection c) throws DataException;
	
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
	public void updateCourse(Connection c, 
							 Course course) 
		throws DataException;
	
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
	public void insertCourse(Connection c, 
							 Course course) 
		throws DataException;
	
	/**
	 * Updates or inserts the specified {@link Course}
	 * @param conn The database connection
	 * @param course The {@link Course} object
	 * @throws NullPointerException If either conn or course are null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException} 
	 */
	public void upsertCourse(Connection c, 
							 Course course) 
		throws DataException;
	
	/**
	 * Updates the courses wiki
	 * @param conn The database connection
	 * @param wikiContents The wiki contents
	 * @throws NullPointerException if conn, or wikiContents are null
	 * @throws DataException If the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public void updateCourseWiki(Connection c, 
								 String wikiContents) 
		throws DataException;
	
	
	/**
	 * Adds the specified course as a course enrolled for the specified user
	 * @param conn The database connection
	 * @param userMeta The user
	 * @param course The course
	 * @throws NullPointerException If either conn, userMeta, or course are null 
	 * @throws DataException If the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public void insertCourseEnrollment(Connection conn,
									   UserMeta userMeta, 
									   Course course)
	    throws DataException;
	
	
	/**
	 * Deletes the enrollment of the specified {@link UserMeta} from the 
	 * specified {@link Course}. If the user is not enrolled for the course
	 * then no action will be taken. 
	 * @param conn The database connection
	 * @param userMeta The user
	 * @param course The course
	 * @throws NullPointerException If either conn, userMeta, or course are null 
	 * @throws DataException If the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public void deleteCourseEnrollment(Connection conn, 
									   UserMeta userMeta, 
									   Course course) 
		throws DataException;
	
	
	/**
	 * Retrieve all the courses where the specified {@link UserMeta} is enrolled. 
	 * @param conn The database connection
	 * @param userMeta The user
	 * @return A {@link List} containing all the courseId's in which the
	 * 		   specified user has enrolled in
	 * @throws NullPointerException If either conn or userMeta is null
	 * @throws DataException If the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public List<String> retreiveCourseEnrollmentsForUser(Connection conn, 
														UserMeta userMeta)
		throws DataException;
	
	
	/**
	 * Verified if the specified user is enrolled in the specified course
	 * @param conn The database connection 
	 * @param userMeta The specified user
	 * @param course The specified course
	 * @return true if the user is enrolled for the specified course, false 
	 * otherwise
	 * @throws NullPointerException If either conn, userMeta, or course are null
	 * @throws DataException If the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public boolean checkEnrollmentByUserMetaAndCourse(Connection conn, 
													  UserMeta userMeta, 
													  Course course) 
		throws DataException;
	
	
	/**
	 * Adds a course enrollment action in the database. A course enrollment
	 * action specified an action performed by a user for either enrolling in
	 * or dropping out of of a course.
	 * @param conn The database connection
	 * @param courseEnrollmentStatus The CourseEnrollmentStatus object which is
	 * to be used for inserting the action in persistent storage
	 */
	public void addCourseEnrollmentAction(Connection c, 
										  CourseEnrollmentStatus courseEnrollmentStatus) 
		throws DataException;
	
	//Competencies
	/**
	 * Update the competencies wiki for the specified course
	 * @param conn The database connection
	 * @param courseId The course id
	 * @param contents The new contents of the course competency wiki. If these
	 * are null then they will be considered to be an empty String ""
	 * @throws NullPointerException If either conn, or courseId are null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public void updateCompetenciesWikiContents(Connection c, 
											   String courseId, 
											   String contents) 
		throws DataException;	
	
	/**
	 * Retrieves all the {@link Competency} objects from the database
	 * @param conn The database connection
	 * @throws NullPointerException If conn null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public List<Competency> retreiveAllCompetencies(Connection c) throws DataException;
	
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
	public List<Competency> retreiveCompetenciesForCourse(Connection c, 
														  Course course) 
	    throws DataException;
	
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
	public Competency retreiveCompetency(Connection c, 
										 String courseId, 
										 String competencyId) 
		throws DataException;
	
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
	public String retreiveCompetenciesWiki(Connection c, 
										   String courseId) 
		throws DataException;
	
	/**
	 * Updates {@link Competency} with the specified {@link Competency} object.
	 * @param conn The database connection object
	 * @param courseId The courseId of the {@link Course} for which we want to 
	 * update the {@link Competency}
	 * @param competency The updated {@link Competency} object
	 * @throws NullPointerException If either conn, courseId, or competency are
	 * null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public void updateCompetency(Connection c, 
								 String courseId, 
								 Competency competency) 
		throws DataException;
	
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
	public Competency insertCompetency(Connection c, 
									   Course course, 
									   String title) 
		throws DataException;
	
	/**
	 * Updates or inserts the specified {@link Competency}
	 * @param conn The database connection
	 * @param competency The {@link Competency} object
	 * @throws NullPointerException If either conn or competency are null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException} 
	 */
	public void upsertCompetency(Connection c, 
								 Competency competency) 
		throws DataException;
	
	/**
	 * Deletes the specified {@link Competency} from the database
	 * @param conn The database {@link Connection}
	 * @param competency The {@link Competency} to delete
	 * @throws NullPointerException If either conn or competency is null
	 * @throws DataException if the jdbc code throws a SqlException. The 
	 * SQLException is wrapped in the DataException
	 */
	public void deleteCompetency(Connection c, 
								 Competency competency) 
		throws DataException;
	
	//Mentors
	/**
	 * Retrieves a list containing all the mentors in the system
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException} 
	 */
	public List<Mentor> retreiveAllMentors(Connection c) throws DataException;
	
	/**
	 * Retrieves a {@link List} of {@link Mentor}s for the specified {@link Course}
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return The List of {@link Mentor}s for the specified {@link Course}
	 */
	public List<Mentor> retreiveMentorsForCourse(Connection c) throws DataException;
	
	/**
	 * Retreives {@link Mentor}s for the specified {@link Competency}
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public List<Mentor> retreiveMentorsForCompetency(Connection c) throws DataException;
	
	/**
	 * Updates a {@link Mentor}
	 * @param conn The database connection
	 * @param mentor The {@link Mentor} object with updated fields
	 * @throws NullPointerException If either conn or mentor are null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException} 
	 */
	public void updateMentor(Connection c, 
							 Mentor mentor) 
		throws DataException;
	
	/**
	 * Inserts the specified {@link Mentor} in the database
	 * @param conn The database connection
	 * @param mentor The {@link Mentor} to delete
	 * @throws NullPointerException If either conn or mentor is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void insertMentor(Connection c, 
							 Mentor mentor) 
		throws DataException;
	
	/**
	 * Updates or inserts the specified {@link Mentor}
	 * @param conn The database connection
	 * @param mentor The {@link Mentor} object
	 * @throws NullPointerException If either conn or mentor are null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException} 
	 */
	public void upsertMentor(Connection c, 
							 Mentor mentor) 
		throws DataException;
	
	/**
	 * Deletes the specified {@link Mentor} from the database
	 * @param conn The database connection
	 * @param mentor The {@link Mentor} to delete
	 * @throws NullPointerException If either conn or mentor is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * SQLException is wrapped in the {@link DataException}
	 */
	public void deleteMentor(Connection c, 
							 Mentor mentor) 
		throws DataException;
	
	//Status updates
	/**
	 * Returns a list of {@link StatusUpdate} objects
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return A {@link List} of {@link StatusUpdate} objects
	 */
	public List<StatusUpdate> getStatusUpdates(Connection c) throws DataException;
	
	//User
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
		throws DataException;
	
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
						   UserForm.Field... userFields) 
		throws DataException;
	
	/**
	 * Retrieves all {@link User} objects from the database
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return A {@link List} of {@link User} objects
	 */
	public List<User> retreiveAllUsers(Connection c) throws DataException ;
	
	/**
	 * Retrieves the {@link User} object for the specified username
	 * @param conn The database connection
	 * @param username The username
	 * @throws NullPointerException Id either conn or username is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return The {@link User} object
	 */
	public User retreiveUserByUsername(Connection conn, 
									   String username) 
		throws DataException;
	
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
	public String retreivePassword(Connection conn, 
								   String username) 
		throws DataException;
	
	//UserMeta
	/**
	 * Inserts the specified UserMeta object in the database
	 * @param conn The database connection
	 * @param userMeta The {@link UserMeta} object
	 * @throws NullPointerException If either conn or userMeta is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void insertUserMeta(Connection conn, 
							   UserMeta userMeta) 
		throws DataException;
	
	/**
	 * Retrieves all {@link UserMeta} objects from the database
	 * @param conn The database connection
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @return A {@link List} of {@link UserMeta} objects. If the database does
	 * not have any {@link UserMeta} objects then the list will be an empty list
	 */
	public List<UserMeta> retreiveAllUserMeta(Connection conn) throws DataException;
	
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
	public UserMeta retreiveUserMeta(Connection conn, 
									 int userid) 
		throws DataException;
	
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
														 String identifer, 
														 UserMeta.LoginVia loginVia) 
		throws DataException;
	
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
	public void updateUserMetaRole(Connection conn, UserMeta userMeta) throws DataException;
	
	//Static Pages
	/**
	 * Retrieves the static page for the specified id
	 * @param c The database connection
	 * @param id The id of the static page
	 * @throws NullPointerException If either c or id is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public StaticPagePOJO retreiveStaticPage(Connection c, 
											 String id) 
		throws DataException;
	
	/**
	 * Insert or update the static page.
	 * @param conn The database connection
	 * @param page The {@link StaticPagePOJO} to insert or update
	 * @throws NullPointerException If either conn or page is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void upsertStaticPage(Connection c, 
								 StaticPagePOJO page) 
		throws DataException;
	
	//KVTable
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
	public String retreiveFromKvTable(Connection c, 
									  String k) 
		throws DataException;
	
	/**
	 * Upsert (update or insert) the specifed key and value in KVTABLE
	 * @param c The database connection
	 * @param k The key
	 * @param v The value
	 * @throws NullPointerException If either c, or k, or v is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void upsertKvTable(Connection c, 
							  String k, 
							  String v) 
		throws DataException;
	
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
	public String retreiveFromKvTableClob(Connection c, 
										  String k) 
		throws DataException;
	
	/**
	 * Upsert (update or insert) the specifed key and value in KVTABLE
	 * @param c The database connection
	 * @param k The key
	 * @param v The value
	 * @throws NullPointerException If either c, or k, or v is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void upsertKvTableClob(Connection c, 
								  String k, 
								  String v) 
		throws DataException;
	
	/**
	 * Returns a {@link List} of all discussion forums represented by
	 * {@link Forum} objects.
	 * @param conn The database connection
	 * @return A {@link List} of {@link Forum} objects
	 * @throws NullPointerException If conn is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public List<Forum> retreiveAllDiscussionForums(Connection conn) 
		throws DataException;
	
	/**
	 * Retreives the {@link Forum} object for the specified forumId
	 * @param conn
	 * @param forumId
	 * @return
	 * @throws DataException
	 */
	public Forum retreiveDiscussionForum(Connection conn, String forumId) 
		throws DataException;
	
	/**
	 * Inserts the specified {@link Forum} in peristent storage
	 * @param conn The database connection
	 * @param forum The {@link Forum} to insert
	 * @throws NullPointerException If either conn or forum is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void insertDiscussionForum(Connection conn, Forum forum)
		throws DataException;
	
	/**
	 * Deletes a discussion forum. A discussion forum can only be deleted if
	 * it contains no questions. If an attempt is made to delete a forum with
	 * questions, an Exception will be thrown
	 * @param conn The database connection
	 * @param forum The {@link Forum}
	 * @throws NullPointerException If either conn or forum is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 * @throws CannotPerformActionException If the discussion forum contains
	 * questions
	 */
	public void deleteDiscussionForum(Connection conn, Forum forum) 
		throws DataException, CannotPerformActionException;
	
	/**
	 * Inserts a {@link Question} into the specified {@link Forum}
	 * @param conn The database connection
	 * @param question The question to insert
	 * @param The forum in which the question must be inserted
	 * @throws NullPointerException If either conn, or question, or forum is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void insertQuestion(Connection conn, Question question) 
		throws DataException;
	
	/**
	 * Returns a {@link List} of all {@link Question} obejcts from the specified
	 * {@link  Forum}
	 * @param conn The database connection
	 * @param forum The forum from which we want to retreive the questions
	 * @return a {@link List} of {@link Question}s asked in the specified forum
	 * @throws NullPointerException If either conn or forum is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public List<Question> retreiveAllQuestionsForForum(Connection conn, 
													   Forum forum) 
		throws DataException;
	
	/**
	 * Retreives the question specified by forumId and questionId
	 * @param conn
	 * @param forumId
	 * @param questionId
	 * @return
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public Question retreiveQuestion(Connection conn, 
									 String forumId, 
									 int questionId) 
		throws DataException;

	/**
	 * Inserts the specified {@link Answer} in the database
	 * @param conn The database connection
	 * @param answer The {@link Answer}
	 * @throws NullPointerException If either conn or answer is null
	 * @throws DataException if the jdbc code throws a {@link SqlException}. The 
	 * {@link SQLException} is wrapped in the {@link DataException}
	 */
	public void insertAnswer(Connection conn, Answer answer) 
		throws DataException;
}
