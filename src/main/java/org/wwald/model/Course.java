/**
 * 
 */
package org.wwald.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * @author pshah
 *
 */
public class Course implements Serializable {
	
	private static final String DESCRIPTION_SHORTENING_CODE = "-short-";
	private String id;
	private String title;
	private String description;
	private List<Competency> competencies;
	private Mentor mentor;
	
	private static Logger cLogger = Logger.getLogger(Course.class);
	
	public Course() {}
	
	public Course(String id, String title, String description) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.competencies = new ArrayList<Competency>();
	}
		
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getFullDescription() {
		if(this.description != null) {
			return description.replaceAll(DESCRIPTION_SHORTENING_CODE, "       ");
		}
		else {
			return this.description;
		}
	}
	
	public String getShortDescription() {
		if(this.description != null) {
			int index = this.description.indexOf(DESCRIPTION_SHORTENING_CODE);
			if(index != -1) {
				return this.description.substring(0, index) + "...";
			}
			else {
				return this.description;
			}
		}
		else {
			return this.description;
		}
		
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
	
	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(this.id + " ");
		buff.append(this.title);
		//TODO: 
		buff.append("\n");
		buff.append(this.description);
		buff.append("Mentor: ");
		buff.append(this.mentor);
		buff.append("\n");
		buff.append("Competencies");
		buff.append("\n");
		for(Competency competency : this.competencies) {
			buff.append(competency);
			buff.append("\n");
		}
		return buff.toString();
	}

	public Competency getCompetency(int id) {
		Competency competency = null;
		
		for(Competency aCompetency : competencies) {
			if(id == aCompetency.getId()) {
				competency = aCompetency;
				break;
			}
		}
		
		return competency;
	}
	
	public Competency getCompetency(String id) {
		Competency competency = null;
		try {
			int intid = Integer.parseInt(id);
			competency = getCompetency(intid);
		} catch(NumberFormatException nfe) {
			cLogger.error("Could not convert String " + id + " to int ", nfe);
		}
		
		return competency;
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int hashCode = 1;
		hashCode = prime * hashCode + id.hashCode();
		hashCode = prime * hashCode + title.hashCode();
		hashCode = prime * hashCode + description.hashCode();
		hashCode = prime * hashCode + mentor.hashCode();
		for(Competency competency : this.competencies) {
			hashCode = prime * hashCode + competency.hashCode();
		}
		return hashCode;
	}
	
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null) {
			return false;
		}
		if(this.getClass() != o.getClass()) {
			return false;
		}
		Course other = (Course)o;
		if(this.getId() == null) {
			if(other.getId() != null) {
				return false;
			}
		} else if(!this.getId().equals(other.getId())) {
			return false;
		}
		if(this.getTitle() == null) {
			if(other.getTitle() != null) {
				return false;
			}
		} else if(!this.getTitle().equals(other.getTitle())) {
			return false;
		}
		if(this.getDescription() == null) {
			if(other.getDescription() != null) {
				return false;
			}
		} else if(!this.getDescription().equals(other.getDescription())) {
			return false;
		}
		if(this.competencies == null && other.competencies != null) {
			return false;
		} else if(this.competencies != null && other.competencies == null){
			return false;
		} else if(this.competencies == null && other.competencies == null) {
			return true;
		} else if(this.competencies.size() != other.getCompetencies().size()){
			return false;			
		} else {
			for(int i=0; i<this.competencies.size(); i++) {
				Competency thisCompetency = this.getCompetencies().get(i);
				Competency otherCompetency = other.getCompetencies().get(i);
				if(!thisCompetency.equals(otherCompetency)) {
					return false;
				}
			}
		}
		return true;
	}
}
