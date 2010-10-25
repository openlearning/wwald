package org.wwald.model;

import java.io.Serializable;

import org.apache.wicket.Application;
import org.wwald.WWALDApplication;

import com.cforcoding.jmd.MarkDown;

public class Competency implements Serializable {
	
	private int id;
	private String title;
	private String description;
	private String resource;
	
	public Competency() {}
	
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
		if(this.id == 0) {
			this.id = title.hashCode();
		}
	}

	public String getDescription() {
		return description;
	}
	
	public String getTranformedDescription() {
		WWALDApplication app = (WWALDApplication)Application.get();
		return app.getMarkDown().transform(this.description);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResource() {
		return resource;
	}
	
	public String getTransformedResources() {
		WWALDApplication app = (WWALDApplication)Application.get();
		return app.getMarkDown().transform(this.resource);
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	
	@Override
	public String toString() {
		return this.id + " " + this.title + " " + this.description + " " + this.resource;
	}
}
