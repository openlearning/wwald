package org.wwald.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.resource.ContextRelativeResource;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Course;
import org.wwald.model.NonExistentCourse;
import org.wwald.model.Role;
import org.wwald.service.DataException;
import org.wwald.util.CourseWikiParser;
import org.wwald.util.ParseException;
import org.wwald.util.CourseWikiParser.CourseTitlePair;
import org.wwald.view.components.AccessControlledViewPageLink;

public class CoursesPanel extends Panel {
	private static final Logger cLogger = Logger.getLogger(CoursesPanel.class);
	
	public CoursesPanel(String id) throws DataException, ParseException {
		super(id);
		add(getCoursesListView());
	}
	
	private ListView getCoursesListView() throws DataException, ParseException {
		ServletWebRequest request = (ServletWebRequest)getRequest();
		String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
		String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
		String wikiContent = ((WWALDApplication)getApplication()).
									getDataFacade().
										retreiveCourseWiki(ConnectionPool.getConnection(databaseId));
		CourseWikiParser parser = new CourseWikiParser();
		
		List<CourseTitlePair> tokens = parser.parse(wikiContent);
		
		List<Course> allCoursesToDisplay = new ArrayList<Course>();
		
		for(CourseTitlePair ctp : tokens) {
			Course course = ((WWALDApplication)getApplication()).
								getDataFacade().
									retreiveCourse(ConnectionPool.getConnection(databaseId),ctp.courseId);
			if(course != null) {
				allCoursesToDisplay.add(course);
			}
			else {
				allCoursesToDisplay.add(new NonExistentCourse(ctp.courseId, ctp.courseTitle));
			}
		}
		
		 
		
    	return getCoursesListView(allCoursesToDisplay);
    }

	private ListView getCoursesListView(List<Course> allCoursesToDisplay) {
		return
    	new ListView(WicketIdConstants.COURSES, allCoursesToDisplay) {

			@Override
			protected void populateItem(ListItem item) {
				final Course course = (Course)item.getModelObject();
				if(course instanceof NonExistentCourse) {
					Link courseLink = new AccessControlledViewPageLink(WicketIdConstants.GOTO_COURSE, 
															 new Role[]{Role.ADMIN}) {
						@Override
						public void onClick() {
							ServletWebRequest request = (ServletWebRequest)getRequest();
							String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
							String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
							//TODO: Why can't we access dataFacade from HomePage?
							PageParameters pageParameters = getPage().getPageParameters();
							//TODO: Why would this ever be null?
							if(pageParameters == null) {
								pageParameters = new PageParameters();
							}
							try {
								WWALDApplication app = (WWALDApplication)(getApplication());
								app.getDataFacade().insertCourse(ConnectionPool.getConnection(databaseId), course);								
								pageParameters.add(WWALDConstants.SELECTED_COURSE, course.getId());
								setResponsePage(EditCompetencies.class, pageParameters);
							} catch(DataException de) {
								String msg = "Could not insert course " + course;
								cLogger.error(msg, de);
								pageParameters.add(WicketIdConstants.MESSAGES, msg);
							}							
						}
					};
					courseLink.add(new Label(WicketIdConstants.COURSE_TITLE, course.getTitle()));
					item.add(courseLink);
					item.add(new Label(WicketIdConstants.COURSE_DESCRIPTION, course.getDescription()));
					item.add(getCourseImage(course.getId()));
				}
				else {
					BookmarkablePageLink courseLink = new BookmarkablePageLink(WicketIdConstants.GOTO_COURSE, CoursePage.class);
					courseLink.setParameter(WWALDConstants.SELECTED_COURSE, course.getId());
					courseLink.add(new Label(WicketIdConstants.COURSE_TITLE, course.getTitle()));
					item.add(courseLink);
					
					WWALDApplication app = (WWALDApplication)Application.get();
					String description = app.getMarkDown().transform(course.getShortDescription());
					item.add(getCourseImage(course.getId()));
					item.add(new Label(WicketIdConstants.COURSE_DESCRIPTION, description).setEscapeModelStrings(false));
				}
			}			
    	};
	}
	
	private Component getCourseImage(String id) {
		String relImagePath = "images/" + id + ".png";
		ContextRelativeResource resource = null;
		try {
			resource = new ContextRelativeResource(relImagePath);
			resource.getResourceStream().getInputStream();
		} catch(ResourceStreamNotFoundException rsnfe) {
			resource = 
				new ContextRelativeResource(WWALDConstants.DEFAULT_COURSE_IMAGE_PATH);
		}
		return new Image("course_image", resource);
	}
	
	private String shorten(String str) {
		if(str == null) {
			return str;
		}
		if(str.length() > 512) {
			return str.substring(0, 512)+"...";
		}
		else {
			return str;
		}
	}
}
