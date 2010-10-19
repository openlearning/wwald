package org.wwald.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable{	
	private String firstName;
	private String mi;
	private String lastName;
	private String username;
	private Date joinDate;
	private Role role;
	
	public User(String firstName, 
				String mi, 
				String lastName,
				String username,
				Date joinDate,
				Role role) {
		this.firstName = firstName;
		this.mi = mi;
		this.lastName = lastName;
		this.username = username;
		this.joinDate = joinDate;
		this.role = role;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMi() {
		return mi;
	}

	public String getLastName() {
		return lastName;
	}
	
	public String getUsername() {
		return this.username;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public Role getRole() {
		return role;
	}
	
	@Override
	public String toString() {
		return this.firstName + " " + 
			   this.mi + " " + 
			   this.lastName + " " + 
			   this.username + " " + 
			   this.joinDate + " " + 
			   this.role;
	}
	
}
