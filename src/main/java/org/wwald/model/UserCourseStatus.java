package org.wwald.model;

public enum UserCourseStatus {
	UNENROLLED(1),
	ENROLLED(2),
	COMPLETED(3);
	
	private int id;
	
	private UserCourseStatus(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public static UserCourseStatus getUserCourseStatus(int id) {
		UserCourseStatus userCourseStatuses[] = values();
		for(UserCourseStatus status : userCourseStatuses) {
			if(status.getId() == id) {
				return status;
			}
		}
		return null;
	}
}
