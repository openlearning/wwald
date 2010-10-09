package org.wwald.view;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WicketIdConstants;


public abstract class BasePage extends WebPage {
	Panel sidebar;
	
	public BasePage(PageParameters parameters) {
		this.sidebar = getSidebar();
		add(this.sidebar);
		add(new HeaderPanel(WicketIdConstants.HEADER_PANEL));
		add(new FooterPanel(WicketIdConstants.FOOTER_PANEL));
	}
	
	public void replaceSidebar(Panel sidebar) {
		Panel temp = this.sidebar;
		temp.replaceWith(sidebar);
		this.sidebar = sidebar;
	}
	
	public abstract Panel getSidebar();
}
