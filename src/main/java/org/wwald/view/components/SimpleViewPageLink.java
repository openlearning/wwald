package org.wwald.view.components;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.Link;

public class SimpleViewPageLink extends Link {
	private Class<? extends Page> responseView;
	
	public SimpleViewPageLink(String id) {
		this(id, null);
	}
	
	public SimpleViewPageLink(String id, Class<? extends Page> responseView) {
		super(id);
		this.responseView = responseView;
	}

	@Override
	public void onClick() {
		if(this.responseView != null) {
			setResponsePage(this.responseView);
		}
	}
}
