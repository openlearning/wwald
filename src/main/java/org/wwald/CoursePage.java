package org.wwald;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

public class CoursePage extends WebPage{
	public CoursePage(final PageParameters parameters) {
		add(getCompetenciesListView());
		add(new Label("selected.lecture", "Hardware - Part 1"));
		Mentor mentor = getMentor();
		add(new Label("mentor.name", mentor.getName()));
		add(new Label("mentor.qanswered", mentor.getQuestionsAnswered()));
		add(new Label("mentor.lastlogin", mentor.getLastLogin()));
    }
	
	private Mentor getMentor() {
		return new Mentor("David J. Malan", "7", "7/11/2010");
	}

	private ListView getCompetenciesListView() {
		return new ListView("competencies", getCompetencies()) {

			@Override
			protected void populateItem(ListItem item) {
				Competency competency = (Competency)item.getModelObject();
				BookmarkablePageLink competencyLink = new BookmarkablePageLink("goto.competency", CoursePage.class);
				competencyLink.add(new Label("competency.title",competency.getTitle()));
				item.add(competencyLink);
			}
			
		};
	}

	private List<Competency> getCompetencies() {
		List<Competency> competencies = new ArrayList<Competency>();
		competencies.add(new Competency("Hardware - Part 1", "Describes the basics of how computer hardware works", ""));
		competencies.add(new Competency("Hardware - Part 2", "Describes advanced concepts of how computer hardware works", ""));
		competencies.add(new Competency("The Internet - Part 1", "Describes the basics of how the Internet works", ""));
		competencies.add(new Competency("The Internet - Part 2", "Describes advanced concepts of how the Internet works", ""));
		return competencies;
	}
}
