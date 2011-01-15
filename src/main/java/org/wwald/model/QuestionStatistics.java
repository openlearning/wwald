package org.wwald.model;

import java.util.List;

public class QuestionStatistics {
	
	private Question question;
	private int numberOfAnswers;
	private UserMeta questioner;
	private int likes;
	private List<String> tags;
	
	public QuestionStatistics() {
		
	}
	
	public QuestionStatistics(Question question) {
		this.question = question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}
	
	public Question getQuestion() {
		return this.question;
	}
	
	public int getNumberOfAnswers() {
		return numberOfAnswers;
	}
	
	public void setNumberOfAnswers(int numberOfAnswers) {
		this.numberOfAnswers = numberOfAnswers;
	}
	
	public UserMeta getQuestioner() {
		return this.question.getUserMeta();
	}
	
	public int getLikes() {
		return likes;
	}
	
	public void setLikes(int likes) {
		this.likes = likes;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
}
