package org.wwald.view;

import java.util.List;

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
import org.wwald.model.Course;
import org.wwald.model.NonExistentCourse;
import org.wwald.model.Role;
import org.wwald.view.components.SimpleViewPageLink;

public class CoursesPanel extends Panel {
	
	public CoursesPanel(String id) {
		super(id);
		add(getCoursesListView());
	}
	
	private ListView getCoursesListView() {
    	List<Course> allCoursesToDisplay =  ((WWALDApplication)getApplication()).getDataFacade().retreiveCouresesListedInCourseWiki(); 
    	return
    	new ListView(WicketIdConstants.COURSES, allCoursesToDisplay) {

			@Override
			protected void populateItem(ListItem item) {
				final Course course = (Course)item.getModelObject();
				if(course instanceof NonExistentCourse) {
					Link courseLink = new SimpleViewPageLink(WicketIdConstants.GOTO_COURSE, 
															 new Role[]{Role.ADMIN}) {
						@Override
						public void onClick() {
							//TODO: Why can't we access dataFacade from HomePage?
							WWALDApplication app = (WWALDApplication)(getApplication());
							app.getDataFacade().insertCourse(course);
							PageParameters pageParameters = new PageParameters();
							pageParameters.add(WWALDConstants.SELECTED_COURSE, course.getId());
							setResponsePage(EditCompetencies.class, pageParameters);
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
