package org.wwald.model;

public class StaticPagePOJO {
	
	private String id;
	private String contents;

	public StaticPagePOJO(String id, String contents) {
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
	
	public String toString() {
		return this.id + " : " + this.contents;
	}
	
}
