package org.wwald.service;

public class CannotPerformActionException extends Exception {
	
	public CannotPerformActionException() {
		super();
	}
	
	public CannotPerformActionException(String msg) {
		super(msg);
	}
}
