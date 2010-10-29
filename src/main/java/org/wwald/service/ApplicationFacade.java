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
	
	public User login(String username, String password) throws ApplicationException{
		User user = null;
		try {
			user = dataFacade.retreiveUser(ConnectionPool.getConnection(), username, password);
		} catch(DataException de) {
			String msg = "Could not not login user";
			cLogger.error(msg, de);
			throw new ApplicationException(msg, de);
		}
		
		return user; 
	}
	
	public void logout() {
		WWALDSession.get().setUser(null);
	}
	
	public void enrollInCourse(User user, Course course) {
		UserCourseStatus userCourseStatus = getUserCourseStatus(user, course);
		if(!userCourseStatus.equals(UserCourseStatus.ENROLLED)) {
			CourseEnrollmentStatus courseEnrollmentStatus = 
				new CourseEnrollmentStatus(course.getId(), 
										   user.getUsername(), 
										   UserCourseStatus.ENROLLED,
										   new Timestamp((new Date()).getTime()));
			this.dataFacade.addCourseEnrollmentAction(ConnectionPool.getConnection(), courseEnrollmentStatus);
		}
		else {
			String msg = "Cannot enroll user " + user + 
			 " in course " + course + 
			 " because user is already enrolled in this course";
			cLogger.warn(msg);
		}
	}
	
	public void dropCourse(User user, Course course) {
		UserCourseStatus userCourseStatus = getUserCourseStatus(user, course);
		if(userCourseStatus.equals(UserCourseStatus.ENROLLED)) {
			CourseEnrollmentStatus courseEnrollmentStatus = 
				new CourseEnrollmentStatus(course.getId(), 
										   user.getUsername(), 
										   UserCourseStatus.DROPPED,
										   new Timestamp((new Date()).getTime()));
			this.dataFacade.addCourseEnrollmentAction(ConnectionPool.getConnection(), courseEnrollmentStatus);
		}
		else {
			String msg = "Cannot drop user " + user + 
			 " from course " + course + 
			 " because user is not enrolled in this course";
			cLogger.warn(msg);
		}
	}
	
	public UserCourseStatus getUserCourseStatus(User user, Course course) {
		CourseEnrollmentStatus courseEnrollmentStatus = 
			this.dataFacade.getCourseEnrollmentStatus(ConnectionPool.getConnection(), user, course);
		return courseEnrollmentStatus.getUserCourseStatus();
	}
}
