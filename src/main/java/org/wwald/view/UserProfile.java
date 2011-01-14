package org.wwald.view;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.value.ValueMap;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

/**
 * Displays the public UserProfile of a user
 * This includes the name, enrolled courses
 */
public class UserProfile extends BasePage {

	private static final Logger cLogger = Logger.getLogger(UserProfile.class);
	public UserProfile(PageParameters parameters) {
		super(parameters);
		try {
			UserMeta userMeta = retreiveUserMeta(parameters);
			String identifier = userMeta.getIdentifier();
			add(getUserThumbnailImage(userMeta));
			add(new Label(WicketIdConstants.PUBLIC_USER_PROFILE_IDENTIFIER, 
						  identifier));
			add(getEnrolledCoursesListView(userMeta));
		} catch(Exception e) {
			cLogger.error("Caught Exception", e);
			setResponsePage(GenericErrorPage.class);
		}
	}
	
	private Component getUserThumbnailImage(UserMeta userMeta) {
		String dbId = getDatabaseId();
		String userid = String.valueOf(userMeta.getUserid());
		ValueMap vm = new ValueMap();
		vm.put("userid", userid);
		vm.put("dbId", dbId);
		return new Image(WWALDApplication.USER_THUMBNAIL_IMAGE, 
						 new ResourceReference(WWALDApplication.USER_THUMBNAIL_IMAGE), 
						 vm);
	}

	private Component getEnrolledCoursesListView(UserMeta userMeta) 
		throws DataException {
		
		Connection conn = ConnectionPool.getConnection(getDatabaseId());
		IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
		List<String> courseIds = 
			dataFacade.retreiveCourseEnrollmentsForUser(conn, 
														userMeta);
		return new ListView(WicketIdConstants.ENROLLED_COURSES, courseIds) {

			@Override
			protected void populateItem(ListItem item) {
				String courseId = (String)item.getModelObject();
				PageParameters pageParams = new PageParameters();
				pageParams.add(WWALDConstants.SELECTED_COURSE, courseId);
				Link enrolledCourseLink = 
					new BookmarkablePageLink(WicketIdConstants.ENROLLED_COURSE_LINK, 
											 CoursePage.class, 
											 pageParams);
				Label enrolledCourseLinkLabel = 
					new Label(WicketIdConstants.ENROLLED_COURSE_LINK_LABEL, 
							  courseId);
				enrolledCourseLink.add(enrolledCourseLinkLabel);
				
				item.add(enrolledCourseLink);
			}
			
		};
	}

	private UserMeta retreiveUserMeta(PageParameters parameters) 
		throws NumberFormatException, DataException {
		
		UserMeta retVal = null;
		String userid = parameters.getString(WWALDConstants.USERID);
		if(userid != null) {
			try {
				IDataFacade dataFacade = ((WWALDApplication)Application.get()).getDataFacade();
				Connection conn = ConnectionPool.getConnection(getDatabaseId());
				retVal = dataFacade.retreiveUserMeta(conn, Integer.parseInt(userid));
			} catch(NumberFormatException nfe) {
				cLogger.error("Cannot fetch UserMeta for incorrect userid '" + userid + "'");
			}
		}
		return retVal;
	}



}
