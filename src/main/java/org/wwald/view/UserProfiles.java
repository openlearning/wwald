package org.wwald.view;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

public class UserProfiles extends BasePage {

	private static final Logger cLogger = Logger.getLogger(UserProfiles.class);
	
	public UserProfiles(PageParameters parameters) {
		super(parameters);
		try {
			String userid = parameters.getString(WWALDConstants.USERID);			
			if(userid == null) {
				add(getUsersListView());
			}
			else {
				setResponsePage(UserProfile.class, parameters);
			}
		} catch(Exception e) {
			cLogger.error("Caught Exception ", e);
			setResponsePage(GenericErrorPage.class);
		}
	}

	private Component getUsersListView() throws DataException {
		IDataFacade dataFacade = ((WWALDApplication)Application.get()).getDataFacade();
		Connection conn = ConnectionPool.getConnection(getDatabaseId());
//		List<User> users = dataFacade.retreiveAllUsers(conn);
		List<UserMeta> allUserMeta = dataFacade.retreiveAllUserMeta(conn);
		return new ListView(WicketIdConstants.PUBLIC_USER_PROFILES_LIST, allUserMeta) {

			@Override
			protected void populateItem(ListItem item) {
				UserMeta user = (UserMeta)item.getModelObject();
				PageParameters parameters = new PageParameters();
				parameters.add(WWALDConstants.USERID, String.valueOf(user.getUserid()));
				
				Link userDetailsLink = new BookmarkablePageLink(WicketIdConstants.PUBLIC_USER_PROFILE, 
																		UserProfiles.class,
																		parameters);
				Label label = new Label(WicketIdConstants.PUBLIC_USER_PROFILE_LABEL, user.getIdentifier());
				userDetailsLink.add(label);
				item.add(userDetailsLink);
			}
			
		};
	}

}
