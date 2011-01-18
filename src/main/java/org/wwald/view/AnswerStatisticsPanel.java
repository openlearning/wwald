package org.wwald.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.AnswerStatistics;
import org.wwald.model.UserMeta;

public class AnswerStatisticsPanel extends BasePanel {

	public AnswerStatisticsPanel(String id, AnswerStatistics answerStatistics) {
		super(id);
		add(getUserImage(answerStatistics.getUser().getUserid()));
		add(getUser(answerStatistics));
		add(getAnswerTimestamp(answerStatistics.getTimestamp()));
		//add(getLikes(answerStatistics));
	}

	private Component getAnswerTimestamp(long timestamp) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern("yyyy-MM-dd HH:mm");
		String displayTimestamp = "unknown";
		if(timestamp != -1) {
			displayTimestamp = dateFormat.format(new Date(timestamp)); 
		}
		return new Label("answer_timestamp", 
						 "answered on " + displayTimestamp);
	}

	private Component getUser(AnswerStatistics answerStatistics) {
		UserMeta user = answerStatistics.getUser();
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
	
	private Component getLikes(AnswerStatistics answerStatistics) {
		return null;
	}

}
