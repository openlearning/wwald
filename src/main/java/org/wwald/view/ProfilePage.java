package org.wwald.view;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.view.UserForm.Field;

public class ProfilePage extends BasePage {
	
	private transient Logger cLogger = Logger.getLogger(ProfilePage.class);	
	
	public ProfilePage(PageParameters parameters) {
		super(parameters);
		try {
			UserMeta userMeta =  WWALDSession.get().getUserMeta();
			
			Component userFormPanel = null;
			Component changePasswordFormPanel = null;
			
			if(userMeta.getLoginVia().equals(UserMeta.LoginVia.INTERNAL)) {
				User userInSession = getUser(userMeta);
				userFormPanel = getUserFormPanel(userInSession);			
				changePasswordFormPanel = getChangePasswordForm(userInSession);				
			}
			else {
				userFormPanel = new EmptyPanel(WicketIdConstants.PROFILE_USER_DETAILS);
				changePasswordFormPanel = new EmptyPanel(WicketIdConstants.CHANGE_PASSWORD_PANEL);
			}
			add(userFormPanel);			
			add(changePasswordFormPanel);
		} catch(DataException de) {
			String msg = "Sorry but we could not process the request due to an error. We will look into this as soon as we can.";
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
	}
	
	private User getUser(UserMeta userMeta) throws DataException {
		String databaseId = ConnectionPool.getDatabaseIdFromRequest((ServletWebRequest)getRequest());
		Connection conn = ConnectionPool.getConnection(databaseId);
		return WWALDApplication.get().getDataFacade().retreiveUserByUsername(conn, userMeta.getIdentifier());
	}

	private Component getChangePasswordForm(final User user) {
		return new ChangePasswordPanel(WicketIdConstants.CHANGE_PASSWORD_PANEL, user);
	}

	private Component getUserFormPanel(User userInSession) throws DataException {
		if(userInSession == null) {
			throw new NullPointerException("userInSession is null");			
		}
		else {
			Connection conn = ConnectionPool.getConnection(getDatabaseId());
			User user = WWALDApplication.get().getDataFacade().retreiveUserByUsername(conn, userInSession.getUsername());
			UserFormPanel userFormPanel = 
				new UserFormPanel(WicketIdConstants.PROFILE_USER_DETAILS, 
								  user, getUserFieldsToUpdate());
			userFormPanel.setFieldEditable(UserForm.Field.USERNAME, false);
			userFormPanel.setFieldEditable(UserForm.Field.ROLE, false);
			userFormPanel.setFieldEditable(UserForm.Field.PASSWORD, false);
			userFormPanel.setFieldVisible(UserForm.Field.PASSWORD, false);
			userFormPanel.setFieldEditable(UserForm.Field.REPEAT_PASSWORD, false);
			userFormPanel.setFieldVisible(UserForm.Field.REPEAT_PASSWORD, false);
			userFormPanel.setRoleChoices(Role.ADMIN, Role.MENTOR, Role.STUDENT);									
			return userFormPanel;
		}
	}

	private Field[] getUserFieldsToUpdate() {
		return new UserForm.Field[] {Field.EMAIL};
	}

}
