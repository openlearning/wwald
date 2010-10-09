package org.wwald.view;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDConstants;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.Course;
import org.wwald.model.IDataFacade;
import org.wwald.model.NonExistentCourse;
import org.wwald.model.StatusUpdate;
import org.wwald.view.components.SimpleViewPageLink;

/**
 * Homepage
 */
public class HomePage extends BasePage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient IDataFacade dataFacade;
	
	private static Logger cLogger = Logger.getLogger(HomePage.class);

	// TODO Add any page properties or variables here

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
    public HomePage(final PageParameters parameters) {
    	super(parameters);
    	this.dataFacade = ((WWALDApplication)getApplication()).getDataFacade();
    	add(getEditCoursesLink());
    	add(getCoursesPanel());
    	add(getStatusUpdatesPanel());
    }

	private Component getCoursesPanel() {
		return new CoursesPanel(WicketIdConstants.COURSES_PANEL);
	}

	private Link getEditCoursesLink() {
		return new SimpleViewPageLink(WicketIdConstants.COURSES_EDIT, EditCourses.class);
	}
            
    private Panel getStatusUpdatesPanel() {
    	return new StatusUpdatesPanel(WicketIdConstants.STATUS_UPDATES_PANEL);
    }

	@Override
	public Panel getSidebar() {
		return new EmptyPanel(WicketIdConstants.RHS_SIDEBAR);
	}
	
}
