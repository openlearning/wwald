package org.wwald.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

public class HeaderPanel extends Panel {
	public HeaderPanel(String id) {
		super(id);
		add(new BookmarkablePageLink("login_link", Login.class));
		add(new BookmarkablePageLink("register_link", Register.class));
	}
}
