package org.wwald.view;

import java.sql.Connection;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.service.DataException;
import org.wwald.view.UserForm.Field;

public class ProfilePage extends BasePage {
	public ProfilePage(PageParameters parameters) {
		super(parameters);
		try {
			add(getUserFormPanel());
		} catch(DataException de) {
			String msg = "Sorry but we could not process the request due to an error. We will look into this as soon as we can.";
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
	}

	private Component getUserFormPanel() throws DataException {		
		User userInSession =  WWALDSession.get().getUser();
		Connection conn = ConnectionPool.getConnection(getDatabaseId());
		User user = WWALDApplication.get().getDataFacade().retreiveUserByUsername(conn, userInSession.getUsername());
		UserFormPanel userFormPanel = 
			new UserFormPanel(WicketIdConstants.PROFILE_USER_DETAILS, 
							  user, getUserFieldsToUpdate());
		userFormPanel.setFieldEditable(UserForm.Field.USERNAME, false);
		userFormPanel.setFieldEditable(UserForm.Field.ROLE, false);
		userFormPanel.setFieldEditable(UserForm.Field.PASSWORD, false);
		userFormPanel.setFieldEditable(UserForm.Field.REPEAT_PASSWORD, false);
		userFormPanel.setRoleChoices(Role.ADMIN, Role.MENTOR, Role.STUDENT);
		
		
		
		return userFormPanel;
	}

	private Field[] getUserFieldsToUpdate() {
		return new UserForm.Field[] {Field.FIRST_NAME, 
				 Field.MIDDLE_INITIAL, 
				 Field.LAST_NAME};
	}

}
