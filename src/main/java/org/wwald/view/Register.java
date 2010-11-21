package org.wwald.view;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;

public class Register extends BasePage {
	public Register(PageParameters parameters) {
		super(parameters);
		add(getUserFormPanel());
	}

	private Component getUserFormPanel() {
		UserFormPanel userFormPanel = new UserFormPanel("register_panel");
		userFormPanel.setSubmitResponsePage(LoginPage.class);
		return userFormPanel;
	}

}
