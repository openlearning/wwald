package org.wwald.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.model.Mentor;

public class CourseDetailsPanel extends Panel {

	public CourseDetailsPanel(String id, BasePage viewPage) {
		super(id);
		if(viewPage instanceof CoursePage) {
			CoursePage coursePage = (CoursePage)viewPage;
			Mentor mentor = coursePage.getMentor();
			if(mentor != null) {
				add(new Label("mentor.name", mentor.getFirstName() + " " + mentor.getLastName()));
				add(new Label("mentor.qanswered", ""));
				add(new Label("mentor.lastlogin", ""));
			}
			else {
				addBlankMentorDetails();
			}
		}
		else {
			addBlankMentorDetails();
		}
	}

	private void addBlankMentorDetails() {
		add(new Label("mentor.name", ""));
		add(new Label("mentor.qanswered", "7 xxx"));
		add(new Label("mentor.lastlogin", "some date"));
	}

}
