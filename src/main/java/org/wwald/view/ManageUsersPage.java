package org.wwald.view;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.resolver.ParentResolver;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Mentor;
import org.wwald.model.Permission;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;
import org.wwald.view.components.AccessControlledViewPageLink;


public class ManageUsersPage extends AccessControlledPage {
	private static final Logger cLogger = Logger.getLogger(ManageUsersPage.class);
	
	public ManageUsersPage(PageParameters parameters) {
		super(parameters);
		try {
			add(getUsersList());
			//add(getAddUserForm());
		} catch(DataException de) {
			String msg = "Sorry but we could not process the request due to an error. We will look into this as soon as we can.";
			cLogger.error(msg, de);
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.MANAGE_USERS;
	}
	
	private Form getAddUserForm() {
		
		return null;
	}
	
	private ListView getUsersList() throws DataException {
		IDataFacade dataFacade = ((WWALDApplication)Application.get()).getDataFacade();
		Connection conn = ConnectionPool.getConnection(getDatabaseId());
//		List<User> users = dataFacade.retreiveAllUsers(conn);
		List<UserMeta> allUserMeta = dataFacade.retreiveAllUserMeta(conn);
		return new ListView("users", allUserMeta) {

			@Override
			protected void populateItem(ListItem item) {
				UserMeta user = (UserMeta)item.getModelObject();
				PageParameters parameters = new PageParameters();
				parameters.add(WWALDConstants.USERID, String.valueOf(user.getUserid()));
				Link userDetailsLink = new AccessControlledViewPageLink(WicketIdConstants.USER_DETAILS_PAGE, 
																		UserDetailsPage.class,
																		parameters,
																		new Role[]{Role.ADMIN});
				Label label = new Label("user", user.getIdentifier());
				userDetailsLink.add(label);
				item.add(userDetailsLink);
			}
			
		};
		
	}
}
