package org.wwald.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDApplication;

public class HeaderPanel extends Panel {
	public HeaderPanel(String id) {
		super(id);
		add(new BookmarkablePageLink("login_link", Login.class));
		add(new BookmarkablePageLink("register_link", Register.class));
		add(new Link("logout_link") {
			@Override
			public void onClick() {
				((WWALDApplication)getApplication()).getApplicationFacade().logout();
				setResponsePage(HomePage.class);
			}
		});
	}
}
