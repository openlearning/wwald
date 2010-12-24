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
	
	public Competency(int id) {
		this.id = id;
	}
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Competency other = (Competency) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource)) {			
			return false;	
		}		
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}
