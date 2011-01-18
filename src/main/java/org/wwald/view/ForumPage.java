package org.wwald.view;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.wwald.WWALDSession;
import org.wwald.model.UserMeta;

public class ForumPage extends BasePage {

	private transient Logger cLogger = Logger.getLogger(ForumPage.class);
	
	public ForumPage(PageParameters parameters) {
		super(parameters);
		String forumId = parameters.getString("forum");
		try {			
			if(forumId != null) {
				add(getAskQuestionLink(forumId));
				String questionId = parameters.getString("question");
				if(questionId != null) {
					QuestionPanel questionPanel = 
						new QuestionPanel("question_or_questions_panel", 
										  forumId, 
										  questionId);
					add(questionPanel);
				} else {
					ForumPanel questionsPanel = 
						new ForumPanel("question_or_questions_panel", 
										   forumId);
					add(questionsPanel);
				}
			} else {
				cLogger.error("forum page parameter is null in the ForumPage. " +
							  "This should never have happened");
				setResponsePage(GenericErrorPage.class);
			}			
		} catch(Exception e) {
			String msg = "An Exception occured while retreiving " +
						 "questions for forum '" + forumId + "'";
			cLogger.error(msg);
			setResponsePage(GenericErrorPage.class);
		}
	}

	private Component getAskQuestionLink(final String forumId) {
		UserMeta userMeta = WWALDSession.get().getUserMeta();
		
		Link askQuestionLink = new Link("ask_question_link") {

			@Override
			public void onClick() {
				PageParameters pageParams = new PageParameters();
				pageParams.add("forum", forumId);
				setResponsePage(AskQuestionPage.class, pageParams);
			}
			
		};
		
		BookmarkablePageLink loginLink = 
			new BookmarkablePageLink("ask_question_link", LoginPage.class);
		if(userMeta != null) {
			return askQuestionLink;
		}
		else {
			return loginLink;
		}
	}
}
