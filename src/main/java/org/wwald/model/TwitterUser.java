package org.wwald.model;


public class TwitterUser extends User {

	private String username;
	private Role role;
	
	public TwitterUser(String screenName) {
		this.role = Role.STUDENT;
		this.username = screenName;
	}
}
