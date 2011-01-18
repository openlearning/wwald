package org.wwald.model;

import java.util.Date;


public class AnswerStatistics {
	
	private UserMeta user;
	private int likes;
	private long timestamp;
	
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
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
