package org.wwald.model;

import java.sql.Connection;
import java.util.List;

import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

public class QuestionStatisticsBuilder {
	public static QuestionStatistics buildQuestionStatistics(Question question,
															 IDataFacade dataFacade,
															 String databaseId) 
		throws DataException {
		
		QuestionStatistics questionStatistics = new QuestionStatistics(question);
		
		Connection conn = ConnectionPool.getConnection(databaseId);
		//TODO: Introduce a method to give us only the answer count
		List<Answer> answers = 
			dataFacade.retreiveAnswersForQuestion(conn, 
												  question.getId());
		questionStatistics.setNumberOfAnswers(answers.size());
		return questionStatistics;
	}
}
