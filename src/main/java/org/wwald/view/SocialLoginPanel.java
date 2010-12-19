package org.wwald.view;

import java.io.IOException;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.resource.ContextRelativeResource;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.RequestToken;

public class SocialLoginPanel extends Panel {

	private static final Logger cLogger = Logger.getLogger(SocialLoginPanel.class);
	
	public SocialLoginPanel(String id) {
		super(id);
		
		Link loginWithTwitterLink =	getLoginWithTwitterLink();
//		Link loginWithTwitterLink =	getTestLoginLink();
		loginWithTwitterLink.add(getTwitterImage());
		add(loginWithTwitterLink);		
	}
	
	private Link getLoginWithTwitterLink() {
		return new Link(WicketIdConstants.LOGIN_WITH_TWITTER) {
			@Override
			public void onClick() {				
		        try {		        			        	
		            StringBuffer callbackURL = getCallbackUrl();
		            Twitter twitter = new TwitterFactory().getInstance();
					WWALDSession.get().setTwitter(twitter);
					setTwitterConsumerAndSecretKey(twitter);					
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

			private void setTwitterConsumerAndSecretKey(Twitter twitter) throws DataException {
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
				
//				System.setProperty("twitter4j.oauth.consumerKey",twitterConsumerKey) ; 
//				System.setProperty("twitter4j.oauth.consumerSecret",twitterSecretKey) ;
				twitter.setOAuthConsumer(twitterConsumerKey, twitterSecretKey);
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
	}
	
	private Link getTestLoginLink() {
		
		return new Link(WicketIdConstants.LOGIN_WITH_TWITTER) {
			@Override
			public void onClick() {				
		        try {
		        	String identifier = "http://twitter.com/testuser";
		        	UserMeta.LoginVia loginVia = UserMeta.LoginVia.TWITTER;
		        	String databaseId = ConnectionPool.
		        							getDatabaseIdFromRequest((ServletWebRequest)getRequest());
		        	IDataFacade dataFacade = WWALDApplication.get().getDataFacade(); 
		        	UserMeta testUser = dataFacade.
		        		retreiveUserMetaByIdentifierLoginVia(ConnectionPool.getConnection(databaseId), 
		        											 identifier, 
		        											 loginVia);
		        	if(testUser == null) {
		        		testUser = new UserMeta();
		        		testUser.setIdentifier(identifier);
		        		testUser.setRole(Role.STUDENT);
		        		testUser.setLoginVia(loginVia);
		        		dataFacade.insertUserMeta(ConnectionPool.getConnection(databaseId), testUser);
		        	}
		        	
		        	testUser = dataFacade.
	        					retreiveUserMetaByIdentifierLoginVia(ConnectionPool.getConnection(databaseId), 
	        											 			 identifier, 
	        											 			 loginVia);
		        	
		        	if(testUser == null) {
		        		cLogger.warn("testUser is null even after inserting it's UserMeta in the database");
		        	}
		        	
		            WWALDSession.get().setUserMeta(testUser);
		            setResponsePage(HomePage.class);
		        } catch (Exception e) {
		        	String msg = "Could not redirect to Twitter for authentication";
		        	cLogger.error(msg, e);
		        	PageParameters parameters = new PageParameters();
		        	parameters.add(WicketIdConstants.MESSAGES, msg);
		            setResponsePage(GenericErrorPage.class);
		        }
			}

		};
	}
	
	private Image getTwitterImage() {
		String relImagePath = "images/twitter.png";
		ContextRelativeResource resource = null;
		try {
			resource = new ContextRelativeResource(relImagePath);
			resource.getResourceStream().getInputStream();
		} catch(ResourceStreamNotFoundException rsnfe) {
			resource = 
				new ContextRelativeResource(WWALDConstants.DEFAULT_COURSE_IMAGE_PATH);
		}
		return new Image(WicketIdConstants.LOGIN_WITH_TWITTER_IMG, resource);
	}
}
