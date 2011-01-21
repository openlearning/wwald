package org.wwald.view.components;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.Model;
import org.wwald.model.UserMeta;

import twitter4j.ProfileImage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class UserImage extends WebComponent {
	
	public static final String SERVICED_TAG = "img";
	private static final String DEFAULT_IMAGE_URL = "/images/goodsamaritan.jpg";
	private transient static final Logger cLogger = Logger.getLogger(UserImage.class);
	
	public UserImage(String id, UserMeta userMeta) {
		super(id);
		
		if(userMeta == null) {
			throw new NullPointerException("userMeta cannot be null");
		}
		
		Model<String> model = new Model<String>(DEFAULT_IMAGE_URL);
		
		if(UserMeta.LoginVia.TWITTER.equals(userMeta.getLoginVia())) {
			try {
				Twitter twitter = (new TwitterFactory()).getInstance();
				model = 
					new Model<String>(twitter.getProfileImage(userMeta.getIdentifier(), 
															  ProfileImage.MINI).
															  	getURL());
			} catch(TwitterException te) {
				String msg = "Could not fetch user image for '" + 
							 userMeta.getIdentifier() + 
							 "' using default image";
				cLogger.warn(msg, te);
			}
		}
		
		setDefaultModel(model);
	}	
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		checkComponentTag(tag, SERVICED_TAG);
		tag.put("src", getDefaultModelObjectAsString());
	}
}
