package org.wwald.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.jasypt.util.password.PasswordEncryptor;
import org.wwald.WWALDSession;
import org.wwald.model.Answer;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Course;
import org.wwald.model.CourseEnrollmentStatus;
import org.wwald.model.Question;
import org.wwald.model.User;
import org.wwald.model.UserCourseStatus;
import org.wwald.model.UserMeta;
import org.wwald.view.ForumPage;
import org.wwald.view.GenericErrorPage;

public class ApplicationFacade {
	
	private IDataFacade dataFacade;
	
	private static Logger cLogger = Logger.getLogger(ApplicationFacade.class);
	
	public ApplicationFacade(IDataFacade dataFacade) {
		this.dataFacade = dataFacade;
	}
	
	public UserMeta login(String username, 
						  String password, 
						  Connection conn,
						  PasswordEncryptor passwordEncryptor) 
		throws ApplicationException {
		
		User user = null;
		UserMeta userMeta = null;
		try {
			cLogger.info("Trying to login user '" + username + "'");
			
			String passwordInDb = 
				dataFacade.retreivePassword(conn, username);
			
			if(passwordInDb != null && 
					passwordEncryptor.checkPassword(password, passwordInDb)) {
				user = 
					dataFacade.retreiveUserByUsername(conn, username);
				if(user == null) {
					cLogger.error("THIS SHOULD NEVER HAPPEN. COULD GET PASSWORD " +
								  "BUT COULD NOT GET USER OBJECT");
				}
				cLogger.info("User " + username + " logged in succesully");
				userMeta = 
					dataFacade.
						retreiveUserMetaByIdentifierLoginVia(conn, 
														 	 user.getUsername(), 
														 	 UserMeta.LoginVia.INTERNAL);
			}
			else {
				cLogger.info("User " + username + " NOT logged in");
			}
		} catch(DataException de) {
			String msg = "Could not not login user " + username;
			cLogger.error(msg, de);
			throw new ApplicationException(msg, de);
		}
		return userMeta; 
	}
	
	public void enrollInCourse(UserMeta user, 
							   Course course, 
							   Connection conn) 
		throws ApplicationException {
		
		try {
			UserCourseStatus userCourseStatus = 
				getUserCourseStatus(user, course, conn);
			if(!userCourseStatus.equals(UserCourseStatus.ENROLLED)) {
				//update the UserCourseStatus
				CourseEnrollmentStatus courseEnrollmentStatus = 
					new CourseEnrollmentStatus(course.getId(), 
											   user.getUserid(), 
											   UserCourseStatus.ENROLLED,
											   new Timestamp((new Date()).getTime()));
				this.
					dataFacade.
						addCourseEnrollmentAction(conn, courseEnrollmentStatus);
				
				//TODO: Audit point because step 1 of a 2 step action is done
				String msg1 = "'" + user.getUserid() + 
							  "' course '" + course.getId() + 
							  "' - Added CourseEnrollmentAction";
				String auditMsg1 = getAuditMsg(msg1, "EnrollInCourse", 1);
				cLogger.info(auditMsg1);
				
				//add entry in COURSE_ENROLLMENTS table
				this.dataFacade.
					insertCourseEnrollment(conn, user, course);
				
				//TODO: Audit point
				String msg2 = "'" + user.getUserid() + 
							  "' course '" + course.getId() + 
							  "' - Added CourseEnrollment";
				String auditMsg2 = getAuditMsg(msg2, "EnrollInCourse", 2);
				cLogger.info(auditMsg2);
				cLogger.info(getAuditMsg("", "EnrollInCourse", 0));
			}
			else {
				String msg = "Cannot enroll user " + user + 
				 " in course " + course + 
				 " because user is already enrolled in this course";
				cLogger.error(msg);
				throw new ApplicationException(msg);
			}
		} catch(DataException de) {
			String msg = "Could not enroll user '" + 
						 user + "' in course '" + course.getId() + "'";
			cLogger.error(msg, de);
			throw new ApplicationException(msg, de);
		}
	}
	
