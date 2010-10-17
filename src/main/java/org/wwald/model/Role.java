package org.wwald.model;

public enum Role {
	STUDENT, 
	MENTOR(Permission.EDIT_COURSE), 
	ADMIN(Permission.EDIT_COURSE, Permission.EDIT_COURSES);
	
	private Permission permissions[];
	
	private Role(Permission ... permission) {
		this.permissions = permission;
	}
	
	public Permission[] getPermissions() {
		return this.permissions;
	}
	
}
