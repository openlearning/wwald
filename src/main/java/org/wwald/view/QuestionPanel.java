package org.wwald.view;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Question;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

public class QuestionPanel extends Panel {

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
			} else {
				String msg = "Question for forumId '" + forumId + 
							 "' questionId '" + iQuestionId + "' is null";
				cLogger.error(msg);
				setResponsePage(GenericErrorPage.class);
			}
		} catch(Exception e) {
			
		}
	}

}
