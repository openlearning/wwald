package org.wwald.model;

import java.io.Serializable;

public class Question implements Serializable {
	
	private int id;
	private String title;
	private String contents;
	private String discussionId;
	
	public Question() {
		
	}
	
	public Question(int id,
					String title, 
					String contents, 
					String discussionId) {
		this.id = id;
		this.title = title;
		this.contents = contents;
		this.discussionId = discussionId;
}
	
	public Question(String title, 
					String contents, 
					String discussionId) {
		this(0, title, contents, discussionId);
	}

	public int getId() {
		return this.id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getDiscussionId() {
		return discussionId;
	}

	public void setDiscussionId(String discussionId) {
		this.discussionId = discussionId;
	}
	
	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(this.id + " ");
		buff.append("'" + this.discussionId + "' ");
		buff.append("'" + this.title + "' ");
		buff.append("'" + this.contents + "'");
		return buff.toString();
	}
}
