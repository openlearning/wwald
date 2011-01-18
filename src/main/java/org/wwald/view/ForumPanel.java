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

import static org.wwald.ForumConstants.*;

public class ForumPanel extends BasePanel {

	private static transient Logger cLogger = Logger.getLogger(ForumPanel.class);
	
	public ForumPanel(String id, String forumId) {
		super(id);
		
		IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
		
		ServletWebRequest request = (ServletWebRequest)getRequest();
		String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
		String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
		
		try {
			Connection conn = ConnectionPool.getConnection(databaseId);
			Forum forum = dataFacade.retreiveDiscussionForum(conn, forumId);
			if(forum != null) {
				add(new Label(FORUM_TITLE, forum.getTitle()));
				add(new Label(FORUM_DESCRIPTION, forum.getDescription()));
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
		return new ListView(FORUM_QUESTIONS, questions) {
	
			@Override
			protected void populateItem(ListItem item) {
				Question question = (Question)item.getModelObject();
				
				PageParameters pageParameters = new PageParameters();
				pageParameters.add(FORUM_PAGE_PARAM, forumId);
				pageParameters.add(QUESTION_PAGE_PARAM, String.valueOf(question.getId()));
				
				Link questionLink = 
					new BookmarkablePageLink(QUESTION_LINK, 
											 ForumsPage.class,
											 pageParameters);
				questionLink.add(new Label(QUESTION_TITLE, question.getTitle()));
				
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
							new QuestionStatisticsPanel(QUESTION_STATISTICS, 
														questionStatistics);
				questionStatisticsPanel.setUserImageVisible(false);
				item.add(questionStatisticsPanel);
			}
			
		};
	}

}
