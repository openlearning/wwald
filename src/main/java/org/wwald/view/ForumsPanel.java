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

import static org.wwald.ForumConstants.*;

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
		return new ListView(FORUMS, forums) {

			@Override
			protected void populateItem(ListItem item) {
				Forum forum = (Forum)item.getModelObject();
			
				PageParameters pageParameters = new PageParameters();
				pageParameters.add(FORUM, forum.getId());
				Link forumLink = 
					new BookmarkablePageLink(FORUM_LINK, 
											 ForumsPage.class, 
											 pageParameters);
				
				forumLink.add(new Label(FORUM_LINK_LABEL, forum.getId() + " " + forum.getTitle()));
				item.add(forumLink);
				
				Label forumDescriptionLabel = new Label(FORUM_DESCRIPTION, forum.getDescription());
				item.add(forumDescriptionLabel);
			}
			
		};
	}

}
