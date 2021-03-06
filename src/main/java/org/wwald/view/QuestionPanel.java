package org.wwald.view;

import java.sql.Connection;
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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.model.Answer;
import org.wwald.model.AnswerStatistics;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Question;
import org.wwald.model.QuestionStatistics;
import org.wwald.model.QuestionStatisticsBuilder;
import org.wwald.model.UserMeta;
import org.wwald.service.ApplicationFacade;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

import static org.wwald.ForumConstants.*;

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
				add(new Label(QUESTION_TITLE, question.getTitle()));
				String formattedQuestion = 
					WWALDApplication.get().getMarkDown().
						process(question.getContents());
				add(new Label(QUESTION_CONTENTS, formattedQuestion).
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
			new QuestionStatisticsPanel(QUESTION_STATISTICS, 
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
			new AjaxCheckBox(QUESTION_ANSWERED, 
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
		questionAnsweredCheckbox.add(new Label(QUESTION_ANSWERED_LABEL, "Mark question as answered"));
		return questionAnsweredCheckbox;
	}

	private Component getAnswersList(IDataFacade dataFacade, 
									 String databaseId, 
									 int questionId) throws DataException {
		Connection conn = ConnectionPool.getConnection(databaseId);
		List<Answer> answers =  
			dataFacade.retreiveAnswersForQuestion(conn, questionId);
		
		ListView answersListView = new ListView(ANSWERS, answers) {

			@Override
			protected void populateItem(ListItem item) {
				Answer answer = (Answer)item.getModelObject();
				String transformedAnswer = 
					WWALDApplication.get().getMarkDown().process(answer.getContents());
				Label answerLabel = new Label(ANSWER, transformedAnswer);
				
				item.add(answerLabel.setEscapeModelStrings(false));
				item.add(getAnswerStatisticsPanel(answer));
			}

			private Component getAnswerStatisticsPanel(Answer answer) {
				try {
					String databaseId = getDatabaseId();
					Connection conn = ConnectionPool.getConnection(databaseId);
					long timestamp = getDataFacade().retreiveAnswerTimestamp(conn, answer.getId());
					
					AnswerStatistics answerStatistics = new AnswerStatistics();
					answerStatistics.setUser(answer.getUserMeta());
					answerStatistics.setTimestamp(timestamp);
					 
					AnswerStatisticsPanel answerStatisticsPanel = 
						new AnswerStatisticsPanel(ANSWER_STATISTICS, 
												  answerStatistics);
					return answerStatisticsPanel;
				} catch(DataException de) {
					return new EmptyPanel(ANSWER_STATISTICS);
				}
			}			
		};
	
		return answersListView;
	}

	private Component getLogInLink() {
		BookmarkablePageLink loginLink = 
			new BookmarkablePageLink(LOGIN_TO_ANSWER, LoginPage.class);
		loginLink.add(new Label(LOGIN_TO_ANSWER_PANEL, 
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
		Form answerForm = new Form(ANSWER_FORM) {
			@Override
			public void onSubmit() {
				if(userMeta == null) {
					setResponsePage(LoginPage.class);
				}
				else {
					ApplicationFacade appFacade = WWALDApplication.get().getApplicationFacade();
					ServletWebRequest request = (ServletWebRequest)getRequest();
					String requestUrl = 
						request.getHttpServletRequest().getRequestURL().toString();
					String databaseId = 
						ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
					Connection conn = ConnectionPool.getConnection(databaseId);
					
					try {
						appFacade.answerQuestion(conn, answer);						
						PageParameters pageParameters = new PageParameters();
						pageParameters.add(FORUM_PAGE_PARAM, forumId);
						pageParameters.add(QUESTION_PAGE_PARAM, questionId);
						setResponsePage(ForumPage.class, pageParameters);
					} catch(DataException de) {
						String msg = "Could not save answer";
						cLogger.error(msg, de);
					}
				}
			}
		};
		TextArea answerTextArea = 
			new TextArea(ANSWER_FIELD, 
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
