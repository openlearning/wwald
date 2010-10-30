package org.wwald.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WicketIdConstants;

public class FooterPanel extends Panel {
	public FooterPanel(String id) {
		super(id);
		
		Link tosLink = 
			new BookmarkablePageLink(WicketIdConstants.TOS_LINK, HomePage.class);
		Label tosLabel = new Label(WicketIdConstants.TOS_LABEL, "Terms of Service");
		tosLink.add(tosLabel);
		add(tosLink);
		
		Link privacyPolicyLink = 
			new BookmarkablePageLink(WicketIdConstants.PRIVACY_POLICY_LINK, HomePage.class);
		Label privacyPolicyLabel = new Label(WicketIdConstants.PRIVACY_POLICY_LABEL, "Privacy Policy");
		privacyPolicyLink.add(privacyPolicyLabel);
		add(privacyPolicyLink);
		
		Link contactLink = 
			new BookmarkablePageLink(WicketIdConstants.CONTACT_LINK, HomePage.class);
		Label contactLabel = new Label(WicketIdConstants.CONTACT_LABEL, "Contact");
		contactLink.add(contactLabel);
		add(contactLink);
	}
}
