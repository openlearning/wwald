package org.wwald.model;

import java.util.Date;

import org.wwald.WWALDSession;

public class ApplicationFacade {
	
	private IDataFacade dataFacade;
	
	public ApplicationFacade(IDataFacade dataFacade) {
		this.dataFacade = dataFacade;
	}
	
	public User login(String username, String password) {
		return dataFacade.retreiveUser(username, password);
	}
	
	public void logout() {
		WWALDSession.get().setUser(null);
	}
}
