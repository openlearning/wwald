package org.wwald.model;

import java.io.Serializable;

public class Answer implements Serializable {
	
	private int id;
	private UserMeta userMeta;
	private int questionId;
	private String contents;
	
	public Answer() {
		
	}
	
	public Answer(int questionId) {
		this(questionId, null);
	}
	
	public Answer(int questionId, String contents) {
		this(0, questionId, contents);
	}
	
	public Answer(int id, int questionId, String contents) {
		this(id, null, questionId, contents);
	}
	
	public Answer(int id, UserMeta userMeta, int questionId, String contents) {
		this.id = id;
		this.userMeta = userMeta;
		this.questionId = questionId;
		this.contents = contents;
	}

	public int getId() {
		return id;
	}

	//TODO: Should we remove this method?
	public void setId(int id) {
		this.id = id;
	}
	
	public UserMeta getUserMeta() {
		return this.userMeta;
	}
	
	public void setUserMeta(UserMeta userMeta) {
		this.userMeta = userMeta;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(this.id + " : " + this.questionId + " :' " + 
					this.contents + "'");
		return buff.toString();
	}
}
