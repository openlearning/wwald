package org.wwald.service;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;
import org.wwald.WWALDSession;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Course;
import org.wwald.model.CourseEnrollmentStatus;
import org.wwald.model.User;
import org.wwald.model.UserCourseStatus;

public class ApplicationFacade {
	
	private IDataFacade dataFacade;
	
	private static Logger cLogger = Logger.getLogger(ApplicationFacade.class);
	
	public ApplicationFacade(IDataFacade dataFacade) {
		this.dataFacade = dataFacade;
	}
	
	public User login(String username, String password, String databaseId) throws ApplicationException{
		User user = null;
		try {
			cLogger.info("Trying to login user '" + username + "'");
			user = dataFacade.retreiveUser(ConnectionPool.getConnection(databaseId), username, password);
			if(user != null) {
				cLogger.info("User " + username + " logged in succesully");
			}
			else {
				cLogger.info("User " + username + " NOT logged in");
			}
		} catch(DataException de) {
			String msg = "Could not not login user " + username;
			cLogger.error(msg, de);
			throw new ApplicationException(msg, de);
		}
		return user; 
	}
	
	public void logout() {
		User user = WWALDSession.get().getUser();
		String msg = "Logging out user ";
		if(user != null) {
			msg += user.getUsername();
		}
		cLogger.info(msg);
		WWALDSession.get().invalidateNow();
	}
	
	public void enrollInCourse(User user, Course course, String databaseId) throws ApplicationException {
		try {
			UserCourseStatus userCourseStatus = getUserCourseStatus(user, course, databaseId);
			if(!userCourseStatus.equals(UserCourseStatus.ENROLLED)) {
				CourseEnrollmentStatus courseEnrollmentStatus = 
					new CourseEnrollmentStatus(course.getId(), 
											   user.getUsername(), 
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
	
	public void dropCourse(User user, Course course, String databaseId) throws ApplicationException {
		try {
			UserCourseStatus userCourseStatus = getUserCourseStatus(user, course, databaseId);
			if(userCourseStatus.equals(UserCourseStatus.ENROLLED)) {
				CourseEnrollmentStatus courseEnrollmentStatus = 
					new CourseEnrollmentStatus(course.getId(), 
											   user.getUsername(), 
											   UserCourseStatus.DROPPED,
											   new Timestamp((new Date()).getTime()));
				this.dataFacade.addCourseEnrollmentAction(ConnectionPool.getConnection(databaseId), courseEnrollmentStatus);
			}
			else {
				String msg = "Could not drop user " + user + 
				 " from course " + course + 
				 " because user is not enrolled in this course";
				cLogger.error(msg);
				throw new ApplicationException(msg);
			}
		} catch(DataException de) {
			String msg = "Could not drop user '" + user + "' from course '" + course.getId() + "'";
			cLogger.error(msg, de);
			throw new ApplicationException(msg, de);
		}
	}
	
	public UserCourseStatus getUserCourseStatus(User user, Course course, String databaseId) throws DataException {
		CourseEnrollmentStatus courseEnrollmentStatus = 
			this.dataFacade.getCourseEnrollmentStatus(ConnectionPool.getConnection(databaseId), user, course);
		return courseEnrollmentStatus.getUserCourseStatus();
	}
}
