package org.wwald.model;

import java.io.Serializable;
import java.util.Date;

import org.jasypt.util.password.BasicPasswordEncryptor;

public class User implements Serializable{	
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private String email;
	private Date joinDate;
	private Role role;
	
	public User() {}
	
	public User(String firstName,  
				String lastName,
				String username,
				Date joinDate,
				Role role) {
		this.firstName = firstName;
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
	
	public String getEncryptedPassword() {
		BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();		
		return passwordEncryptor.encryptPassword(this.password);
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return this.email;
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
			   this.lastName + " " + 
			   this.username + " " +
			   this.username + " " +
			   this.email + " " + 
			   this.joinDate + " " + 
			   this.role;
	}
	
	public User duplicate() {
		User dupUser = new User();
		dupUser.setFirstName(this.getFirstName());
		dupUser.setLastName(this.getLastName());
		dupUser.setUsername(this.getUsername());
		dupUser.setPassword(this.getPassword());
		dupUser.setEmail(this.getEmail());
		dupUser.setJoinDate(this.getJoinDate());
		dupUser.setRole(this.getRole());
		return dupUser;
	}	
}
