package org.wwald.model;

public class Competency {
	
	private String title;
	private String description;
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

	private String resource;
	
	public Competency(String title, String description, String resource) {
		this.title = title;
		this.description = description;
		this.resource = resource;
	}
}
