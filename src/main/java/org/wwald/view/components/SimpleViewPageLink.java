package org.wwald.view.components;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.Link;
import org.wwald.WWALDSession;
import org.wwald.model.Role;
import org.wwald.model.User;

public class SimpleViewPageLink extends Link {
	private Class<? extends Page> responseView;
	private Role roles[];
	
	public SimpleViewPageLink(String id) {
		this(id, null, null);
	}
	
	public SimpleViewPageLink(String id, Class<? extends Page> responseView) {
		this(id, responseView, null);
	}
	
	public SimpleViewPageLink(String id, Role roles[]) {
		this(id, null, roles);
	}
	
	public SimpleViewPageLink(String id, Class<? extends Page> responseView, Role roles[]) {
		super(id);
		this.responseView = responseView;
		this.roles = roles;
		validateRolesForVisibility();
	}

	@Override
	public void onClick() {
		if(this.responseView != null) {
			setResponsePage(this.responseView);
		}
	}
	
	private void validateRolesForVisibility() {
		if(this.roles != null) {
			boolean visible = false;
			User user = WWALDSession.get().getUser();
			if(user != null) {
				Role userRole = user.getRole();
				for(Role role : roles) {
					if(role.equals(userRole)) {
						visible = true;
						break;
					}
				}
			}
			this.setVisible(visible);
		}
		
	}
}
