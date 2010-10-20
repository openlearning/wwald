package org.wwald.view;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDConstants;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.Competency;
import org.wwald.model.Course;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.view.components.AccessControlledViewPageLink;
import org.wwald.view.components.CourseStatusPanel;
import org.wwald.view.components.SimpleViewPageLink;

public class CourseCompetenciesPanel extends Panel {
	
	public CourseCompetenciesPanel(final String id, 
								   final Course course, 
								   final Competency competency, 
								   final PageParameters parameters) {
		super(id);
		Link editCompetenciesLink = new AccessControlledViewPageLink(WicketIdConstants.COURSE_COMPETENCIES_EDIT, 
														   new Role[]{Role.ADMIN}) {
			@Override
			public void onClick() {
				setResponsePage(EditCompetencies.class, parameters);
			}
		};
		add(editCompetenciesLink);
		Link editLecture = new AccessControlledViewPageLink(WicketIdConstants.LECTURE_EDIT,
												  new Role[]{Role.ADMIN}) {
			@Override
			public void onClick() {
				setResponsePage(EditLecture.class, parameters);
			}
		};
		add(editLecture);
		
		add(getCompetenciesListView(course, competency));
		add(new Label(WicketIdConstants.SELECTED_COURSE, course.getTitle())); 
			
		add(getCourseStatusPanel(course));
		
		add(new Label(WicketIdConstants.SELECTED_LECTURE, competency.getTitle()));
		//TODO: Can we use something other than labels out here
		add(new Label(WicketIdConstants.COMPETENCY_RESOURCES, competency.getTransformedResources()).setEscapeModelStrings(false));
		add(new Label(WicketIdConstants.COMPETENCY_DESCRIPTION, competency.getTranformedDescription()).setEscapeModelStrings(false));
	}
	
	private Component getCourseStatusPanel(Course course) {
		User user = WWALDSession.get().getUser();
		Panel panel = null;
		if(user == null) {
			panel = new EmptyPanel(WicketIdConstants.COURSE_STATUS_PANEL);
		}
		else {
			panel = new CourseStatusPanel(WicketIdConstants.COURSE_STATUS_PANEL, course);
		}
		return panel;
	}

	private ListView getCompetenciesListView(final Course selectedCourse, final Competency selectedCompetency) {
		return new ListView(WicketIdConstants.COMPETENCIES, selectedCourse.getCompetencies()) {

			@Override
			protected void populateItem(ListItem item) {
				Competency competency = (Competency)item.getModelObject();
				if(selectedCompetency.equals(competency)) {
					item.add(new SimpleAttributeModifier("class", WicketIdConstants.SELECTED_LECTURE_CLASS));
				}
				PageParameters pars = new PageParameters();
				pars.add(WWALDConstants.SELECTED_COURSE, selectedCourse.getId());
				pars.add(WWALDConstants.SELECTED_COMPETENCY, String.valueOf(competency.getId()));
				BookmarkablePageLink competencyLink = new BookmarkablePageLink(WicketIdConstants.GOTO_COMPETENCY, CoursePage.class, pars);				
				competencyLink.add(new Label(WicketIdConstants.COMPETENCY_TITLE,competency.getTitle()));
				item.add(competencyLink);
			}
			
		};
	}
	
}
