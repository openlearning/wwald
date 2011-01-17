package org.wwald.view;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
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
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.Competency;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Course;
import org.wwald.model.Forum;
import org.wwald.model.Role;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.view.components.AccessControlledViewPageLink;
import org.wwald.view.components.CourseStatusPanel;


public class CourseCompetenciesPanel extends BasePanel {
	
	private static final transient Logger cLogger = 
		Logger.getLogger(CourseCompetenciesPanel.class);
	
	public CourseCompetenciesPanel(final String id, 
								   final Course course, 
								   final Competency selectedCompetency, 
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
		//since a competency has not been selected, we cannot edit the lecture
		if(selectedCompetency == null) {
			editLecture.setVisible(false);
		}
		try {
			add(editLecture);
			
			add(getCourseForumLink(course));
			
			WWALDApplication app = (WWALDApplication)Application.get();
			
			String description = app.getMarkDown().process(course.getFullDescription());
			add(new Label(WicketIdConstants.COURSE_DESCRIPTION, description).setEscapeModelStrings(false));
			add(getCompetenciesListView(course, selectedCompetency));
			add(new Label(WicketIdConstants.SELECTED_COURSE, course.getTitle())); 
				
			add(getCourseStatusPanel(course));
			
			add(new Label(WicketIdConstants.SELECTED_LECTURE, getSelectedCompetencyTitle(selectedCompetency)));
			//TODO: Can we use something other than labels out here
			String competencyResources = app.getMarkDown().process(getSelectedCompetencyResource(selectedCompetency));
			add(new Label(WicketIdConstants.COMPETENCY_RESOURCES, competencyResources).setEscapeModelStrings(false));
			String competencyDescription = app.getMarkDown().process(getSelectedCompetencyDescription(selectedCompetency));
			add(new Label(WicketIdConstants.COMPETENCY_DESCRIPTION, competencyDescription).setEscapeModelStrings(false));
		} catch(Exception e) {
			String msg = "Caught Exception while showing course competencies";
			cLogger.error(msg, e);
			setResponsePage(GenericErrorPage.class);
		}
	}
	
	private Component getCourseForumLink(Course course) throws DataException {
		Connection conn = ConnectionPool.getConnection(getDatabaseId());
		Forum forum = getDataFacade().retreiveDiscussionForum(conn, course.getId());
		
		PageParameters pageParameters = new PageParameters();
		pageParameters.add("forum", forum.getId());
		
		Link forumLink = 
			new BookmarkablePageLink("forum_link", 
									 ForumsPage.class, 
									 pageParameters);
		
		forumLink.add(new Label("forum_link_label", "Go to course forum"));
		
		return forumLink;
	}

	private String getSelectedCompetencyDescription(Competency competency) {
		String retVal = "";
		if(competency != null) {
			retVal = competency.getDescription();
		}
		return retVal;
	}
	
	private String getSelectedCompetencyResource(Competency competency) {
		String retVal = "";
		if(competency != null) {
			retVal = competency.getResource();
		}
		return retVal;
	}
	
	private String getSelectedCompetencyTitle(Competency competency) {
		String retVal = "";
		if(competency != null) {
			retVal = competency.getTitle();
		}
		return retVal;
	}

	private Component getCourseStatusPanel(Course course) {
		UserMeta userMeta = WWALDSession.get().getUserMeta();
		Panel panel = null;
		if(userMeta == null) {
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
