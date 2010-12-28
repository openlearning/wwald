package org.wwald.view;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.StatusUpdate;
import org.wwald.service.DataException;

/**
 * This {@link Panel} displays various status updates which have happened in the
 * social learning environment.
 * At the moment it only displays course enrollments and course drops
 * 
 * @author pshah
 *
 */
public class StatusUpdatesPanel extends Panel {

	public StatusUpdatesPanel(String id) {
		super(id);
		try {
			add(getStatusUpdateListView());
		} catch(DataException de) {
			String msg = "Sorry we could not get status updates, due to an internal error";
			PageParameters parameters = getPage().getPageParameters();
			parameters.add(WicketIdConstants.MESSAGES, msg);
		}
	}
	
//	private ListView getStatusUpdateListView() throws DataException {
//		ServletWebRequest request = (ServletWebRequest)getRequest();
//		String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
//		String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
//		List<StatusUpdate> statusUpdates = ((WWALDApplication)getApplication()).getDataFacade().getStatusUpdates(ConnectionPool.getConnection(databaseId));
//    	return
//    	new ListView(WicketIdConstants.STATUS_UPDATES, statusUpdates) {
//
//			@Override
//			protected void populateItem(ListItem item) {
//				StatusUpdate statusUpdate = (StatusUpdate)item.getModelObject();
//				item.add(new Label(WicketIdConstants.STATUS_UPDATE_TEXT, statusUpdate.getText()));
//			}
//    		
//    	};
//    }
	
	private ListView getStatusUpdateListView() throws DataException {
		ServletWebRequest request = (ServletWebRequest)getRequest();
		String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
		String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
		List<StatusUpdate> statusUpdates = 
			((WWALDApplication)getApplication()).
				getDataFacade().
					getStatusUpdates(ConnectionPool.getConnection(databaseId));
    	return
    	new ListView(WicketIdConstants.STATUS_UPDATES, statusUpdates) {

			@Override
			protected void populateItem(ListItem item) {
				StatusUpdate statusUpdate = (StatusUpdate)item.getModelObject();
//				item.add(new Label(WicketIdConstants.STATUS_UPDATE_TEXT, statusUpdate.getText()));
				StatusUpdatePanel statusUpdatePanel = 
					new StatusUpdatePanel(WicketIdConstants.A_STATUS_UPDATE, 
										  statusUpdate);
				item.add(statusUpdatePanel);
			}
    		
    	};
    }

}
