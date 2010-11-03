package org.wwald.model;

public class StaticPage {
	
	private String id;
	private String contents;

	public StaticPage(String id, String contents) {
		this.id = id;
		this.contents = contents;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	
	
	
}
