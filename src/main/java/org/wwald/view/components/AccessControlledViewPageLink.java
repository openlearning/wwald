package org.wwald.view.components;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.view.HomePage;

public class AccessControlledViewPageLink extends SimpleViewPageLink {
	private Role roles[];
	
	public AccessControlledViewPageLink(String id, Role roles[]) {
		this(id, null, roles);
	}
	
	public AccessControlledViewPageLink(String id, Class<? extends Page> responseView, Role roles[]) {
		super(id, responseView);
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

	private boolean hasPermission() {
		boolean retVal = false;
		if(this.roles != null) {
			User user = WWALDSession.get().getUser();
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
		return retVal;
	}
	
}
