/**
 * 
 */
package org.wwald.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Represents a StatusUpdate object. This object represents any activity which
 * constitutes a status update which should be reported on the StatusUpdate
 * panel
 * @author pshah
 *
 */
public class StatusUpdate implements Serializable {
	
	private UserMeta userMeta;
	private Timestamp timestamp;
	private String enrollmentStatus;
	private String courseId;
	
	public StatusUpdate() {
		
	}

	public String getText() {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern("yyyy-MM-dd");
		return dateFormat.format(this.timestamp) + ": " + 
			   userMeta.getIdentifier() + " " + 
			   this.enrollmentStatus + 
			   " course " + this.courseId;
	}
	
	public String getShortText() {
		return this.enrollmentStatus + " course ";
	}
	
	public void setUserMeta(UserMeta userMeta) {
		this.userMeta = userMeta;
	}
	
	public UserMeta getUserMeta() {
		return this.userMeta;
	}
	
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public Timestamp getTimestamp() {
		return this.timestamp;
	}
	
	public void setEnrollmentStatus(String enrollmentStatus) {
		this.enrollmentStatus = enrollmentStatus;
	}
	
	public String getEnrollmentStatus() {
		return this.enrollmentStatus;
	}
	
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	
	public String getCourseId() {
		return this.courseId;
	}
}
