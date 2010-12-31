package org.wwald.view;

import java.sql.Connection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

/**
 * Displays all courses enrolled by a user
 * @author pshah
 *
 */
public class CoursesEnrolledByUserPanel extends Panel {
	
	private UserMeta userMeta;
	
	public CoursesEnrolledByUserPanel(String id, UserMeta user) 
		throws DataException {
		
		super(id);
		this.userMeta = userMeta;
		add(getEnrolledCoursesListView(userMeta));
	}
	
	private Component getEnrolledCoursesListView(UserMeta userMeta) 
	throws DataException {
	
	ServletWebRequest request = (ServletWebRequest)getRequest();
	String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
	String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
	
	Connection conn = ConnectionPool.getConnection(databaseId);
	IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
	List<String> courseIds = 
		dataFacade.retreiveCourseEnrollmentsForUser(conn, 
													userMeta);
	return new ListView("enrolled_courses", courseIds) {

		@Override
		protected void populateItem(ListItem item) {
			String courseId = (String)item.getModelObject();
			PageParameters pageParams = new PageParameters();
			pageParams.add(WWALDConstants.SELECTED_COURSE, courseId);
			Link enrolledCourseLink = 
				new BookmarkablePageLink("enrolled_course_link", CoursePage.class, pageParams);
			Label enrolledCourseLinkLabel = new Label("enrolled_course_link_label", courseId);
			enrolledCourseLink.add(enrolledCourseLinkLabel);
			
			item.add(enrolledCourseLink);
		}
		
	};
}
	
}
