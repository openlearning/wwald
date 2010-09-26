/**
 * 
 */
package org.wwald;

import java.io.Serializable;
import java.util.List;

/**
 * @author pshah
 *
 */
public class Course implements Serializable {
	private String id;
	private String title;
	private String description;
	private List<Competency> competencies;
	private Mentor mentor;
	
	public Course(String id, String title, String description) {
		this.id = id;
		this.title = title;
		this.description = description;
	}
		
	public String getId() {
		return this.id;
	}
	
	public void setId() {
		this.id = id;
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
	
	public void setCompetencies(List<Competency> competencies) {
		this.competencies = competencies;
	}
	
	public List<Competency> getCompetencies() {
		return this.competencies;
	}

	public void setMentor(Mentor mentor) {
		this.mentor = mentor;
	}
	
	public Mentor getMentor() {
		return this.mentor;
	}
}
