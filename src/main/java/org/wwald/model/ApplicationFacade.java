package org.wwald.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;
import org.wwald.WWALDSession;

public class ApplicationFacade {
	
	private IDataFacade dataFacade;
	
	private static Logger cLogger = Logger.getLogger(ApplicationFacade.class);
	
	public ApplicationFacade(IDataFacade dataFacade) {
		this.dataFacade = dataFacade;
	}
	
	public User login(String username, String password) {
		return dataFacade.retreiveUser(username, password);
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
			this.dataFacade.addCourseEnrollmentAction(courseEnrollmentStatus);
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
			this.dataFacade.addCourseEnrollmentAction(courseEnrollmentStatus);
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
			this.dataFacade.getCourseEnrollmentStatus(user, course);
		return courseEnrollmentStatus.getUserCourseStatus();
	}
}
