package org.wwald.view;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.wwald.WWALDApplication;

public class EditCourses extends WebPage {
	public EditCourses() {
		add(getCoursesEditForm());
	}

	private Form getCoursesEditForm() {
		Form editCoursesForm = new Form("courses.edit.form") {
			@Override
			public void onSubmit() {
				TextArea textArea = (TextArea)get(0);
				WWALDApplication app = (WWALDApplication)getApplication();
				app.getDataStore().updateCoursesWikiContents(textArea.getModelObject());
				setResponsePage(HomePage.class);
			}
		};
		TextArea editCoursesFormTextArea = new TextArea("courses.edit.form.textarea", new Model(getCoursesWikiContents()));
		editCoursesForm.add(editCoursesFormTextArea);
		return editCoursesForm;
	}

	private String getCoursesWikiContents() {
		WWALDApplication app = (WWALDApplication)getApplication();
		return app.getDataStore().getCoursesWikiContents();
	}
}
