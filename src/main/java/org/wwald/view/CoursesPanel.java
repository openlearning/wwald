package org.wwald.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
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
		
		String wikiContent = ((WWALDApplication)getApplication()).
									getDataFacade().
										retreiveCourseWiki(ConnectionPool.getConnection());
		CourseWikiParser parser = new CourseWikiParser();
		
		List<CourseTitlePair> tokens = parser.parse(wikiContent);
		
		List<Course> allCoursesToDisplay = new ArrayList<Course>();
		
		for(CourseTitlePair ctp : tokens) {
			Course course = ((WWALDApplication)getApplication()).
								getDataFacade().
									retreiveCourse(ConnectionPool.getConnection(),ctp.courseId);
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
							//TODO: Why can't we access dataFacade from HomePage?
							PageParameters pageParameters = getPage().getPageParameters();
							//TODO: Why would this ever be null?
							if(pageParameters == null) {
								pageParameters = new PageParameters();
							}
							try {
								WWALDApplication app = (WWALDApplication)(getApplication());
								app.getDataFacade().insertCourse(ConnectionPool.getConnection(), course);								
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
				}
				else {
					BookmarkablePageLink courseLink = new BookmarkablePageLink(WicketIdConstants.GOTO_COURSE, CoursePage.class);
					courseLink.setParameter(WWALDConstants.SELECTED_COURSE, course.getId());
					courseLink.add(new Label(WicketIdConstants.COURSE_TITLE, course.getTitle()));
					item.add(courseLink);
					item.add(new Label(WicketIdConstants.COURSE_DESCRIPTION, course.getDescription()));
				}
			}
    	};
	}

}
