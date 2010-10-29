package org.wwald.service;

public class ApplicationException extends Exception {
	
	public ApplicationException() {
		super();
	}
	
	public ApplicationException(String msg) {
		super(msg);
	}
	
	public ApplicationException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public ApplicationException(Throwable t) {
		super(t);
	}
}
