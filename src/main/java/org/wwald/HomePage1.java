package org.wwald;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
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

        // Add the simplest type of label
    	add(new Label("course1.title", "Understanding Computers And The Internet"));
    	add(new Label("course1.description", "This course is all about understanding: understanding what's going on inside your computer when you flip on the switch, why tech support has you constantly rebooting your computer, how everything you do on the Internet can be watched by ..."));
    	
    	add(new Label("course2.title", "Introduction To Computer Science"));
    	add(new Label("course2.description", "Introduction to Computer Science I is a first course in computer science at Harvard College for concentrators and non-concentrators alike. More than just teach you how to program, this course teaches you how to think more methodically and how to ..."));
    	
    	add(new Label("course3.title", "Introduction to Computer Science and Programming (using Python)"));
    	add(new Label("course3.description", "This subject is aimed at students with little or no programming experience. It aims to provide students with an understanding of the role computation can play in solving problems. It also aims to help students, regardless of their major, to ..."));

        // TODO Add your page's components here
    }
}
