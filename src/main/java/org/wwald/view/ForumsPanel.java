package org.wwald.view;

import java.sql.Connection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.wwald.WWALDApplication;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Forum;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

public class ForumsPanel extends BasePanel {

	public ForumsPanel(String id) {
		super(id);
		try {			
			add(getForumsListView());		
		} catch(DataException de) {
			
		}
	}
	
	private Component getForumsListView() throws DataException {
		Connection conn = ConnectionPool.getConnection(getDatabaseId());
		IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
		List<Forum> forums = dataFacade.retreiveAllDiscussionForums(conn);
		return new ListView("forums", forums) {

			@Override
			protected void populateItem(ListItem item) {
				Forum forum = (Forum)item.getModelObject();
			
				PageParameters pageParameters = new PageParameters();
				pageParameters.add("forum", forum.getId());
				Link forumLink = 
					new BookmarkablePageLink("forum_link", 
											 ForumsPage.class, 
											 pageParameters);
				
				forumLink.add(new Label("forum_link_label", forum.getId() + " " + forum.getTitle()));
				item.add(forumLink);
				
				Label forumDescriptionLabel = new Label("forum_description", forum.getDescription());
				item.add(forumDescriptionLabel);
			}
			
		};
	}

}
