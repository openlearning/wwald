package org.wwald.view;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Permission;
import org.wwald.service.DataException;

public class EditCourses extends AccessControlledPage {
	
	public EditCourses(PageParameters parameters) {
		super(parameters);
		try {
			add(getCoursesEditForm());
		} catch(DataException de) {
			String msg = "We cannot display this page because an " +
						 "internal error has occured. We will look " +
						 "into this as soon as we can.";
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
	}

	private Form getCoursesEditForm() throws DataException {
		Form editCoursesForm = new Form("courses.edit.form") {
			@Override
			public void onSubmit() {
				try {
					//TODO: Get by name and not index
					TextArea textArea = (TextArea)get(0);
					WWALDApplication app = (WWALDApplication)getApplication();
					app.getDataFacade().updateCourseWiki(ConnectionPool.getConnection(), (String)textArea.getModelObject());
					
				} catch(DataException de) {
					String msg = "Sorry we could not perform the action you requested, " +
								 "due to an internal error. We will look into this issue as soon as we can";
					PageParameters pageParams = getPage().getPageParameters();
					if(pageParams != null) {
						pageParams.add(WicketIdConstants.MESSAGES, msg);
					}
				}
				setResponsePage(HomePage.class);
			}
		};
		WWALDApplication app = (WWALDApplication)getApplication();
		String wikiContents = app.getDataFacade().retreiveCourseWiki(ConnectionPool.getConnection());
		TextArea editCoursesFormTextArea = new TextArea("courses.edit.form.textarea", new Model(wikiContents));
		editCoursesForm.add(editCoursesFormTextArea);
		return editCoursesForm;
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.EDIT_COURSES;
	}
}
