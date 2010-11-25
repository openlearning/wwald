package org.wwald.view;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.wwald.WicketIdConstants;

public class ErrorPageInternal extends BasePage{

	public ErrorPageInternal(PageParameters parameters) {
		super(parameters);
		String msg = "We are sorry, an internal error has occurred. " +
				     "The administrator has been notified, we will fix it as soon as we can. " +
				     "Thank you and please visit us again.";
		
		add(new Label(WicketIdConstants.INTERNAL_ERROR_MESSAGE, msg));
	}

}
