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
	
}
