package org.wwald.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WicketIdConstants;
import org.wwald.model.User;

public class InternalUsersDetailsPanel extends Panel {

	public InternalUsersDetailsPanel(String id, User user) {
		super(id);
		add(new Label(WicketIdConstants.INTERNAL_USERNAME_LABEL, "Username: "));
		add(new Label(WicketIdConstants.INTERNAL_USERNAME, user.getUsername()));
		add(new Label(WicketIdConstants.INTERNAL_USER_EMAIL_LABEL, "Email: "));
		add(new Label(WicketIdConstants.INTERNAL_USER_EMAIL, user.getEmail()));
	}
	
	
}
