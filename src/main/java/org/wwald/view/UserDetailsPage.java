package org.wwald.view;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Mentor;
import org.wwald.model.Permission;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;
import org.wwald.view.UserForm.Field;
import org.wwald.view.components.AccessControlledViewPageLink;


public class UserDetailsPage extends AccessControlledPage {
	private static final Logger cLogger = Logger.getLogger(UserDetailsPage.class);
	
	public UserDetailsPage(PageParameters parameters) {
		super(parameters);
		String username = parameters.getString("username");
		try {
			IDataFacade dataFacade = ((WWALDApplication)Application.get()).getDataFacade();
			Connection conn = ConnectionPool.getConnection(getDatabaseId());
			User user = dataFacade.retreiveUserByUsername(conn, username);
			
			add(buildUserForm(user));			
		} catch(DataException de) {
			String msg = "Sorry but we could not process the request due to an error. We will look into this as soon as we can.";
			cLogger.error(msg, de);
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
		Link manageUsersLink = new AccessControlledViewPageLink(WicketIdConstants.MANAGE_USERS_PAGE, ManageUsersPage.class, new Role[]{Role.ADMIN});
		add(manageUsersLink);			
	}	

	private Component buildUserForm(User user) {
		UserFormPanel userFormPanel = new UserFormPanel(WicketIdConstants.USER_DETAILS_FORM, user, getUserFieldsToUpdate());
		userFormPanel.setFieldEditable(UserForm.Field.USERNAME, false);
		userFormPanel.setFieldEditable(UserForm.Field.EMAIL, false);
		userFormPanel.setFieldEditable(UserForm.Field.PASSWORD, false);
		userFormPanel.setFieldEditable(UserForm.Field.REPEAT_PASSWORD, false);
		userFormPanel.setRoleChoices(Role.ADMIN, Role.MENTOR, Role.STUDENT);
		
		return userFormPanel;
	}

	private Field[] getUserFieldsToUpdate() {
		return new UserForm.Field[] {Field.ROLE};
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.MANAGE_USERS;
	}
	
}
