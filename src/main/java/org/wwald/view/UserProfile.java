package org.wwald.view;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;


public class UserProfile extends BasePage {

	private static final Logger cLogger = Logger.getLogger(UserProfile.class);
	public UserProfile(PageParameters parameters) {
		super(parameters);
		try {
			UserMeta userMeta = retreiveUserMeta(parameters);
			String identifier = userMeta.getIdentifier();
			add(new Label(WicketIdConstants.PUBLIC_USER_PROFILE_IDENTIFIER, 
						  identifier));
		} catch(Exception e) {
			cLogger.error("Caught Exception", e);
			setResponsePage(GenericErrorPage.class);
		}
	}
	
	private UserMeta retreiveUserMeta(PageParameters parameters) throws NumberFormatException, DataException {
		UserMeta retVal = null;
		String userid = parameters.getString(WWALDConstants.USERID);
		if(userid != null) {
			try {
				IDataFacade dataFacade = ((WWALDApplication)Application.get()).getDataFacade();
				Connection conn = ConnectionPool.getConnection(getDatabaseId());
				retVal = dataFacade.retreiveUserMeta(conn, Integer.parseInt(userid));
			} catch(NumberFormatException nfe) {
				cLogger.error("Cannot fetch UserMeta for incorrect userid '" + userid + "'");
			}
		}
		return retVal;
	}



}
