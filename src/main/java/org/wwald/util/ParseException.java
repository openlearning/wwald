package org.wwald.util;

public class ParseException extends Exception {
	
	private int rowOffset;
	private int columnOffset;
	
	public ParseException() {
		super();
	}
	
	public ParseException(String msg) {
		super(msg);
	}
	
	public ParseException(String msg, int rowOffset, int columnOffset) {
		super(msg);
		this.rowOffset = rowOffset;
		this.columnOffset = columnOffset;
	}
	
	public ParseException(Throwable t) {
		super(t);
	}
	
	public ParseException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public int getRowOffset() {
		return this.rowOffset;
	}
	
	public int getColumnOffset() {
		return this.columnOffset;
	}
}
