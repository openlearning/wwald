package org.wwald;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
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

    	add(new ListView("courses", getCourses()) {

			@Override
			protected void populateItem(ListItem item) {
				Course course = (Course)item.getModelObject();
				Link courseLink = new Link("goto.course", item.getModel()) {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						
					}
				};
				courseLink.add(new Label("course.title", course.getTitle()));
				item.add(courseLink);
				item.add(new Label("course.description", course.getDescription()));
			}
    		
    	});
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
}
