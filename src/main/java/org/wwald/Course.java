/**
 * 
 */
package org.wwald;

import java.io.Serializable;

/**
 * @author pshah
 *
 */
public class Course implements Serializable {
	private String title;
	private String description;
	
	public Course(String title, String description) {
		this.title = title;
		this.description = description;
	}
	
	public Course() {
		
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
}
