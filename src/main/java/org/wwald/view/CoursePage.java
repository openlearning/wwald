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
import org.wwald.model.DataStore;

public class CoursePage extends WebPage{
	public CoursePage(final PageParameters parameters) {
		Course selectedCourse = getSelectedCourse(parameters);
		add(getCompetenciesListView(selectedCourse.getCompetencies()));
		add(new Label("selected.course", "Introduction to computers and the Internet"));
		add(new Label("selected.lecture", "Hardware - Part 1"));
		add(new Label("mentor.name", selectedCourse.getMentor().getName()));
		add(new Label("mentor.qanswered", selectedCourse.getMentor().getQuestionsAnswered()));
		add(new Label("mentor.lastlogin", selectedCourse.getMentor().getLastLogin()));
    }
	
	private Course getSelectedCourse(PageParameters parameters) {
		WWALDApplication app = (WWALDApplication)getApplication();
		DataStore dataStore = app.getDataStore();
		return dataStore.getCourse(parameters.getString(HomePage1.SELECTED_COURSE));
	}
	
	private ListView getCompetenciesListView(List<Competency> competencies) {
		return new ListView("competencies", competencies) {

			@Override
			protected void populateItem(ListItem item) {
				Competency competency = (Competency)item.getModelObject();
				BookmarkablePageLink competencyLink = new BookmarkablePageLink("goto.competency", CoursePage.class);
				competencyLink.add(new Label("competency.title",competency.getTitle()));
				item.add(competencyLink);
			}
			
		};
	}
}
