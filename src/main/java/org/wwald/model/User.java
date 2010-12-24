package org.wwald.model;

import java.io.Serializable;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
