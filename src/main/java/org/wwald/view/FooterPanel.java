package org.wwald.view;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WicketIdConstants;

public class FooterPanel extends Panel {
	public FooterPanel(String id) {
		super(id);
		
		Link tosLink = 
			new BookmarkablePageLink(WicketIdConstants.TOS_LINK, HomePage.class);
		add(tosLink);
		
		Link privacyPolicyLink = 
			new BookmarkablePageLink(WicketIdConstants.PRIVACY_POLICY_LINK, HomePage.class);
		add(privacyPolicyLink);
		
		Link contactLink = 
			new BookmarkablePageLink(WicketIdConstants.CONTACT_LINK, HomePage.class); 
		add(contactLink);
	}
}
