package org.wwald.view;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.wwald.WWALDApplication;
import org.wwald.model.ConnectionPool;
import org.wwald.model.User;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

public class DuplicateUsernameValidator extends AbstractValidator {
	
	private String databaseId;
	//TODO: What is the implication of making this field transient?
	transient private IDataFacade dataFacade;
	//TODO: What is the implication of making this field transient?
	private transient Logger cLogger = Logger.getLogger(DuplicateUsernameValidator.class);	
	
	public DuplicateUsernameValidator(String databaseId, IDataFacade dataFacade) {
		this.databaseId = databaseId;
		this.dataFacade = dataFacade;
	}
	
	@Override
	protected void onValidate(IValidatable validatable) {
		String username = (String)validatable.getValue();
				
		try {
			User existingUser = this.dataFacade.retreiveUserByUsername(ConnectionPool.getConnection(databaseId), 
																	   username);
			if(!(existingUser == null || existingUser.getUsername().equals(null) || existingUser.getUsername().equals(""))) {
				error(validatable);
			}
			
		} catch(DataException de) {
			cLogger.error("Caught Exception while trying to get existing user " + de);
			//TODO: How should we handle this exception?
		}
	}
}
