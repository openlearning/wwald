package org.wwald;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.wwald.model.User;

public class WWALDSession extends WebSession {

	private User user;
	
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
}
