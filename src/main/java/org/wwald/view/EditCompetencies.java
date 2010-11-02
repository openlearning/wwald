package org.wwald.view;

import java.io.Serializable;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Permission;
import org.wwald.service.DataException;

public class EditCompetencies extends AccessControlledPage {
	public EditCompetencies(PageParameters parameters) {
		super(parameters);
		try {
			String courseId = parameters.getString(WWALDConstants.SELECTED_COURSE);
			add(new Label("course.name", courseId));
			add(getCoursesEditForm(courseId, parameters));
		} catch(DataException de) {
			String msg = "Sorry we could not perform the requested action, due to an internal error. We will look into this issue as soon as we can";
			parameters.add(WicketIdConstants.MESSAGES, msg);
			//TODO: Find out how to access page history and go to the page from where we came
			setResponsePage(CoursePage.class, parameters);
		}
	}

	private Form getCoursesEditForm(final String courseId, final PageParameters pageParams) throws DataException {
		Form editCompetenciesForm = new Form("competencies.edit.form") {
			@Override
			public void onSubmit() {
				try {
					TextArea textArea = (TextArea)get(0);
					WWALDApplication app = (WWALDApplication)getApplication();
					app.getDataFacade().updateCompetenciesWikiContents(ConnectionPool.getConnection(), courseId, (String)textArea.getModelObject());
					setResponsePage(CoursePage.class, pageParams);
				} catch(DataException de) {
					String msg = "Sorry we could not perform the action you requested " +
								 "due to an internal error. We will look into this " +
								 "issue as soon as we can";
					pageParams.add(WicketIdConstants.MESSAGES, msg);
					setResponsePage(GenericErrorPage.class, pageParams);
				}
			}
		};
		TextArea editCompetenciesFormTextArea = new TextArea("competencies.edit.form.textarea", 
															 new Model(getCompetenciesWikiContents(courseId)));
		editCompetenciesForm.add(editCompetenciesFormTextArea);
		return editCompetenciesForm;
	}

	private Serializable getCompetenciesWikiContents(String courseId) throws DataException {
		WWALDApplication app = (WWALDApplication)getApplication();
		return app.getDataFacade().retreiveCompetenciesWiki(ConnectionPool.getConnection(), courseId);
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.EDIT_COURSE;
	}
}
