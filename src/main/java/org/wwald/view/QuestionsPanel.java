package org.wwald.view;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Forum;
import org.wwald.model.Question;
import org.wwald.model.QuestionStatistics;
import org.wwald.model.QuestionStatisticsBuilder;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

public class QuestionsPanel extends BasePanel {

	private static transient Logger cLogger = Logger.getLogger(QuestionsPanel.class);
	
	public QuestionsPanel(String id, String forumId) {
		super(id);
		
		IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
		
		ServletWebRequest request = (ServletWebRequest)getRequest();
		String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
		String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
		
		try {
			Connection conn = ConnectionPool.getConnection(databaseId);
			Forum forum = dataFacade.retreiveDiscussionForum(conn, forumId);
			if(forum != null) {
				add(new Label("forum_title", forum.getTitle()));
				add(new Label("forum_description", forum.getDescription()));
				add(getForumQuestions(forumId));
			} else {
				cLogger.error("Could not retreive Forum object for forumId '" 
							  + forumId + "'");
				setResponsePage(GenericErrorPage.class);
			}
		} catch(DataException de) {
			String msg = "Caught Exception";
			cLogger.error(msg, de);
			setResponsePage(GenericErrorPage.class);
		}
	}
	
	private Component getForumQuestions(final String forumId) 
		throws DataException {
	
		IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
		
		ServletWebRequest request = (ServletWebRequest)getRequest();
		String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
		String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
		
		Connection conn = ConnectionPool.getConnection(databaseId);
		Forum forum = new Forum();
		forum.setId(forumId);
		List<Question> questions = dataFacade.retreiveAllQuestionsForForum(conn, 
																	   forum);
		return new ListView("forum_questions", questions) {
	
			@Override
			protected void populateItem(ListItem item) {
				Question question = (Question)item.getModelObject();
				
				PageParameters pageParameters = new PageParameters();
				pageParameters.add("forum", forumId);
				pageParameters.add("question", String.valueOf(question.getId()));
				
				Link questionLink = 
					new BookmarkablePageLink("question_link", 
											 ForumsPage.class,
											 pageParameters);
				questionLink.add(new Label("question_title", question.getTitle()));
				
				item.add(questionLink);
				
				QuestionStatistics questionStatistics = null;
				
				//TODO: We need to handle this Exception in a better way
				try {
				questionStatistics = 
					QuestionStatisticsBuilder.
						buildQuestionStatistics(question, 
												WWALDApplication.get().getDataFacade(), 
												getDatabaseId());
				} catch(DataException de) {
					String msg = "Could not create QuestionStatistics";
					cLogger.error(msg, de);
				}
				
				QuestionStatisticsPanel questionStatisticsPanel = 
							new QuestionStatisticsPanel("question_statistics", 
														questionStatistics);
				item.add(questionStatisticsPanel);
			}
			
		};
	}

}
