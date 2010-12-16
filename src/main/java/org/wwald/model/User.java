package org.wwald.model;

import java.io.Serializable;
import java.util.Date;

import org.jasypt.util.password.BasicPasswordEncryptor;

public class User implements Serializable{	
	private String username;
	private String password;
	private String email;
	
	public User() {}

	public User(String username) {
		this.username = username;
	}

	public User(String username, String email) {
		this.username = username;
		this.email = email;
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
	
	@Override
	public String toString() {
		return this.username + " " +
			   this.username + " " +
			   this.email;
	}
	
	public User duplicate() {
		User dupUser = new User();
		dupUser.setUsername(this.getUsername());
		dupUser.setPassword(this.getPassword());
		dupUser.setEmail(this.getEmail());
		return dupUser;
	}	
}
