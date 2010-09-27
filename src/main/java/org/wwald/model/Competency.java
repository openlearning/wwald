package org.wwald.model;

public class Competency {
	
	private String id;
	private String title;
	private String description;
	private String resource;
	
	public Competency(String id, String title, String description, String resource) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.resource = resource;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	
	public static Competency createBlankCompeteny() {
		return new Competency("", "", "", "");
	}

}
