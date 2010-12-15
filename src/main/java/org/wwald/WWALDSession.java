package org.wwald;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.wwald.model.UserMeta;

import twitter4j.Twitter;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class WWALDSession extends WebSession {

	private UserMeta userMeta;
	private Twitter twitter;
	private RequestToken requestToken;
	private AccessToken accessToken;
	
	public WWALDSession(Request request) {
		super(request);
	}
	
	public static WWALDSession get() {
		return (WWALDSession)Session.get();
	}
	
	public UserMeta getUserMeta() {
		if(this.userMeta == null) {
			return null;
		}
		//TODO: Duplicate the user meta object
		return this.userMeta;
	}
	
	public void setUserMeta(UserMeta userMeta) {
		this.userMeta = userMeta;
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
	
	public AccessToken getAccessToken() {
		return this.accessToken;
	}
	
	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}
}
