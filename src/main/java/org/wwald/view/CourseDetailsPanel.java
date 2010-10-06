package org.wwald.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.model.Mentor;

public class CourseDetailsPanel extends Panel {

	public CourseDetailsPanel(Mentor mentor, String id) {
		super(id);
		if(mentor != null) {
			add(new Label("mentor.name", mentor.getFirstName() + " " + mentor.getMiddleInitial() + " " + mentor.getLastName()));
			add(new Label("mentor.qanswered", "7 xxx"));
			add(new Label("mentor.lastlogin", "some date"));
		}
		else {
			add(new Label("mentor.name", ""));
			add(new Label("mentor.qanswered", "7 xxx"));
			add(new Label("mentor.lastlogin", "some date"));
		}
	}

}
