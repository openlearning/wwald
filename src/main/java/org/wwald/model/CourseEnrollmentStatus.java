package org.wwald.model;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

public class CourseEnrollmentStatus {
	
	private String courseId;
	private String username;
	private UserCourseStatus userCourseStatus;
	private Timestamp timestamp;
	
	public static class TimestampComparator<T extends CourseEnrollmentStatus> implements Comparator<T> {

		public int compare(CourseEnrollmentStatus o1, CourseEnrollmentStatus o2) {
			if(o1 != null && o2 != null) {
				return o1.getTimestamp().compareTo(o2.getTimestamp());
			}
			else if(o1 != null) {
				return 1;
			}
			else if(o2 != null) {
				return -1;
			}
			else {
				return 0;
			}
		}
		
	}
	
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
	
	public static Comparator<CourseEnrollmentStatus> getTimestampComparator() {
		return new TimestampComparator<CourseEnrollmentStatus>();
	}
}
