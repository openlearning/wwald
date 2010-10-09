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
import org.wwald.model.Competency;
import org.wwald.model.Course;

public class CourseCompetenciesPanel extends Panel {
	
	public CourseCompetenciesPanel(final String id, 
								   final Course course, 
								   final Competency competency, 
								   final PageParameters parameters) {
		super(id);
		Link editCompetenciesLink = new Link("competencies.edit") {
			@Override
			public void onClick() {
				setResponsePage(EditCompetencies.class, parameters);
			}
		};
		add(editCompetenciesLink);
		Link editLecture = new Link("lecture.edit") {
			@Override
			public void onClick() {
				setResponsePage(EditLecture.class, parameters);
			}
		};
		add(editLecture);
		
		add(getCompetenciesListView(course, competency));
		add(new Label("selected.course", course.getTitle()));
		add(new Label("selected.lecture", competency.getTitle()));
		add(new Label("competency.resources", competency.getResource()).setEscapeModelStrings(false));
		add(new Label("competency.description", competency.getDescription()));
	}
	
	private ListView getCompetenciesListView(final Course selectedCourse, final Competency selectedCompetency) {
		return new ListView("competencies", selectedCourse.getCompetencies()) {

			@Override
			protected void populateItem(ListItem item) {
				Competency competency = (Competency)item.getModelObject();
				if(selectedCompetency.equals(competency)) {
					item.add(new SimpleAttributeModifier("class", "selected_lecture"));
				}
				PageParameters pars = new PageParameters();
				pars.add(WWALDConstants.SELECTED_COURSE, selectedCourse.getId());
				pars.add(WWALDConstants.SELECTED_COMPETENCY, String.valueOf(competency.getId()));
				BookmarkablePageLink competencyLink = new BookmarkablePageLink("goto.competency", CoursePage.class, pars);				
				competencyLink.add(new Label("competency.title",competency.getTitle()));
				item.add(competencyLink);
			}
			
		};
	}
	
}