	public void dropCourse(UserMeta userMeta, 
						   Course course, 
						   Connection conn) 
		throws ApplicationException {
		
		try {
			//add course enrollment status for status updates
			UserCourseStatus userCourseStatus = 
				getUserCourseStatus(userMeta, course, conn);
			
			if(userCourseStatus.equals(UserCourseStatus.ENROLLED)) {
				CourseEnrollmentStatus courseEnrollmentStatus = 
					new CourseEnrollmentStatus(course.getId(), 
											   userMeta.getUserid(), 
											   UserCourseStatus.DROPPED,
											   new Timestamp((new Date()).getTime()));
				
				this.dataFacade.
					addCourseEnrollmentAction(conn, 
											  courseEnrollmentStatus);
				
				//TODO: Add audit point for step 1 of this 2 step action
				String msg1 = "'" + userMeta.getUserid() + 
				   			  "' in course '" + course.getId() + 
				   			  "' - Added CourseEnrollmentStatus";
				String auditMsg1 = getAuditMsg(msg1, "DroppedCourse", 1);
				cLogger.info(auditMsg1);
				
				//now delete row from course_enrollment table
				this.dataFacade.
					deleteCourseEnrollment(conn, userMeta, course);
				
				//TODO: Add audit point for step 2 of this 2 step action
				String msg2 = "Dropping user '" + userMeta.getUserid() + 
				   			  "' in course '" + course.getId() + 
				   			  "' - Deleted CourseEnrollment";
				String auditMsg2 = getAuditMsg(msg2, "DroppedCourse", 2);
				cLogger.info(auditMsg2);
				cLogger.info(getAuditMsg("", "DroppedCourse", 0));
			}
			else {
				String msg = "Could not drop user " + userMeta + 
				 " from course " + course + 
				 " because user is not enrolled in this course";
				cLogger.error(msg);
				throw new ApplicationException(msg);
			}
		} catch(DataException de) {
			String msg = "Could not drop user '" + userMeta + 
						 "' from course '" + course.getId() + "'";
			cLogger.error(msg, de);
			throw new ApplicationException(msg, de);
		}
	}
	
	//TODO: This method is not tested... also can we refactor this so we go through DataFacade and not directly to the database
	public UserCourseStatus getUserCourseStatus(UserMeta userMeta, 
												Course course, 
												Connection conn) 
		throws DataException {
		
		UserCourseStatus retVal = UserCourseStatus.UNENROLLED;
		
		String sql = 
			String.format(Sql.RETREIVE_COURSE_ENROLLMENTS_BY_USER_AND_COURSE, 
						  userMeta.getUserid(), 
						  DataFacadeRDBMSImpl.wrapForSQL(course.getId()));
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				retVal = UserCourseStatus.ENROLLED;
			}
		} catch(SQLException sqle) {
			String msg = "Could not get course enrollment status for user '" + 
						 userMeta.getUserid() + 
						 " in course '" + 
						 course.getId() + "'";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		}
		return retVal;
	}
	
	/**
	 * 
	 * @param conn
	 * @param question
	 * @throws DataException
	 */
	//TODO: not tested
	public void askQuestion(Connection conn, 
							Question question) throws DataException {
		if(question == null) {
			throw new NullPointerException("question cannot be null");
		}
		try {	
			conn.setAutoCommit(false);
			question = dataFacade.insertQuestion(conn, question);
			
			dataFacade.
				insertQuestionTimestamp(conn, 
										question.getId(), 
										new Date().getTime(), 
										Locale.getDefault());
			
			conn.commit();			
		} catch(SQLException sqle) {
			String msg = "Could not save question in the database";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		} finally {
			try { conn.setAutoCommit(true); } catch(SQLException sqle) 
			{cLogger.error("Could not set autoCommit back to true");}
		}
	}
	
	public void answerQuestion(Connection conn, 
							   Answer answer) throws DataException {
		try {
			conn.setAutoCommit(false);
			answer = dataFacade.insertAnswer(conn, answer);
			long timestamp = new Date().getTime();
			Locale locale = Locale.getDefault();
			dataFacade.insertAnswerTimestamp(conn, 
											 answer.getId(), 
											 timestamp, 
											 locale);
			conn.commit();			
		} catch(SQLException sqle) {
			String msg = "Could not save answer in the database";
			cLogger.error(msg, sqle);
			throw new DataException(msg, sqle);
		} finally {
			try { conn.setAutoCommit(true); } catch(SQLException sqle) 
			{cLogger.error("Could not set autoCommit back to true");}
		}
	}
	
	private String getAuditMsg(String msg, 
							   String auditPointId, 
							   int auditPoint) {
		
		if(auditPoint < 0) {
			throw new IllegalArgumentException("auditPoint cannot be < 0");
		}
		
		StringBuffer buff = new StringBuffer();
		buff.append("AUDIT POINT (");
		buff.append(auditPointId + ") ");
		if(auditPoint == 0) {
			buff.append("COMPLETED");
		}
		else if(auditPoint > 0) {
			buff.append("[");
			buff.append(auditPoint);
			buff.append("] - ");
			buff.append(msg);
		}
		
		return buff.toString();
	}
}
