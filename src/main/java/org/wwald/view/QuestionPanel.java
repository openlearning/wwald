package org.wwald.view;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.Answer;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Question;
import org.wwald.model.QuestionStatistics;
import org.wwald.model.QuestionStatisticsBuilder;
import org.wwald.model.Role;
import org.wwald.model.UserMeta;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;
import org.wwald.service.Sql;
import org.wwald.view.components.AccessControlledViewPageLink;

public class QuestionPanel extends BasePanel {

	private String questionId;
	private Answer answer;
	private boolean questionAnswered;
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
			this.questionId = questionId;
			
			this.answer = new Answer(iQuestionId);
			
			IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
			ServletWebRequest request = (ServletWebRequest)getRequest();
			String requestUrl = 
				request.getHttpServletRequest().getRequestURL().toString();
			String databaseId = 
				ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
			Connection conn = ConnectionPool.getConnection(databaseId);
			
			initQuestionAnswered(conn);
			
			Question question = dataFacade.retreiveQuestion(conn, 
															forumId, 
															iQuestionId);
			if(question != null) {
				add(new Label("question_title", question.getTitle()));
				String formattedQuestion = 
					WWALDApplication.get().getMarkDown().
						transform(question.getContents());
				add(new Label("question_contents", formattedQuestion).
							setEscapeModelStrings(false));
				add(getQuestionStatistics(question));
				add(getQuestionAnsweredCheckbox(question.getUserMeta()));
				add(getAnswersList(dataFacade, databaseId, iQuestionId));
				add(getLogInLink());
				add(getAnswerForm(forumId, questionId));
			} else {
				String msg = "Question for forumId '" + forumId + 
							 "' questionId '" + iQuestionId + "' is null";
				cLogger.error(msg);
				setResponsePage(GenericErrorPage.class);
			}
		} catch(Exception e) {
			String msg = "Could not construct QuestionPanel";
			cLogger.error(msg, e);
			setResponsePage(GenericErrorPage.class);
		}
	}

	private Component getQuestionStatistics(Question question) 
		throws DataException {
		
		QuestionStatistics questionStatistics = 
			QuestionStatisticsBuilder.
				buildQuestionStatistics(question, 
										WWALDApplication.get().getDataFacade(), 
										getDatabaseId());
		
		QuestionStatisticsPanel questionStatisticsPanel = 
			new QuestionStatisticsPanel("question_statistics", 
										questionStatistics);
		return questionStatisticsPanel;
	}

	private void initQuestionAnswered(Connection conn) throws DataException {
		IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
		int iQuestionId = Integer.parseInt(this.questionId);
		this.questionAnswered = 
			dataFacade.isQuestionAnswered(conn, iQuestionId);
	}

	private Component getQuestionAnsweredCheckbox(UserMeta userMeta) {
		AjaxCheckBox questionAnsweredCheckbox = 
			new AjaxCheckBox("question_answered", 
							new PropertyModel(QuestionPanel.this, "questionAnswered")) {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
				ServletWebRequest request = (ServletWebRequest)getRequest();
				String requestUrl = 
					request.getHttpServletRequest().getRequestURL().toString();
				String databaseId = 
					ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
				Connection conn = ConnectionPool.getConnection(databaseId);
				try {
					int iQuestionId = Integer.parseInt(questionId);
					if(questionAnswered) {
						dataFacade.markQuestionAsAnswered(conn, iQuestionId);
					} else {
						dataFacade.markQuestionAsUnanswered(conn, iQuestionId);
					}
				} 
				catch(DataException de) {
					cLogger.error("Could not change the answered status of this question " + de);
				}
			}
			
		};
		UserMeta loggedInUser = WWALDSession.get().getUserMeta();
		if(loggedInUser == null || !loggedInUser.equals(userMeta)) {
			questionAnsweredCheckbox.setVisible(false);
		}
		questionAnsweredCheckbox.add(new Label("question_answered_label", "Mark question as answered"));
		return questionAnsweredCheckbox;
	}

	private Component getAnswersList(IDataFacade dataFacade, 
									 String databaseId, 
									 int questionId) throws DataException {
		Connection conn = ConnectionPool.getConnection(databaseId);
		List<Answer> answers =  
			dataFacade.retreiveAnswersForQuestion(conn, questionId);
		
		ListView answersListView = new ListView("answers", answers) {

			@Override
			protected void populateItem(ListItem item) {
				Answer answer = (Answer)item.getModelObject();
				String transformedAnswer = 
					WWALDApplication.get().getMarkDown().transform(answer.getContents());
				Label answerLabel = new Label("answer", transformedAnswer);				
				item.add(answerLabel.setEscapeModelStrings(false));
			}			
		};
	
		return answersListView;
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

	private Component getAnswerForm(final String forumId, 
									final String questionId) {
		final UserMeta userMeta = WWALDSession.get().getUserMeta();
		Form answerForm = new Form("answer_form") {
			@Override
			public void onSubmit() {
				if(userMeta == null) {
					setResponsePage(LoginPage.class);
				}
				else {
					IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
					ServletWebRequest request = (ServletWebRequest)getRequest();
					String requestUrl = 
						request.getHttpServletRequest().getRequestURL().toString();
					String databaseId = 
						ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
					Connection conn = ConnectionPool.getConnection(databaseId);
					
					try {
						dataFacade.insertAnswer(conn, answer);
						PageParameters pageParameters = new PageParameters();
						pageParameters.add("forum", forumId);
						pageParameters.add("question", questionId);
						setResponsePage(ForumPage.class, pageParameters);
					} catch(DataException de) {
						String msg = "Could not save answer";
						cLogger.error(msg, de);
					}
				}
			}
		};
		TextArea answerTextArea = 
			new TextArea("answer_field", 
						 new PropertyModel(this.answer, "contents"));
		answerForm.add(answerTextArea);
		if(userMeta == null) {
			answerForm.setVisible(false);
		}
		return answerForm;
	}
	
	public boolean getQuestionAnswred() {
		return this.questionAnswered;
	}
	
	public void setQuestionAnswered(boolean questionAnswered) {
		this.questionAnswered = questionAnswered;
	}

}
