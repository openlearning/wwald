package org.wwald;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
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
    	return
    	new ListView("courses", getCourses()) {

			@Override
			protected void populateItem(ListItem item) {
				Course course = (Course)item.getModelObject();
				BookmarkablePageLink courseLink = new BookmarkablePageLink("goto.course", CoursePage.class);
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
    
    private List<Course> getCourses() {
    	List<Course> courses = new ArrayList<Course>();
    	
    	courses.add(new Course("Understanding Computers And The Internet", 
    						   "This course is all about understanding: understanding what's going on inside your computer when you flip on the switch, why tech support has you constantly rebooting your computer, how everything you do on the Internet can be watched by ..."));

    	courses.add(new Course("Introduction To Computer Science", 
    						   "Introduction to Computer Science I is a first course in computer science at Harvard College for concentrators and non-concentrators alike. More than just teach you how to program, this course teaches you how to think more methodically and how to ..."));

    	courses.add(new Course("Introduction to Computer Science and Programming (using Python)", 
    						   "This subject is aimed at students with little or no programming experience. It aims to provide students with an understanding of the role computation can play in solving problems. It also aims to help students, regardless of their major, to ..."));
    	
    	return courses;
    }
    
    private List<StatusUpdate> getStatusUpdates() {
    	List<StatusUpdate> statusUpdates = new ArrayList<StatusUpdate>();
    	statusUpdates.add(new StatusUpdate("Daniel learned HTML lists and blogged his learnings. "));
    	statusUpdates.add(new StatusUpdate("Parag took a quiz on Java programming. "));
    	statusUpdates.add(new StatusUpdate("Joe finished watching a lecture on sorting algorithms."));
    	return statusUpdates;
    }
}
