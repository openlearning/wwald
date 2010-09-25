/**
 * 
 */
package org.wwald;

import java.io.Serializable;

/**
 * @author pshah
 *
 */
public class StatusUpdate implements Serializable {
	private String text;
	
	public StatusUpdate(String text) {
		this.text = text;
	}
	
	public StatusUpdate() {
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
