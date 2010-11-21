package org.wwald.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable{	
	private String firstName;
	private String mi;
	private String lastName;
	private String username;
	private String password;
	private Date joinDate;
	private Role role;
	
	public User() {}
	
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

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setMi(String mi) {
		this.mi = mi;
	}
	
	public String getMi() {
		return mi;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return this.password;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	
	public Date getJoinDate() {
		return joinDate;
	}

	public void setRole(Role role) {
		this.role = role;
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
