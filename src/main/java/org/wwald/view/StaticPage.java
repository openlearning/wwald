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

public class StaticPage extends BasePage {
	private final String STATIC_PREFIX = "static/" + WicketIdConstants.PAGE + "/";
	
	public StaticPage(PageParameters parameters) {		
		super(parameters);

		String requestedStaticPage = getRequestedStaticPage(getRequest().getPath());
		if(requestedStaticPage == null || requestedStaticPage.equals("")) {
			requestedStaticPage = parameters.getString(WicketIdConstants.PAGE);
		}
		else {
			parameters.add(WicketIdConstants.PAGE, requestedStaticPage);
		}
		
		try {
			if(requestedStaticPage != null && !requestedStaticPage.equals("")) {
				Link editLink = new AccessControlledViewPageLink(
						WicketIdConstants.EDIT_ABOUT, EditStaticPage.class,
						parameters, new Role[] { Role.ADMIN });

				StaticPagePOJO page = ((WWALDApplication) Application.get())
						.getDataFacade().retreiveStaticPage(
								ConnectionPool.getConnection(),
								requestedStaticPage);

				add(new Label(WicketIdConstants.ABOUT_CONTENTS, page
						.getContents()));
				add(editLink);
			}
			else {
				gotoErrorPage(parameters);
			}
			
		} catch(DataException de) {
			gotoErrorPage(parameters);
		}
	}

	private void gotoErrorPage(PageParameters parameters) {
		String msg = "Sorry cannot display this page because an internal error has occured";
		parameters.add(WicketIdConstants.MESSAGES, msg);
		setResponsePage(GenericErrorPage.class, parameters);
	}

	private String getRequestedStaticPage(String path) {
		if(!path.startsWith(STATIC_PREFIX) || path.length() <= STATIC_PREFIX.length()) {
			return null;
		}
		return path.substring(STATIC_PREFIX.length(), path.length());
	}

}
