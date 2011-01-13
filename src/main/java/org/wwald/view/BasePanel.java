package org.wwald.view;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.model.ConnectionPool;
import org.wwald.service.IDataFacade;

public abstract class BasePanel extends Panel {

	public BasePanel(String id) {
		super(id);
	}
	
	public String getDatabaseId() {
		ServletWebRequest request = (ServletWebRequest)getRequest();
		String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
		String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
		return databaseId;
	}
	
	public IDataFacade getDataFacade() {
		return WWALDApplication.get().getDataFacade();
	}

}
