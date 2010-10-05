package org.wwald.model;

import java.io.Serializable;

public class Competency implements Serializable {
	
	private int id;
	private String title;
	private String description;
	private String resource;
	
	public Competency(int id, String title, String description, String resource) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.resource = resource;
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
	
	@Override
	public String toString() {
		return this.id + " " + this.title + " " + this.description + " " + this.resource;
	}
}
