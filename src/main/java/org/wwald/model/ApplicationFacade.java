package org.wwald.model;

import java.util.Date;

public class ApplicationFacade {
	
	public User login(String username, String password) {
		return new User("", "", "", username, new Date(), Role.STUDENT);
	}
}
