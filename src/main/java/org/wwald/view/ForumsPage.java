package org.wwald.view;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import static org.wwald.ForumConstants.*;

public class ForumsPage extends BasePage {

	public ForumsPage(PageParameters parameters) {
		super(parameters);
		String forumId = parameters.getString(FORUM_PAGE_PARAM);
		if(forumId == null) {
			add(new Label(FORUM_PAGE_TITLE, "Forums"));
			add(new ForumsPanel(FORUMS_PANEL));
		} else {
			setResponsePage(ForumPage.class, parameters);
		}
	}
}
