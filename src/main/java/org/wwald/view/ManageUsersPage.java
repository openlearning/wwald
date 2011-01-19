package org.wwald.view;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Permission;
import org.wwald.model.Role;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;
import org.wwald.view.components.AccessControlledViewPageLink;


public class ManageUsersPage extends AccessControlledPage {
	
	private ListView usersListView;
	private static final Logger cLogger = Logger.getLogger(ManageUsersPage.class);
	
	public ManageUsersPage(PageParameters parameters) {
		super(parameters);
		try {
			getUsers();
			this.usersListView = getUsersList(); 
			add(this.usersListView);
			//add(getAddUserForm());
		} catch(DataException de) {
			String msg = "Sorry but we could not process the request due to an " +
						 "error. We will look into this as soon as we can.";
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
	
	private List<UserMeta> getUsers() throws DataException {
		IDataFacade dataFacade = ((WWALDApplication)Application.get()).getDataFacade();
		Connection conn = ConnectionPool.getConnection(getDatabaseId());
		return dataFacade.retreiveAllUserMeta(conn);
	}
	
	private ListView getUsersList() throws DataException {
		
		return new ListView("users", getUsers()) {
			
			@Override
			protected void onModelChanged() {
				try {
					setModelObject(getUsers());
				} catch(DataException de) {
					String msg = "Could not get list of users";
					cLogger.error(msg, de);
					error(msg);
				}
			}
			
			@Override
			protected void populateItem(ListItem item) {
				final UserMeta user = (UserMeta)item.getModelObject();
				PageParameters parameters = new PageParameters();
				parameters.add(WWALDConstants.USERID, String.valueOf(user.getUserid()));
				
				Link userDetailsLink = 
					new AccessControlledViewPageLink(WicketIdConstants.USER_DETAILS_PAGE, 
													UserDetailsPage.class,
													parameters,
													new Role[]{Role.ADMIN});
				
				Label label = new Label("user", user.getIdentifier());
				userDetailsLink.add(label);
				item.add(userDetailsLink);
				
				Link deleteUserLink = new Link("delete_user_link") {

					@Override
					public void onClick() {
						String databaseId = getDatabaseId();
						Connection conn = ConnectionPool.getConnection(databaseId);
						IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
						try {
							dataFacade.deleteUserMeta(conn, user.getUserid());
							usersListView.modelChanged();
						} catch(DataException sqle) {
							error("Could not delete user '" + user.getUserid() + "'");
						}
					}
					
				};
				deleteUserLink.add(new Label("delete_user_link_label", "Delete"));
				item.add(deleteUserLink);
			}
			
		};
		
	}
}
