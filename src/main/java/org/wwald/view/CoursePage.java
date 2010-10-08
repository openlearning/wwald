package org.wwald.view;

import java.util.List;

import org.apache.log4j.Logger;
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
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.model.Competency;
import org.wwald.model.Course;
import org.wwald.model.DataFacadeRDBMSImpl;
import org.wwald.model.Mentor;

public class CoursePage extends BasePage {
	Course selectedCourse;
	private static Logger cLogger = Logger.getLogger(CoursePage.class);
	
	public CoursePage(final PageParameters parameters) {
		super(parameters);
		this.selectedCourse = getSelectedCourse(parameters);
		replaceSidebar(getSidebar());
		Competency selectedCompetency = getSelectedCompetency(parameters,selectedCourse);
		if(parameters.getString(WWALDConstants.SELECTED_COMPETENCY) == null && selectedCompetency != null ){
			parameters.add(WWALDConstants.SELECTED_COMPETENCY, String.valueOf(selectedCompetency.getId()));
		}
		if(selectedCourse != null) {
			
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
			
			add(getCompetenciesListView(selectedCourse, selectedCompetency));
			add(new Label("selected.course", selectedCourse.getTitle()));
			add(new Label("selected.lecture", selectedCompetency.getTitle()));
			add(new Label("competency.resources", selectedCompetency.getResource()).setEscapeModelStrings(false));
			add(new Label("competency.description", selectedCompetency.getDescription()));
		}
		else {
			setResponsePage(EditCompetencies.class, parameters);
		}
    }
	
	private Course getSelectedCourse(PageParameters parameters) {
		WWALDApplication app = (WWALDApplication)getApplication();
		DataFacadeRDBMSImpl dataStore = app.getDataFacade();
		String selectedCourseId = parameters.getString(WWALDConstants.SELECTED_COURSE);
		Course course = dataStore.retreiveCourse(selectedCourseId); 
		return course;
	}

	private Competency getSelectedCompetency(PageParameters parameters, Course selectedCourse) {
		if(parameters == null) {
			throw new NullPointerException("PageParameters cannot be null");
		}
		if(selectedCourse == null) {
			throw new NullPointerException("selectedCourse cannot be null");
		}
		String selectedCompetencyId = parameters.getString(WWALDConstants.SELECTED_COMPETENCY);
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
		
		return competency;
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

	@Override
	public Panel getSidebar() {
		if(this.selectedCourse == null) {
			return new EmptyPanel("rhs_sidebar");
		}
		else {
			Mentor mentor = selectedCourse.getMentor();
			return new CourseDetailsPanel(mentor, "rhs_sidebar");
		}
		
	}
}
