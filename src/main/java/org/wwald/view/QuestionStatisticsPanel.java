package org.wwald.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.QuestionStatistics;
import org.wwald.model.UserMeta;

public class QuestionStatisticsPanel extends BasePanel {
	
	private static final transient Logger cLogger = 
		Logger.getLogger(QuestionStatisticsPanel.class);
	
	public QuestionStatisticsPanel(String id, 
								   QuestionStatistics questionStatistics) {

		super(id);
		
		try {
			//4890999
			add(getUserImage(questionStatistics.getQuestioner().getUserid()));
			add(getQuestionerLink(questionStatistics));
			add(getQuestionTimestamp(questionStatistics.getTimestamp()));
			add(getNumberOfAnswers(questionStatistics));
//			add(getLastAnswererLink());
//			add(getLikes());
//			add(getTags());
		} catch(Exception e) {
			String msg = "Exception caught while showing QuestionStatisticsPanel";
			cLogger.error(msg, e);
			setResponsePage(GenericErrorPage.class);
		}
	}
	
	public void setUserImageVisible(boolean visible) {
		Component image = get(WWALDApplication.USER_THUMBNAIL_IMAGE);
		if(image != null) {
			image.setVisible(visible);
		} else {
			String msg = "Could not get image component for setting visibility";
			cLogger.warn(msg);
		}
	}

	private Component getQuestionTimestamp(long timestamp) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern("yyyy-MM-dd HH:mm");
		String displayTimestamp = "unknown";
		if(timestamp != -1) {
			displayTimestamp = dateFormat.format(new Date(timestamp)); 
		}
		return new Label("question_timestamp", 
						 "asked on " + displayTimestamp);
	}

	private Component getQuestionerLink(QuestionStatistics questionStatistics) {
		UserMeta user = questionStatistics.getQuestioner();
		PageParameters parameters = new PageParameters();
		parameters.add(WWALDConstants.USERID, String.valueOf(user.getUserid()));
		
		Link userLink = new BookmarkablePageLink(WicketIdConstants.PUBLIC_USER_PROFILE, 
												 UserProfiles.class,
												 parameters);
		
		Label label = 
			new Label(WicketIdConstants.PUBLIC_USER_PROFILE_LABEL, 
					  user.getIdentifier());
		userLink.add(label);
		
		return userLink;
	}

	private Component getNumberOfAnswers(QuestionStatistics questionStatistics) {
		return new Label("number_of_answers", 
						 String.valueOf(questionStatistics.getNumberOfAnswers()));
	}

	private Component getLastAnswererLink() {
		// TODO Auto-generated method stub
		return null;
	}

	private Component getLikes() {
		// TODO Auto-generated method stub
		return null;
	}

	private Component getTags() {
		// TODO Auto-generated method stub
		return null;
	}

}
