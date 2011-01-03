package org.wwald.view;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.model.ConnectionPool;
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
		
		IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
		ServletWebRequest request = (ServletWebRequest)getRequest();
		String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
		String databaseId = ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
		Connection conn = ConnectionPool.getConnection(databaseId);
		
		//dataFacade.retreive
		add(new Label("question", "Please help me with my question. Why are there only 24 hours in a day?"));
	}

}
