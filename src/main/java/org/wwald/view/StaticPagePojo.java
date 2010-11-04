package org.wwald.view;

import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;
import org.wwald.model.StaticPagePOJO;
import org.wwald.service.DataException;
import org.wwald.view.components.AccessControlledViewPageLink;

public class StaticPagePojo extends BasePage {

	public StaticPagePojo(PageParameters parameters) {		
		super(parameters);
		try {
			Link editLink = new AccessControlledViewPageLink("about_edit", 
															 EditStaticPage.class, 
															 new Role[]{Role.ADMIN});
			
			StaticPagePOJO page = ((WWALDApplication)Application.get()).getDataFacade().retreiveStaticPage(ConnectionPool.getConnection(), "about");
			
			add(new Label(WicketIdConstants.STATIC_PAGE_CONTENTS, page.getContents()));
			add(editLink);
		} catch(DataException de) {
			String msg = "Sorry cannot display this page because an internal error has occured";
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
	}

}
