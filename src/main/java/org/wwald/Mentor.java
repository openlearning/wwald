package org.wwald;

public class Mentor {
	
	private String name;
	private String questionsAnswered;
	private String lastLogin;
	
	public Mentor(String name, String questionsAnswered, String lastLogin) {
		this.name = name;
		this.questionsAnswered = questionsAnswered;
		this.lastLogin = lastLogin;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuestionsAnswered() {
		return questionsAnswered;
	}

	public void setQuestionsAnswered(String questionsAnswered) {
		this.questionsAnswered = questionsAnswered;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
}
