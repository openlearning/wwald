package org.wwald.service;

public class DataException extends Exception {
	
	public DataException() {
		super();
	}
	
	public DataException(String msg) {
		super(msg);
	}
	
	public DataException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DataException(Throwable t) {
		super(t);
	}
}
