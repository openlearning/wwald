package org.wwald.model;

import java.sql.Timestamp;
import java.util.Date;

public class CourseEnrollmentStatus {
	
	private String courseId;
	private String username;
	private UserCourseStatus userCourseStatus;
	private Timestamp timestamp;
	
	public CourseEnrollmentStatus(String courseId,
								  String username,
								  UserCourseStatus userCourseStatus,
								  Timestamp timestamp) {
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

	public Timestamp getTimestamp() {
		return timestamp;
	}	
}
