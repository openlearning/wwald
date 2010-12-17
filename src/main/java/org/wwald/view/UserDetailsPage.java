package org.wwald.view;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Permission;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;
import org.wwald.view.UserForm.Field;
import org.wwald.view.components.AccessControlledViewPageLink;


public class UserDetailsPage extends AccessControlledPage {
	 
	private static final Logger cLogger = Logger.getLogger(UserDetailsPage.class);
	
	public UserDetailsPage(PageParameters parameters) {
		super(parameters);
		String userid = parameters.getString(WWALDConstants.USERID);
		try {
			IDataFacade dataFacade = ((WWALDApplication)Application.get()).getDataFacade();
			Connection conn = ConnectionPool.getConnection(getDatabaseId());			
			
			UserMeta userMeta = dataFacade.retreiveUserMeta(conn, Integer.parseInt(userid));
			if(userMeta.getLoginVia().equals(UserMeta.LoginVia.INTERNAL)) {
				User user = dataFacade.retreiveUserByUsername(conn, userMeta.getIdentifier());
				InternalUsersDetailsPanel panel = new InternalUsersDetailsPanel(WicketIdConstants.INTERNAL_USER_DETAILS, user);
				add(panel);
			}
			else {
				add(new EmptyPanel(WicketIdConstants.INTERNAL_USER_DETAILS));
			}
			add(buildChangeRoleForm(userMeta));						
		} catch(DataException de) {
			String msg = "Sorry but we could not process the request due to an error. We will look into this as soon as we can.";
			cLogger.error(msg, de);
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
		Link manageUsersLink = new AccessControlledViewPageLink(WicketIdConstants.MANAGE_USERS_PAGE, ManageUsersPage.class, new Role[]{Role.ADMIN});
		add(manageUsersLink);			
	}	

	private Component buildChangeRoleForm(final UserMeta userMeta) {
		
		Form changeRoleForm = new Form("change_role_form") {
			
			public void onSubmit() {
				IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
				String databaseId = ConnectionPool.getDatabaseIdFromRequest((ServletWebRequest)getRequest());
				Connection conn = ConnectionPool.getConnection(databaseId);
				Role newRole = (Role)get("change_role_form_role_dropdown").getDefaultModelObject();
				userMeta.setRole(newRole);
				try {
					dataFacade.updateUserMetaRole(conn, userMeta);
				} catch(DataException de) {
					String msg = "Could not change role '" + userMeta + "'";
					cLogger.error(msg, de);
					error(msg);
				}
			}
		};
		
		DropDownChoice roleChoiceField = new DropDownChoice("change_role_form_role_dropdown", new PropertyModel(userMeta, "role"), getRoles());
		changeRoleForm.add(roleChoiceField);
		return changeRoleForm;
	}

	private List getRoles() {
		Role roles[] = Role.values();
		List<Role> rolesList = Arrays.asList(roles);
		return rolesList;
	}

	private Field[] getUserFieldsToUpdate() {
		return new UserForm.Field[] {Field.EMAIL};
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.MANAGE_USERS;
	}
	
}
