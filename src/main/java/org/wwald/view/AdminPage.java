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
		
		PageParameters pageParamsForSiteAnalytics = new PageParameters();
		pageParamsForSiteAnalytics.add(WicketIdConstants.KVTableKey, 
					   WicketIdConstants.KVTableKey_GOOGLE_ANALYTICS);
		Link googleAnalyticsLink = new AccessControlledViewPageLink(WicketIdConstants.SITE_ANALYTICS_LINK, 
																	KVTablePage.class, 
																	pageParamsForSiteAnalytics, 
																	new Role[]{Role.ADMIN});
		add(googleAnalyticsLink);
		
		
		PageParameters pageParamsForTwitterConsumerKey = new PageParameters();
		pageParamsForTwitterConsumerKey.add(WicketIdConstants.KVTableKey, 
										    WicketIdConstants.KVTableKey_TWITTER_CONSUMER);
		Link twitterConsumerLink = new AccessControlledViewPageLink(WicketIdConstants.TWITTER_CONSUMER_LINK, 
																  KVTablePage.class, 
																  pageParamsForTwitterConsumerKey, 
																  new Role[]{Role.ADMIN});
		add(twitterConsumerLink);
		
		PageParameters pageParamsForTwitterSecretKey = new PageParameters();
		pageParamsForTwitterSecretKey.add(WicketIdConstants.KVTableKey, 
										  WicketIdConstants.KVTableKey_TWITTER_SECRET);
		Link twitterSecretLink = new AccessControlledViewPageLink(WicketIdConstants.TWITTER_SECRET_LINK, 
																  KVTablePage.class, 
																  pageParamsForTwitterSecretKey, 
																  new Role[]{Role.ADMIN});
		add(twitterSecretLink);
		
		Link manageDbLink = new AccessControlledViewPageLink("manage_db", DBPage.class, new Role[]{Role.ADMIN});
		add(manageDbLink);
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADD_MENTOR;
	}

}
