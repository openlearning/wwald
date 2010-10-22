package org.wwald.view;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.Competency;
import org.wwald.model.ConnectionPool;
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
		if(selectedCourse == null) {
			setResponsePage(EditCompetencies.class, parameters);
		}
		else {
			add(new CourseCompetenciesPanel(WicketIdConstants.COURSE_COMPETENCIES_PANEL, 
					   this.selectedCourse,
					   selectedCompetency, 
					   parameters));
		}
    }
	
	private Course getSelectedCourse(PageParameters parameters) {
		WWALDApplication app = (WWALDApplication)getApplication();
		DataFacadeRDBMSImpl dataStore = app.getDataFacade();
		String selectedCourseId = parameters.getString(WWALDConstants.SELECTED_COURSE);
		Course course = dataStore.retreiveCourse(ConnectionPool.getConnection(),selectedCourseId); 
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
