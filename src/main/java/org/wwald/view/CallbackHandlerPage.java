package org.wwald.view;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.WebRequest;
import org.wwald.WWALDSession;
import org.wwald.model.UserMeta;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class CallbackHandlerPage extends BasePage {

	private static final Logger cLogger = Logger.getLogger(CallbackHandlerPage.class);
	
	public CallbackHandlerPage(PageParameters parameters) {
		super(parameters);
		Twitter twitter = WWALDSession.get().getTwitter();
		RequestToken requestToken =  WWALDSession.get().getRequestToken();
		String verifier = getVerifier();
		try {
			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
			WWALDSession.get().setRequestToken(null);
			cLogger.info("User logged in with Twitter '" + accessToken.getScreenName() + "', '" + accessToken.getUserId() + "'");
			UserMeta userMeta = new UserMeta();
			userMeta.setIdentifier(twitter.getScreenName());
			WWALDSession.get().setUserMeta(userMeta);
		} catch (TwitterException e) {
			String msg = "Caught Exception while handling the callback from Twitter";
			cLogger.error(msg, e);
			setResponsePage(GenericErrorPage.class);
		}
		setResponsePage(HomePage.class);
	}

	private String getVerifier() {
		WebRequest request = (WebRequest)getRequest();
		return request.getHttpServletRequest().getParameter("oauth_verifier");
	}

}
