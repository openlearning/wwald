package org.wwald.view;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;


public abstract class BasePage extends WebPage {
	public BasePage(PageParameters parameters) {
		add(getSidebar());
	}
	public abstract Component getSidebar();
}
