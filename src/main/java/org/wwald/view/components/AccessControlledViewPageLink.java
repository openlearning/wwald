package org.wwald.view.components;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;
import org.wwald.view.HomePage;

public class AccessControlledViewPageLink extends SimpleViewPageLink {
	
	private Role roles[];
	
	private static final Logger cLogger = Logger.getLogger(AccessControlledViewPageLink.class);
	
	public AccessControlledViewPageLink(String id, Role roles[]) {
		this(id, null, roles);
	}
	
	public AccessControlledViewPageLink(String id, 
										Class<? extends Page> responseView, 
										Role roles[]) {
		this(id, responseView, null, roles);		
	}
	
	public AccessControlledViewPageLink(String id, 
										Class<? extends Page> responseView, 
										PageParameters parameters, 
										Role roles[]) {
		super(id, responseView, parameters);
		this.roles = roles;
		if(!hasPermission()) {
			this.setVisible(false);
		}
	}
	
	@Override
	public void onClick() {
		if(hasPermission()) {
			super.onClick();
		}
		else {
			PageParameters parameters = getPage().getPageParameters();
			if(parameters != null) {
				parameters.add(WicketIdConstants.MESSAGES, 
							   "You do not have authorization to access this page");
			}
			setResponsePage(HomePage.class, parameters);
		}
	}

	protected boolean hasPermission() {
		boolean retVal = false;
		if(this.roles != null) {
			UserMeta userMeta = WWALDSession.get().getUserMeta();
			String databaseId = ConnectionPool.getDatabaseIdFromRequest((ServletWebRequest)getRequest());
			IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
			
			//TODO: The UserMeta object itself should have roles associated with it
			if(userMeta != null) {
				User user = null;
				try {
					user = dataFacade.retreiveUserByUsername(ConnectionPool.getConnection(databaseId), userMeta.getIdentifier());
				} catch(DataException de) {
					String msg = "Could not get user from db while trying to determine permissions";
					cLogger.error(msg, de);
				}
				if(user != null) {
					Role userRole = user.getRole();
					for(Role role : roles) {
						if(role.equals(userRole)) {
							retVal = true;
							break;
						}
					}
				}
			}			
		}
		return retVal;
	}
	
}
