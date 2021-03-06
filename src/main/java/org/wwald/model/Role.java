package org.wwald.model;

public enum Role {
	ALL,
	UNAUTHENTICATED_USER,
	STUDENT, 
	MENTOR(Permission.EDIT_COURSE), 
	ADMIN(Permission.EDIT_COURSE, 
		  Permission.EDIT_COURSES,
		  Permission.ADD_MENTOR, 
		  Permission.ADD_USER, 
		  Permission.MANAGE_USERS,
		  Permission.SITE_ANALYTICS,
		  Permission.MANAGE_DB);
	
	private Permission permissions[];
	
	private Role(Permission ... permission) {
		this.permissions = permission;
	}
	
	public Permission[] getPermissions() {
		return this.permissions;
	}
	
}
