package org.wwald;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.WebPage;

/**
 * Homepage
 */
public class HomePage1 extends WebPage {

	private static final long serialVersionUID = 1L;
	
	public static String SELECTED_COURSE = "selected.course";

	// TODO Add any page properties or variables here

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
    public HomePage1(final PageParameters parameters) {
    	add(getCoursesListView());
    	add(getStatusUpdateListView());
    }
    
    private ListView getCoursesListView() {
    	WWALDApplication app = (WWALDApplication)(getApplication());
    	return
    	new ListView("courses", app.getDataStore().getAllCourses()) {

			@Override
			protected void populateItem(ListItem item) {
				Course course = (Course)item.getModelObject();
				PageParameters pars = new PageParameters();
				pars.add(SELECTED_COURSE, course.getId());
				BookmarkablePageLink courseLink = new BookmarkablePageLink("goto.course", CoursePage.class, pars);
				courseLink.add(new Label("course.title", course.getTitle()));
				item.add(courseLink);
				item.add(new Label("course.description", course.getDescription()));
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
}
