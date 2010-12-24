package org.wwald.model;

import java.io.Serializable;

public class UserMeta implements Serializable {
	
	public enum LoginVia {
		INTERNAL,
		TWITTER,
		FACEBOOK,
		OPENID;
	}
	
	private int userid;
	private Role role;
	private String identifier;
	private UserMeta.LoginVia loginVia;
	
	public int getUserid() {
		return userid;
	}
	
	public void setUserid(int userid) {
		this.userid = userid;
	}
	
	public Role getRole() {
		return this.role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public UserMeta.LoginVia getLoginVia() {
		return loginVia;
	}
	
	public void setLoginVia(UserMeta.LoginVia loginVia) {
		this.loginVia = loginVia;
	}
	
	public String toString() {
		return this.userid + " " + this.identifier + " " + this.loginVia + " " + this.role;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result
				+ ((loginVia == null) ? 0 : loginVia.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + userid;
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
		UserMeta other = (UserMeta) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (loginVia == null) {
			if (other.loginVia != null)
				return false;
		} else if (!loginVia.equals(other.loginVia))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (userid != other.userid)
			return false;
		return true;
	}

	
}
