package org.wwald.view;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.wwald.WWALDSession;
import org.wwald.model.UserMeta;

import static org.wwald.ForumConstants.*;

public class ForumPage extends BasePage {

	private transient Logger cLogger = Logger.getLogger(ForumPage.class);
	
	public ForumPage(PageParameters parameters) {
		super(parameters);
		String forumId = parameters.getString(FORUM_PAGE_PARAM);
		try {			
			if(forumId != null) {
				add(getAskQuestionLink(forumId));
				String questionId = parameters.getString(QUESTION_PAGE_PARAM);
				if(questionId != null) {
					QuestionPanel questionPanel = 
						new QuestionPanel(QUESTION_OR_QUESTIONS_PANEL, 
										  forumId, 
										  questionId);
					add(questionPanel);
				} else {
					ForumPanel questionsPanel = 
						new ForumPanel(QUESTION_OR_QUESTIONS_PANEL, 
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
				pageParams.add(FORUM_PAGE_PARAM, forumId);
				setResponsePage(AskQuestionPage.class, pageParams);
			}
			
		};
		
		BookmarkablePageLink loginLink = 
			new BookmarkablePageLink(ASK_QUESTION_LINK, LoginPage.class);
		if(userMeta != null) {
			return askQuestionLink;
		}
		else {
			return loginLink;
		}
	}
}
