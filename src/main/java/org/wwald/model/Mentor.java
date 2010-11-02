package org.wwald.model;

import java.io.Serializable;

public class Mentor implements Serializable {
	
	private int id;
	private String firstName;
	private String middleInitial;
	private String lastName;
	private String shortBio;
	
	public Mentor() {
		
	}
	
	public Mentor(int id, String firstName, String middleInitial, String lastName, String shortBio) {
		this.id = id;
		this.firstName = firstName;
		this.middleInitial = middleInitial;
		this.lastName = lastName;
		this.shortBio = shortBio;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getShortBio() {
		return shortBio;
	}

	public void setShortBio(String shortBio) {
		this.shortBio = shortBio;
	}
	
	@Override
	public String toString() {
		//StringBuffer buff = new StringBuffer();
		return this.firstName + " " + this.middleInitial + " " + this.lastName;
	}
}
