package org.wwald.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.model.User;

public class HeaderPanel extends Panel {
	public HeaderPanel(String id) {
		super(id);
		
		Link loginLink = new BookmarkablePageLink("login_link", LoginPage.class); 
		add(loginLink);
		
		Link registerLink = new BookmarkablePageLink("register_link", Register.class); 
		add(registerLink);
		
		Link logoutLink = new Link("logout_link") {
			@Override
			public void onClick() {
				((WWALDApplication)getApplication()).getApplicationFacade().logout();
				setResponsePage(HomePage.class);
			}
		}; 
		add(logoutLink);
		
		add(new Label("user", getLogginInUserName()));
		
		if(userLoggedIn()) {
			loginLink.setVisible(false);
			registerLink.setVisible(false);
		}
		else {
			logoutLink.setVisible(false);
		}
	}

	private boolean userLoggedIn() {
		User user = WWALDSession.get().getUser();
		return (!(user == null));
	}

	private String getLogginInUserName() {
		String username = "";
		User user = WWALDSession.get().getUser();
		if(user != null) {
			username = user.getUsername();
		}
		return username;
	}
	
	
}
