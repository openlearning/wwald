package org.wwald.view;

import org.apache.wicket.PageParameters;
import org.wwald.model.Permission;

public class DBPage extends AccessControlledPage {

	public DBPage(PageParameters parameters) {
		super(parameters);
		add(new DBPanel("db_panel"));
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.MANAGE_DB;
	}

}
