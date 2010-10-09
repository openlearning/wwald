package org.wwald.view;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.Competency;
import org.wwald.model.Course;

public class CourseCompetenciesPanel extends Panel {
	
	public CourseCompetenciesPanel(final String id, 
								   final Course course, 
								   final Competency competency, 
								   final PageParameters parameters) {
		super(id);
		Link editCompetenciesLink = new Link(WicketIdConstants.COURSE_COMPETENCIES_EDIT) {
			@Override
			public void onClick() {
				setResponsePage(EditCompetencies.class, parameters);
			}
		};
		add(editCompetenciesLink);
		Link editLecture = new Link(WicketIdConstants.LECTURE_EDIT) {
			@Override
			public void onClick() {
				setResponsePage(EditLecture.class, parameters);
			}
		};
		add(editLecture);
		
		add(getCompetenciesListView(course, competency));
		add(new Label(WicketIdConstants.SELECTED_COURSE, course.getTitle()));
		add(new Label(WicketIdConstants.SELECTED_LECTURE, competency.getTitle()));
		add(new Label(WicketIdConstants.COMPETENCY_RESOURCES, competency.getResource()).setEscapeModelStrings(false));
		add(new Label(WicketIdConstants.COMPETENCY_DESCRIPTION, competency.getDescription()));
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
