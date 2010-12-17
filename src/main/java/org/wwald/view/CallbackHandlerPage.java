package org.wwald.view;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.WebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

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
			String screenName = accessToken.getScreenName();
			cLogger.info("User logged in with Twitter '" + screenName + "', '" + accessToken.getUserId() + "'");
			
			String databaseId = getDatabaseId();
			IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
			
			UserMeta userMeta = dataFacade.
				retreiveUserMetaByIdentifierLoginVia(ConnectionPool.getConnection(databaseId), 
													 screenName, 
													 UserMeta.LoginVia.TWITTER);			
			if(userMeta == null) {
				userMeta = new UserMeta();
				userMeta.setIdentifier("http://twitter.com/" + screenName);
				userMeta.setLoginVia(UserMeta.LoginVia.TWITTER);
				userMeta.setRole(Role.STUDENT);
				dataFacade.insertUserMeta(ConnectionPool.getConnection(databaseId), userMeta);
				
				userMeta = dataFacade.
					retreiveUserMetaByIdentifierLoginVia(ConnectionPool.getConnection(databaseId), 
													 	 screenName, 
													 	 UserMeta.LoginVia.TWITTER);
			}			
			
			if(userMeta == null) {
				cLogger.warn("UserMeta object for '" + screenName + "' is null");
			}
			
			WWALDSession.get().setUserMeta(userMeta);
		} catch (TwitterException e) {
			String msg = "Caught Exception while handling the callback from Twitter";
			cLogger.error(msg, e);
			setResponsePage(GenericErrorPage.class);
		} catch(DataException de) {
			String msg = "Caught Exception while retreiving/inserting UserMeta after Twitter authentication";
			cLogger.error(msg, de);
			setResponsePage(GenericErrorPage.class);
		}
		setResponsePage(HomePage.class);
	}

	private String getVerifier() {
		WebRequest request = (WebRequest)getRequest();
		return request.getHttpServletRequest().getParameter("oauth_verifier");
	}

}
