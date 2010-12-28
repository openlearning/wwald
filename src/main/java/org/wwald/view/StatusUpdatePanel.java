package org.wwald.view;

import java.text.SimpleDateFormat;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.StatusUpdate;
import org.wwald.model.UserMeta;

/**
 * This {@link Panel} contains details of one status update
 * @author pshah
 *
 */
public class StatusUpdatePanel extends Panel {

	public StatusUpdatePanel(String id, StatusUpdate statusUpdate) {
		super(id);
		
		//add Date for status update
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern("yyyy-MM-dd");
		Label dateLabel = new Label(WicketIdConstants.STATUS_UPDATE_DATE, 
									dateFormat.format(statusUpdate.getTimestamp())); 
		add(dateLabel);
		
		//add link to user responsible for status update
		UserMeta userMeta = statusUpdate.getUserMeta();
		PageParameters userLinkPageParams = new PageParameters();
		userLinkPageParams.add(WWALDConstants.USERID, 
							   String.valueOf(userMeta.getUserid()));
		Link userLink = 
			new BookmarkablePageLink(WicketIdConstants.STATUS_UPDATE_USER_LINK, 
									 UserProfiles.class, 
									 userLinkPageParams);
		Label usernameLabel = new Label(WicketIdConstants.STATUS_UPDATE_USER, 
										userMeta.getIdentifier());
		userLink.add(usernameLabel);
		add(userLink);
		
		//add text of the status update
		Label statusUpdateText = new Label(WicketIdConstants.STATUS_UPDATE_TEXT, 
										   statusUpdate.getShortText());
		add(statusUpdateText);
		
		//add the course for which the status update has happened
		String courseId = statusUpdate.getCourseId();
		PageParameters coursePageParams = new PageParameters();
		coursePageParams.add(WWALDConstants.SELECTED_COURSE, courseId);
		Link courseLink = 
			new BookmarkablePageLink(WicketIdConstants.STATUS_UPDATE_COURSE_LINK, 
									 CoursePage.class, 
									 coursePageParams);
		Label statusUpdateCourseId = 
			new Label(WicketIdConstants.STATUS_UPDATE_COURSE_LABEL, courseId);
		courseLink.add(statusUpdateCourseId);
		add(courseLink);
	}

}
