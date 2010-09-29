package org.wwald.view;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.wwald.WWALDApplication;
import org.wwald.model.Competency;
import org.wwald.model.Course;
import org.wwald.model.DataFacade;
import org.wwald.model.Mentor;

public class CoursePage extends WebPage{
	public CoursePage(final PageParameters parameters) {
		Course selectedCourse = getSelectedCourse(parameters);
		Competency selectedCompetency = getSelectedCompetency(parameters,selectedCourse);
		add(getCompetenciesListView(selectedCourse.getCompetencies(), parameters.getString(HomePage1.SELECTED_COURSE)));
		add(new Label("selected.course", selectedCourse.getTitle()));
		add(new Label("selected.lecture", selectedCompetency.getTitle()));
		add(new Label("competency.resources", selectedCompetency.getResource()).setEscapeModelStrings(false));
		add(new Label("competency.description", selectedCompetency.getDescription()));
		Mentor mentor = selectedCourse.getMentor();
		add(new Label("mentor.name", mentor.getFirstName() + " " + mentor.getMiddleInitial() + " " + mentor.getLastName()));
		add(new Label("mentor.qanswered", "7 xxx"));
		add(new Label("mentor.lastlogin", "some date"));
    }
	
	private Course getSelectedCourse(PageParameters parameters) {
		WWALDApplication app = (WWALDApplication)getApplication();
		DataFacade dataStore = app.getDataStore();
		String selectedCourseId = parameters.getString(HomePage1.SELECTED_COURSE);
		return dataStore.getCourse(selectedCourseId);
	}
	
	private Competency getSelectedCompetency(PageParameters parameters, Course selectedCourse) {
		if(parameters == null) {
			throw new NullPointerException("PageParameters cannot be null");
		}
		if(selectedCourse == null) {
			throw new NullPointerException("selectedCourse cannot be null");
		}
		String selectedCompetencyId = parameters.getString(HomePage1.SELECTED_COMPETENCY);
		Competency competency = null; 
		if(selectedCompetencyId == null) {
			List<Competency> competencies = selectedCourse.getCompetencies();
			if(competencies != null && competencies.size() > 0) {
				competency = competencies.get(0);
			}
		}
		else {
			competency = selectedCourse.getCompetency(selectedCompetencyId);
		}
		
		if(competency == null) {
			competency = Competency.createBlankCompeteny();
		}
		return competency;
	}
	
	private ListView getCompetenciesListView(List<Competency> competencies, final String courseId) {
		return new ListView("competencies", competencies) {

			@Override
			protected void populateItem(ListItem item) {
				Competency competency = (Competency)item.getModelObject();
				PageParameters pars = new PageParameters();
				pars.add(HomePage1.SELECTED_COURSE, courseId);
				pars.add(HomePage1.SELECTED_COMPETENCY, competency.getId());
				BookmarkablePageLink competencyLink = new BookmarkablePageLink("goto.competency", CoursePage.class, pars);				
				competencyLink.add(new Label("competency.title",competency.getTitle()));
				item.add(competencyLink);
			}
			
		};
	}
}
