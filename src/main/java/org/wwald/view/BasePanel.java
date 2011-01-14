package org.wwald.view;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.value.ValueMap;
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
	
	public Image getUserImage(int userid) {
		String sUserid = String.valueOf(userid);
		ValueMap vm = new ValueMap();
		vm.add("dbId", getDatabaseId());
		vm.add("userid", sUserid);
		return new Image(WWALDApplication.USER_THUMBNAIL_IMAGE, 
						 new ResourceReference(WWALDApplication.USER_THUMBNAIL_IMAGE), 
						 vm);
	}
	
	public IDataFacade getDataFacade() {
		return WWALDApplication.get().getDataFacade();
	}

}
