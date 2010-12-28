package org.wwald.util;

import org.apache.log4j.Logger;
import org.wwald.model.UserMeta;


public class LoginLogger {
	public static final Logger cLogger = Logger.getLogger(LoginLogger.class);
	
	public static void successfullLogin(String username, UserMeta.LoginVia loginVia, String sessionId) {
		cLogger.info("SUCCESS " + username + " " + loginVia + " " + sessionId);
	}
	
	public static void unsuccessfullLoginInternal(String username) {
		cLogger.info("FAIL " + username);
	}
}
