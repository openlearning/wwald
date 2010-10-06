package org.wwald.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDApplication;
import org.wwald.model.Course;
import org.wwald.model.NonExistentCourse;
import org.wwald.model.StatusUpdate;

/**
 * Homepage
 */
public class HomePage extends BasePage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Logger cLogger = Logger.getLogger(HomePage.class);
	
	public static String SELECTED_COURSE = "course";
	public static String SELECTED_COURSE_TITLE = "title";
	public static String SELECTED_COMPETENCY = "competency";

	// TODO Add any page properties or variables here

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
    public HomePage(final PageParameters parameters) {
    	super(parameters);
    	add(new Link("courses.edit", null){

			@Override
			public void onClick() {
				setResponsePage(EditCourses.class);
			}
    		
    	});
    	add(getCoursesListView());
    	add(getStatusUpdateListView());
    }
    
    private ListView getCoursesListView() {
    	WWALDApplication app = (WWALDApplication)(getApplication());
    	List<Course> allCoursesToDisplay = app.getDataStore().getAllCoursesToDisplay(); 
    	return
    	new ListView("courses", allCoursesToDisplay) {

			@Override
			protected void populateItem(ListItem item) {
				final Course course = (Course)item.getModelObject();
				if(course instanceof NonExistentCourse) {
					Link courseLink = new Link("goto.course", null) {
						@Override
						public void onClick() {
							WWALDApplication app = (WWALDApplication)(getApplication());
							Course newCourse = app.getDataStore().createCourse(course);
							PageParameters pageParameters = new PageParameters();
							pageParameters.add(SELECTED_COURSE, newCourse.getId());
							setResponsePage(EditCompetencies.class, pageParameters);
						}
					};
					courseLink.add(new Label("course.title", course.getTitle()));
					item.add(courseLink);
					item.add(new Label("course.description", course.getDescription()));
				}
				else {
					BookmarkablePageLink courseLink = new BookmarkablePageLink("goto.course", CoursePage.class);
					courseLink.setParameter(SELECTED_COURSE, course.getId());
					courseLink.add(new Label("course.title", course.getTitle()));
					item.add(courseLink);
					item.add(new Label("course.description", course.getDescription()));
				}
			}
    	};
    }
        
    private ListView getStatusUpdateListView() {
    	return
    	new ListView("status_updates", getStatusUpdates()) {

			@Override
			protected void populateItem(ListItem item) {
				StatusUpdate statusUpdate = (StatusUpdate)item.getModelObject();
				item.add(new Label("status_update_text", statusUpdate.getText()));
			}
    		
    	};
    }
        
    private List<StatusUpdate> getStatusUpdates() {
    	List<StatusUpdate> statusUpdates = new ArrayList<StatusUpdate>();
    	statusUpdates.add(new StatusUpdate("Daniel learned HTML lists and blogged his learnings. "));
    	statusUpdates.add(new StatusUpdate("Parag took a quiz on Java programming. "));
    	statusUpdates.add(new StatusUpdate("Joe finished watching a lecture on sorting algorithms."));
    	return statusUpdates;
    }

	@Override
	public Panel getSidebar() {
		return new EmptyPanel("rhs_sidebar");
	}
}
