package org.wwald;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.wwald.model.User;

import twitter4j.Twitter;
import twitter4j.http.RequestToken;

public class WWALDSession extends WebSession {

	private User user;
	private Twitter twitter;
	private RequestToken requestToken;
	
	public WWALDSession(Request request) {
		super(request);
	}
	
	public static WWALDSession get() {
		return (WWALDSession)Session.get();
	}
	
	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Twitter getTwitter() {
		return this.twitter;
	}
	
	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}
	
	public RequestToken getRequestToken() {
		return this.requestToken;
	}
	
	public void setRequestToken(RequestToken requestToken) {
		this.requestToken = requestToken;
	}
}
