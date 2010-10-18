package org.wwald.model;

import java.util.Date;

public class ApplicationFacade {
	
	private IDataFacade dataFacade;
	
	public ApplicationFacade(IDataFacade dataFacade) {
		this.dataFacade = dataFacade;
	}
	
	public User login(String username, String password) {
		return dataFacade.retreiveUser(username, password);
	}
}
