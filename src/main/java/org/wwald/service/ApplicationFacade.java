package org.wwald.service;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.wwald.WWALDSession;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Course;
import org.wwald.model.CourseEnrollmentStatus;
import org.wwald.model.User;
import org.wwald.model.UserCourseStatus;
import org.wwald.model.UserMeta;

public class ApplicationFacade {
	
	private IDataFacade dataFacade;
	
	private static Logger cLogger = Logger.getLogger(ApplicationFacade.class);
	
	public ApplicationFacade(IDataFacade dataFacade) {
		this.dataFacade = dataFacade;
	}
	
	public UserMeta login(String username, String password, String databaseId) throws ApplicationException{
		User user = null;
		UserMeta userMeta = null;
		try {
			cLogger.info("Trying to login user '" + username + "'");
			String passwordInDb = dataFacade.retreivePassword(ConnectionPool.getConnection(databaseId), username);
			BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
			if(passwordInDb != null && passwordEncryptor.checkPassword(password, passwordInDb)) {
				user = dataFacade.retreiveUserByUsername(ConnectionPool.getConnection(databaseId), username);
				if(user == null) {
					cLogger.error("THIS SHOULD NEVER HAPPEN. COULD GET PASSWORD BUT COULD NOT GET USER OBJECT");
				}
				cLogger.info("User " + username + " logged in succesully");
				userMeta = 
					dataFacade.
						retreiveUserMetaByIdentifierLoginVia(ConnectionPool.getConnection(databaseId), 
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
	
	public void logout() {
		UserMeta userMeta = WWALDSession.get().getUserMeta();
		String msg = "Logging out user ";
		if(userMeta != null) {
			msg += userMeta.getIdentifier();
		}
		cLogger.info(msg);
		WWALDSession.get().invalidateNow();
	}
	
	public void enrollInCourse(UserMeta user, Course course, String databaseId) throws ApplicationException {
		try {
			UserCourseStatus userCourseStatus = getUserCourseStatus(user, course, databaseId);
			if(!userCourseStatus.equals(UserCourseStatus.ENROLLED)) {
				CourseEnrollmentStatus courseEnrollmentStatus = 
					new CourseEnrollmentStatus(course.getId(), 
											   user.getUserid(), 
											   UserCourseStatus.ENROLLED,
											   new Timestamp((new Date()).getTime()));
				this.dataFacade.addCourseEnrollmentAction(ConnectionPool.getConnection(databaseId), courseEnrollmentStatus);
			}
			else {
				String msg = "Cannot enroll user " + user + 
				 " in course " + course + 
				 " because user is already enrolled in this course";
				cLogger.error(msg);
				throw new ApplicationException(msg);
			}
		} catch(DataException de) {
			String msg = "Could not enroll user '" + user + "' in course '" + course.getId() + "'";
			cLogger.error(msg, de);
			throw new ApplicationException(msg, de);
		}
	}
	
	public void dropCourse(UserMeta userMeta, Course course, String databaseId) throws ApplicationException {
		try {
			UserCourseStatus userCourseStatus = getUserCourseStatus(userMeta, course, databaseId);
			if(userCourseStatus.equals(UserCourseStatus.ENROLLED)) {
				CourseEnrollmentStatus courseEnrollmentStatus = 
					new CourseEnrollmentStatus(course.getId(), 
											   userMeta.getUserid(), 
											   UserCourseStatus.DROPPED,
											   new Timestamp((new Date()).getTime()));
				this.dataFacade.addCourseEnrollmentAction(ConnectionPool.getConnection(databaseId), courseEnrollmentStatus);
			}
			else {
				String msg = "Could not drop user " + userMeta + 
				 " from course " + course + 
				 " because user is not enrolled in this course";
				cLogger.error(msg);
				throw new ApplicationException(msg);
			}
		} catch(DataException de) {
			String msg = "Could not drop user '" + userMeta + "' from course '" + course.getId() + "'";
			cLogger.error(msg, de);
			throw new ApplicationException(msg, de);
		}
	}
	
	public UserCourseStatus getUserCourseStatus(UserMeta userMeta, Course course, String databaseId) throws DataException {
		CourseEnrollmentStatus courseEnrollmentStatus = 
			this.dataFacade.getCourseEnrollmentStatus(ConnectionPool.getConnection(databaseId), userMeta, course);
		return courseEnrollmentStatus.getUserCourseStatus();
	}
}
