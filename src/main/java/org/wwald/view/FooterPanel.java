package org.wwald.view;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WicketIdConstants;

public class FooterPanel extends Panel {
	public FooterPanel(String id) {
		super(id);
		PageParameters parameters = null;
		
		parameters = new PageParameters();
		parameters.add(WicketIdConstants.PAGE, WicketIdConstants.TOS_PAGE_NAME);
		Link aboutLink = new BookmarkablePageLink(WicketIdConstants.TOS_LINK, StaticPage.class, parameters);
		Label aboutLabel = new Label(WicketIdConstants.TOS_LABEL, "Terms of Service");
		aboutLink.add(aboutLabel);
		add(aboutLink);

		parameters = new PageParameters();
		parameters.add(WicketIdConstants.PAGE, WicketIdConstants.PRIVACY_POLICY_PAGE_NAME);
		Link ppLink = new BookmarkablePageLink(WicketIdConstants.PRIVACY_POLICY_LINK, StaticPage.class, parameters);
		Label ppLabel = new Label(WicketIdConstants.PRIVACY_POLICY_LABEL, "Privacy Policy");
		ppLink.add(ppLabel);
		add(ppLink);
		
		parameters = new PageParameters();
		parameters.add(WicketIdConstants.PAGE, WicketIdConstants.CONTACT_PAGE_NAME);
		Link contactLink = new BookmarkablePageLink(WicketIdConstants.CONTACT_LINK, StaticPage.class, parameters);
		Label contactLabel = new Label(WicketIdConstants.CONTACT_LABEL, "Contact");
		contactLink.add(contactLabel);
		add(contactLink);
	}
}
