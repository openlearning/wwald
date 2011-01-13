package org.wwald.view;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Question;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

public class AskQuestionPage extends BasePage {

	private Question question;
	
	private static final transient Logger cLogger = 
		Logger.getLogger(AskQuestionPage.class);
	
	public AskQuestionPage(PageParameters parameters) {
		super(parameters);
		String forumId = parameters.getString("forum");
		if(forumId != null) {
			this.question = new Question();
			this.question.setDiscussionId(forumId);
			add(new FeedbackPanel(WicketIdConstants.MESSAGES));
			add(getQuestionForm(parameters));
		} else {
			String msg = "Could not find page parameter for key 'forum'";
			setResponsePage(GenericErrorPage.class);
		}
	}

	private Component getQuestionForm(final PageParameters parameters) {
		
		Form questionForm = new Form("question_form") {			
			@Override
			public void onSubmit() {
				String databaseId = getDatabaseId();
				IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
				Connection conn = ConnectionPool.getConnection(databaseId);
				try {
					question.setUserMeta(WWALDSession.get().getUserMeta());
					dataFacade.insertQuestion(conn, question);
					setResponsePage(ForumPage.class, parameters);
				} catch(DataException de) {
					String msg = "Could not save question in the database";
					cLogger.error(msg, de);
					setResponsePage(GenericErrorPage.class);
				}
			}
		};
		TextField questionTitleTextField = 
			new RequiredTextField("question_title_text_field", 
						  new PropertyModel(this.question, "title"));
		questionForm.add(questionTitleTextField);
		TextArea questionTextArea = 
			new TextArea("question_textarea", 
						 new PropertyModel(this.question, "contents"));
		questionTextArea.setRequired(true);
		questionForm.add(questionTextArea);
		
		return questionForm;
	}

}
