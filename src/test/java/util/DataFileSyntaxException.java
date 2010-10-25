package util;

public class DataFileSyntaxException extends Exception {
	
	public DataFileSyntaxException() {
		super();
	}
	
	public DataFileSyntaxException(String msg) {
		super(msg);
	}
	
	public DataFileSyntaxException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DataFileSyntaxException(Throwable t) {
		super(t);
	}
}
