package org.wwald.model;

import java.util.Date;


public class AnswerStatistics {
	
	private UserMeta user;
	private int likes;
	private Date timestamp;
	
	public UserMeta getUser() {
		return user;
	}
	
	public void setUser(UserMeta user) {
		this.user = user;
	}
	
	public int getLikes() {
		return likes;
	}
	
	public void setLikes(int likes) {
		this.likes = likes;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
}
