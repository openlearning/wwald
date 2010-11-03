package org.wwald.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.User;

public class HeaderPanel extends Panel {
	public HeaderPanel(String id) {
		super(id);
		
		Link homeLink = new BookmarkablePageLink(WicketIdConstants.HOMEPAGE_LINK, HomePage.class);
		Label homeLabel = new Label(WicketIdConstants.HOMEPAGE_LABEL, "Home .");
		homeLink.add(homeLabel);
		add(homeLink);
		
		Link aboutLink = new BookmarkablePageLink(WicketIdConstants.ABOUT_LINK, StaticPage.class);
		Label aboutLabel = new Label(WicketIdConstants.ABOUT_LABEL, "About .");
		aboutLink.add(aboutLabel);
		add(aboutLink);
		
		Link loginLink = new BookmarkablePageLink(WicketIdConstants.LOGIN_LINK, LoginPage.class);
		Label loginLabel = new Label(WicketIdConstants.LOGIN_LABEL, "Login .");
		loginLink.add(loginLabel);
		add(loginLink);
		
		Link registerLink = new BookmarkablePageLink(WicketIdConstants.REGISTER_LINK, Register.class);
		Label registerLabel = new Label(WicketIdConstants.REGISTER_LABEL, "Register .");
		registerLink.add(registerLabel);
		add(registerLink);
		
		Link logoutLink = new Link(WicketIdConstants.LOGOUT_LINK) {
			@Override
			public void onClick() {
				((WWALDApplication)getApplication()).getApplicationFacade().logout();
				setResponsePage(HomePage.class);
			}
		};
		Label logoutLabel = new Label(WicketIdConstants.LOGOUT_LABEL, "Logout .");
		logoutLink.add(logoutLabel);
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
