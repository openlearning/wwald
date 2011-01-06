package org.wwald.model;

import java.io.Serializable;

public class Answer implements Serializable {
	
	private int id;
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
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
