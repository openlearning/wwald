package org.wwald.view;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.model.Answer;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Question;
import org.wwald.model.UserMeta;
import org.wwald.service.IDataFacade;

public class QuestionPanel extends Panel {

	private Answer answer;
	private static transient Logger cLogger = Logger.getLogger(QuestionPanel.class);
	
	public QuestionPanel(String id, String forumId, String questionId) {
		super(id);
		
		if(forumId == null) {
			String msg = "forumId is null";
			cLogger.error(msg);
			setResponsePage(GenericErrorPage.class);
		}
		if(questionId == null) {
			String msg = "questionId is null";
			cLogger.error(msg);
			setResponsePage(GenericErrorPage.class);
		}		
		try {
			int iQuestionId = Integer.parseInt(questionId);
			
			this.answer = new Answer(iQuestionId);
			
			IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
			ServletWebRequest request = (ServletWebRequest)getRequest();
			String requestUrl = 
				request.getHttpServletRequest().getRequestURL().toString();
			String databaseId = 
				ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
			Connection conn = ConnectionPool.getConnection(databaseId);
			
			Question question = dataFacade.retreiveQuestion(conn, 
															forumId, 
															iQuestionId);
			if(question != null) {
				add(new Label("question_title", question.getTitle()));
				add(new Label("question_contents", question.getContents()));
				add(getLogInLink());
				add(getAnswerForm());
			} else {
				String msg = "Question for forumId '" + forumId + 
							 "' questionId '" + iQuestionId + "' is null";
				cLogger.error(msg);
				setResponsePage(GenericErrorPage.class);
			}
		} catch(Exception e) {
			
		}
	}

	private Component getLogInLink() {
		BookmarkablePageLink loginLink = 
			new BookmarkablePageLink("login_to_answer", LoginPage.class);
		loginLink.add(new Label("login_to_answer_label", 
					  "Login to answer this question"));
		final UserMeta userMeta = WWALDSession.get().getUserMeta();
		if(userMeta != null) {
			loginLink.setVisible(false);
		}
		return loginLink;
	}

	private Component getAnswerForm() {
		final UserMeta userMeta = WWALDSession.get().getUserMeta();
		Form answerForm = new Form("answer_form") {
			@Override
			public void onSubmit() {
				System.out.println("submitting answer");
				if(userMeta == null) {
					setResponsePage(LoginPage.class);
				}
				else {
					//save the answer in the database
				}
			}
		};
		TextArea answerTextArea = new TextArea("answer_field", new PropertyModel(this.answer, "contents"));
		answerForm.add(answerTextArea);
		if(userMeta == null) {
			answerForm.setVisible(false);
		}
		return answerForm;
	}

}
