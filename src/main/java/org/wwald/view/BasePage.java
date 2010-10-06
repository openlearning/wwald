package org.wwald.view;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;


public abstract class BasePage extends WebPage {
	Panel sidebar;
	
	public BasePage(PageParameters parameters) {
		this.sidebar = getSidebar();
		add(this.sidebar);
	}
	
	public void replaceSidebar(Panel sidebar) {
		Panel temp = this.sidebar;
		temp.replaceWith(sidebar);
		this.sidebar = sidebar;
	}
	
	public abstract Panel getSidebar();
}
