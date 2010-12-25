package org.wwald.view;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.Competency;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Course;
import org.wwald.model.Mentor;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

public class CoursePage extends BasePage {
	Course selectedCourse;
	private static Logger cLogger = Logger.getLogger(CoursePage.class);
	
	public CoursePage(final PageParameters parameters) {
		super(parameters);
		try {
			this.selectedCourse = getSelectedCourse(parameters);
			
//			replaceSidebar(getSidebar(this));
			
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
		} catch(DataException de) {
			String msg = "Sorry cannot display this page because an internal error has occured";
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
    }
	
	private Course getSelectedCourse(PageParameters parameters) throws DataException {
		WWALDApplication app = (WWALDApplication)getApplication();
		IDataFacade dataStore = app.getDataFacade();
		String selectedCourseId = parameters.getString(WWALDConstants.SELECTED_COURSE);
		Course course = dataStore.retreiveCourse(ConnectionPool.getConnection(getDatabaseId()),selectedCourseId); 
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
			competency = getFirstCompetency(selectedCourse);			
		}
		else {
			competency = selectedCourse.getCompetency(selectedCompetencyId);
			//The user may have deleted the selected competency from the course wiki
			//In such a scenario the course object will not contain the competency
			//we will default to the first competency
			if(competency == null) {
				competency = getFirstCompetency(selectedCourse);
			}
		}
		
		return competency;
	}

	public Mentor getMentor() {
		Mentor mentor = null;
		if(selectedCourse != null) {
			mentor = selectedCourse.getMentor();
		}
		return mentor;
	}
	
	private Competency getFirstCompetency(Course course) {
		Competency competency = null;
		List<Competency> competencies = selectedCourse.getCompetencies();
		if(competencies != null && competencies.size() > 0) {
			competency = competencies.get(0);
		}
		return competency;
	}
}
