package org.wwald.view;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDConstants;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.Course;
import org.wwald.model.IDataFacade;
import org.wwald.model.NonExistentCourse;
import org.wwald.model.StatusUpdate;
import org.wwald.view.components.SimpleViewPageLink;

/**
 * Homepage
 */
public class HomePage extends BasePage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient IDataFacade dataFacade;
	
	private static Logger cLogger = Logger.getLogger(HomePage.class);

	// TODO Add any page properties or variables here

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
    public HomePage(final PageParameters parameters) {
    	super(parameters);
    	this.dataFacade = ((WWALDApplication)getApplication()).getDataFacade();
    	add(getEditCoursesLink());
    	add(getCoursesListView());
    	add(getStatusUpdateListView());
    }

	private Link getEditCoursesLink() {
		return new SimpleViewPageLink(WicketIdConstants.COURSES_EDIT, EditCourses.class);
	}
    
    private ListView getCoursesListView() {
    	List<Course> allCoursesToDisplay = this.dataFacade.retreiveCouresesListedInCourseWiki(); 
    	return
    	new ListView(WicketIdConstants.COURSES, allCoursesToDisplay) {

			@Override
			protected void populateItem(ListItem item) {
				final Course course = (Course)item.getModelObject();
				if(course instanceof NonExistentCourse) {
					Link courseLink = new Link(WicketIdConstants.GOTO_COURSE) {
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
        
    private ListView getStatusUpdateListView() {
    	return
    	new ListView(WicketIdConstants.STATUS_UPDATES, this.dataFacade.getStatusUpdates()) {

			@Override
			protected void populateItem(ListItem item) {
				StatusUpdate statusUpdate = (StatusUpdate)item.getModelObject();
				item.add(new Label(WicketIdConstants.STATUS_UPDATE_TEXT, statusUpdate.getText()));
			}
    		
    	};
    }
        
	@Override
	public Panel getSidebar() {
		return new EmptyPanel(WicketIdConstants.RHS_SIDEBAR);
	}
}
