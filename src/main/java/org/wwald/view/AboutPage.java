package org.wwald.view;

import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;
import org.wwald.model.StaticPage;
import org.wwald.service.DataException;
import org.wwald.view.components.AccessControlledViewPageLink;

public class AboutPage extends BasePage {

	public AboutPage(PageParameters parameters) {		
		super(parameters);
		try {
			Link editLink = new AccessControlledViewPageLink("about_edit", 
															 EditAbout.class, 
															 new Role[]{Role.ADMIN});
			
			StaticPage page = ((WWALDApplication)Application.get()).getDataFacade().retreiveStaticPage(ConnectionPool.getConnection(), "about");
			
			add(new Label(WicketIdConstants.ABOUT_CONTENTS, page.getContents()));
			add(editLink);
		} catch(DataException de) {
			String msg = "Sorry cannot display this page because an internal error has occured";
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
	}

}
