package org.wwald.view;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Mentor;
import org.wwald.model.Permission;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;


public class AdminPage extends AccessControlledPage {

	private static final Logger cLogger = Logger.getLogger(AdminPage.class);
	
	public AdminPage(PageParameters parameters) {
		super(parameters);
		try {
			add(getMentorList());
		} catch(DataException de) {
			String msg = "Sorry but we could not process the request due to an error. We will look into this as soon as we can.";
			cLogger.error(msg, de);
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADD_MENTOR;
	}
	
	private ListView getMentorList() throws DataException {
		IDataFacade dataFacade = ((WWALDApplication)Application.get()).getDataFacade();
		Connection conn = ConnectionPool.getConnection();
		List<Mentor> mentors = dataFacade.retreiveAllMentors(conn);
		
		return new ListView("mentors", mentors) {

			@Override
			protected void populateItem(ListItem item) {
				Mentor mentor = (Mentor)item.getModelObject();
				Label label = new Label("mentor", mentor.getUsername());
				item.add(label);
			}
			
		};
		
	}

}
