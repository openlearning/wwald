/**
 * 
 */
package org.wwald;

/**
 * @author pshah
 *
 */
public class Course {
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
