package org.wwald.view;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.Role;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;
import org.wwald.util.ParseException;
import org.wwald.view.components.AccessControlledViewPageLink;

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
    	add(getCoursesPanel(parameters));
    	add(getStatusUpdatesPanel());
    }

	private Component getCoursesPanel(PageParameters parameters) {
		ServletWebRequest request = (ServletWebRequest)getRequest();
		
		System.out.println("Request Domain Name '" + request.getHttpServletRequest().getLocalName() + "'");
		System.out.println("Request relative URL '" + request.getURL() + "'");
		//TODO: Should we throw the DataException out of here and let the caller handle it?
		Panel coursesPanel = null;
		try {
			coursesPanel = new CoursesPanel(WicketIdConstants.COURSES_PANEL);
		} catch(ParseException pe) {
			coursesPanel = new EmptyPanel(WicketIdConstants.COURSES_PANEL);
			String msg = "Sorry but we cannot display the list of courses " +
						 "due to an internal error. We will look into this " +
						 "issue very soon.";
			parameters.add(WicketIdConstants.MESSAGES, msg);
		} catch(DataException de) {
			coursesPanel = new EmptyPanel(WicketIdConstants.COURSES_PANEL);
			String msg = "Sorry but we cannot display the list of courses " +
						 "due to an internal error. We will look into this " +
						 "issue very soon.";
			parameters.add(WicketIdConstants.MESSAGES, msg);
		}
		
		return coursesPanel;
	}

	private Link getEditCoursesLink() {
		Link link = new AccessControlledViewPageLink(WicketIdConstants.COURSES_EDIT, 
										   EditCourses.class,
										   new Role[]{Role.ADMIN});
		return link; 
	}
            
    private Panel getStatusUpdatesPanel() {
    	return new StatusUpdatesPanel(WicketIdConstants.STATUS_UPDATES_PANEL);
    }
	
}
