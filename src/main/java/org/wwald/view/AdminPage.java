package org.wwald.view;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.wwald.WicketIdConstants;
import org.wwald.model.Permission;
import org.wwald.model.Role;
import org.wwald.view.components.AccessControlledViewPageLink;


public class AdminPage extends AccessControlledPage {

	private static final Logger cLogger = Logger.getLogger(AdminPage.class);
	
	public AdminPage(PageParameters parameters) {
		super(parameters);
		
		Link manageUsersLink = new AccessControlledViewPageLink(WicketIdConstants.MANAGE_USERS_PAGE, ManageUsersPage.class, new Role[]{Role.ADMIN});
		add(manageUsersLink);
		
		PageParameters pageParams = new PageParameters();
		pageParams.add(WicketIdConstants.KVTableKey, WicketIdConstants.KVTableKey_GOOGLE_ANALYTICS);
		Link googleAnalyticsLink = new AccessControlledViewPageLink(WicketIdConstants.SITE_ANALYTICS_LINK, KVTablePage.class, pageParams, new Role[]{Role.ADMIN});
		add(googleAnalyticsLink);
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADD_MENTOR;
	}

}
