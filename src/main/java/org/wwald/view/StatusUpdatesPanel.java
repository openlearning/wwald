package org.wwald.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.StatusUpdate;

public class StatusUpdatesPanel extends Panel {

	public StatusUpdatesPanel(String id) {
		super(id);
		add(getStatusUpdateListView());
	}
	
	private ListView getStatusUpdateListView() {
    	return
    	new ListView(WicketIdConstants.STATUS_UPDATES, ((WWALDApplication)getApplication()).getDataFacade().getStatusUpdates()) {

			@Override
			protected void populateItem(ListItem item) {
				StatusUpdate statusUpdate = (StatusUpdate)item.getModelObject();
				item.add(new Label(WicketIdConstants.STATUS_UPDATE_TEXT, statusUpdate.getText()));
			}
    		
    	};
    }

}
