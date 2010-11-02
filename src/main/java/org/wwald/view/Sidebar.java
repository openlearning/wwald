package org.wwald.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class Sidebar extends Panel {

	public Sidebar(String id, BasePage viewPage) {
		super(id);
		add(new Label("sidebar_title", "Links"));
	}

}
