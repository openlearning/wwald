package org.wwald.model;

import java.util.Date;

public class CourseEnrollmentStatus {
	
	private String courseId;
	private String username;
	private UserCourseStatus userCourseStatus;
	private Date timestamp;
	
	public CourseEnrollmentStatus(String courseId,
								  String username,
								  UserCourseStatus userCourseStatus,
								  Date timestamp) {
		this.courseId = courseId;
		this.username = username;
		this.userCourseStatus = userCourseStatus;
		this.timestamp = timestamp;
	}

	public String getCourseId() {
		return courseId;
	}

	public String getUsername() {
		return username;
	}

	public UserCourseStatus getUserCourseStatus() {
		return userCourseStatus;
	}

	public Date getTimestamp() {
		return timestamp;
	}	
}
