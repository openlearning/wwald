package org.wwald.view.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Course;
import org.wwald.model.Role;
import org.wwald.model.UserCourseStatus;
import org.wwald.model.UserMeta;
import org.wwald.service.ApplicationException;
import org.wwald.service.ApplicationFacade;
import org.wwald.service.DataException;
import org.wwald.view.GenericErrorPage;

public class CourseStatusPanel extends Panel implements Serializable {
	
	private Course course;
	private UserMeta userMeta;
	private ICourseStatusForUser courseStatusForUser;
	
	//TODO: Make all inner classes static
	private abstract class ICourseStatusForUser implements Serializable {
		transient protected ApplicationFacade appFacade;
		ICourseStatusForUser() {
			WWALDApplication app = (WWALDApplication)getApplication();
			appFacade = app.getApplicationFacade();
		}
		abstract ListView getActionLinksListView();
	}
	
	private class UnenrolledCourseStatus extends ICourseStatusForUser {
		public ListView getActionLinksListView() {
			List<String> actions = new ArrayList<String>();
			actions.add("Enroll In Course");
			return new ListView(WicketIdConstants.COURSE_STATUS_ACTIONS, actions) {

				@Override
				protected void populateItem(ListItem item) {
					String action = (String)item.getModelObject();
					
					Link actionLink = 
						new AccessControlledViewPageLink(WicketIdConstants.COURSE_STATUS_ACTION_LINK, 
														 new Role[]{Role.STUDENT}) {
						public void onClick() {
							if(hasPermission()) {
								try {
									ServletWebRequest request = (ServletWebRequest)getRequest();
									String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
									String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
									appFacade.enrollInCourse(userMeta, course, ConnectionPool.getConnection(databaseId));
								} catch(ApplicationException ae) {
									String msg = "Sorry we could not enroll you in the course due to an internal error. " +
												 "We will look into this problem as soon as we can.";
									PageParameters parameters = getPage().getPageParameters();
									parameters.add(WicketIdConstants.MESSAGES, msg);
									setResponsePage(GenericErrorPage.class);
								}
							}
						}
					};
					
					Label actionLabel = new Label(WicketIdConstants.COURSE_STATUS_ACTION_LABEL, action);
					actionLink.add(actionLabel);
					item.add(actionLink);
				}
				
			};
		}
	}
	
	private class EnrolledCourseStatus extends ICourseStatusForUser {
		public ListView getActionLinksListView() {
			List<String> actions = new ArrayList<String>();
			actions.add("Drop Course");
			return new ListView(WicketIdConstants.COURSE_STATUS_ACTIONS, actions) {

				@Override
				protected void populateItem(ListItem item) {
					String action = (String)item.getModelObject();
					
					Link actionLink = 
						new AccessControlledViewPageLink(WicketIdConstants.COURSE_STATUS_ACTION_LINK, 
														 new Role[]{Role.STUDENT}) {
						@Override
						public void onClick() {
							if(hasPermission()) {
								try {
									ServletWebRequest request = (ServletWebRequest)getRequest();
									String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
									String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
									appFacade.dropCourse(userMeta, course, ConnectionPool.getConnection(databaseId));
								} catch(ApplicationException ae) {
									String msg = "Sorry we could not drop you from the course due to an internal error. " +
									 			 "We will look into this problem as soon as we can.";
									PageParameters parameters = getPage().getPageParameters();
									parameters.add(WicketIdConstants.MESSAGES, msg);
									setResponsePage(GenericErrorPage.class);
								}
							}
						}
					};
					
					Label actionLabel = new Label(WicketIdConstants.COURSE_STATUS_ACTION_LABEL, action);
					actionLink.add(actionLabel);
					item.add(actionLink);
				}
				
			};
		}
	}
	public CourseStatusPanel(String id, Course course) {
		super(id);
		this.course = course;
		init();
	}
	
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		//TODO: This is a really bad way of ensuring that the new course 
		//enrollment status is reflected in the page after a user has 
		//changed it by clicking on the link
		remove(WicketIdConstants.COURSE_STATUS_ACTIONS);
		init();
	}


	private void init() {
		this.userMeta = WWALDSession.get().getUserMeta();
		if(this.userMeta != null) {
			
		try {
			//TODO: What do we do if this is null... should we introduce another course status?
			this.courseStatusForUser = getCourseStatusForUser();
		} catch (DataException e) {
			//Exception should have been logged in the Data layer
			//not doing anything here
		}
			
			add(this.courseStatusForUser.getActionLinksListView());
		}
	}

	private ICourseStatusForUser getCourseStatusForUser() throws DataException {
		ICourseStatusForUser retVal = null;
		WWALDApplication app = (WWALDApplication)getApplication();
		ApplicationFacade appFacade = app.getApplicationFacade();
		String databaseId = ConnectionPool.getDatabaseIdFromRequest((ServletWebRequest)getRequest());
		UserCourseStatus userCourseStatus = appFacade.getUserCourseStatus(userMeta, course, ConnectionPool.getConnection(databaseId));
		//TODO: Can we use a map here instead of multiple if...else???
		if(UserCourseStatus.UNENROLLED.equals(userCourseStatus) || 
		   UserCourseStatus.DROPPED.equals(userCourseStatus)) {
			retVal = new UnenrolledCourseStatus();
		}
		else if(UserCourseStatus.ENROLLED.equals(userCourseStatus)) {
			retVal = new EnrolledCourseStatus();
		}
		return retVal;
	}
	
	
	
}
