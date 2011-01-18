package org.wwald.view;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

public class ForumsPage extends BasePage {

	public ForumsPage(PageParameters parameters) {
		super(parameters);
		String forumId = parameters.getString("forum");
		if(forumId == null) {
			add(new Label("forum_page_title", "Forums"));
			add(new ForumsPanel("forums_panel"));
		} else {
			setResponsePage(ForumPage.class, parameters);
		}
	}
}
