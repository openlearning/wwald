package org.wwald.view;

import java.sql.Connection;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.value.ValueMap;
import org.wwald.WWALDApplication;
import org.wwald.model.ConnectionPool;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;
import org.wwald.view.components.UserImage;

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
	
	public Component getUserImage(int userid) throws DataException {
		Connection conn = ConnectionPool.getConnection(getDatabaseId());
		IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
		UserMeta userMeta = dataFacade.retreiveUserMeta(conn, userid);
		UserImage userImage = 
			new UserImage(WWALDApplication.USER_THUMBNAIL_IMAGE, 
						  userMeta);
		return userImage;
	}
	
	public IDataFacade getDataFacade() {
		return WWALDApplication.get().getDataFacade();
	}

}
