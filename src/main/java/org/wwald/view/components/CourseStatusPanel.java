package org.wwald.view.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.ApplicationFacade;
import org.wwald.model.Course;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.model.UserCourseStatus;
import org.wwald.view.CoursePage;

public class CourseStatusPanel extends Panel implements Serializable {
	
	private Course course;
	private User user;
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
								appFacade.enrollInCourse(user, course);
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
								appFacade.dropCourse(user, course);						
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
		this.user = WWALDSession.get().getUser();
		if(this.user != null) {
			this.courseStatusForUser = getCourseStatusForUser();
			add(this.courseStatusForUser.getActionLinksListView());
		}
	}

	private ICourseStatusForUser getCourseStatusForUser() {
		ICourseStatusForUser retVal = null;
		WWALDApplication app = (WWALDApplication)getApplication();
		ApplicationFacade appFacade = app.getApplicationFacade();
		UserCourseStatus userCourseStatus = appFacade.getUserCourseStatus(user, course);
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
