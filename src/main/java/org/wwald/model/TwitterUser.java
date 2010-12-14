package org.wwald.model;


public class TwitterUser extends User {
	
	public TwitterUser(String screenName) {
		setRole(Role.STUDENT);
		setUsername(screenName);
	}
	
	@Override
	public TwitterUser duplicate() {
		return new TwitterUser(getUsername()); 
	}
}
