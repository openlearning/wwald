package org.wwald.view;

import java.io.IOException;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.RequestToken;

public class SocialLoginPanel extends Panel {

	private static final Logger cLogger = Logger.getLogger(SocialLoginPanel.class);
	
	public SocialLoginPanel(String id) {
		super(id);		
		Link loginWithTwitterLink =	new Link(WicketIdConstants.LOGIN_WITH_TWITTER) {
			@Override
			public void onClick() {				
		        try {
		        	setTwitterConsumerAndSecretKey();		        	
		            StringBuffer callbackURL = getCallbackUrl();

		            Twitter twitter = new TwitterFactory().getInstance();
					WWALDSession.get().setTwitter(twitter);
		            RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());
		            WWALDSession.get().setRequestToken(requestToken);
		            redirectToExternalUrl(requestToken.getAuthenticationURL());
		        } catch (Exception e) {
		        	String msg = "Could not redirect to Twitter for authentication";
		        	cLogger.error(msg, e);
		        	PageParameters parameters = new PageParameters();
		        	parameters.add(WicketIdConstants.MESSAGES, msg);
		            setResponsePage(GenericErrorPage.class);
		        }
			}

			private void setTwitterConsumerAndSecretKey() throws DataException {
				ServletWebRequest request = (ServletWebRequest)getRequest();
				String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
				String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
				Connection conn = ConnectionPool.getConnection(databaseId);
				IDataFacade dataFacade = ((WWALDApplication)getApplication()).getDataFacade();
				String twitterConsumerKey = dataFacade.retreiveFromKvTableClob(conn, WicketIdConstants.KVTableKey_TWITTER_CONSUMER);
				String twitterSecretKey = dataFacade.retreiveFromKvTableClob(conn, WicketIdConstants.KVTableKey_TWITTER_SECRET);
				
				if(twitterConsumerKey == null) {
					cLogger.warn("Twitter consumer key is null for database " + databaseId);
				}
				else if(twitterConsumerKey.trim().equals("")) {
					cLogger.warn("Twitter consumer key is blank for database " + databaseId);
				}
				
				if(twitterSecretKey == null) {
					cLogger.warn("Twitter secret key is null for database " + databaseId);
				}
				else if(twitterSecretKey.trim().equals("")) {
					cLogger.warn("Twitter secret key is null for database " + databaseId);
				}
				
				System.setProperty("twitter4j.oauth.consumerKey",twitterConsumerKey) ; 
				System.setProperty("twitter4j.oauth.consumerSecret",twitterSecretKey) ;
//				System.setProperty("twitter4j.oauth.accessToken","xxx");
//				System.setProperty("twitter4j.oauth.accessTokenSecret","xxx");				
			}

			private void redirectToExternalUrl(String authenticationURL) throws IOException {
				WebResponse webResponse = (WebResponse)getResponse();
				webResponse.getHttpServletResponse().sendRedirect(authenticationURL);	
			}

			private StringBuffer getCallbackUrl() {
				ServletWebRequest request = (ServletWebRequest)getRequest();
				StringBuffer requestUrl = request.getHttpServletRequest().getRequestURL();
				int index = requestUrl.lastIndexOf("/");
	            StringBuffer callbackUrl = requestUrl.replace(index, requestUrl.length(), "").append(WWALDConstants.TWITTER_CALLBACK_URL);
	            cLogger.info("Callback URL for Twitter login " + callbackUrl);
	            return callbackUrl;
			}
		};
	
		loginWithTwitterLink.add(new Label(WicketIdConstants.LOGIN_WITH_TWITTER_LABEL, "Login With Twitter"));
		add(loginWithTwitterLink);		
	}

}
