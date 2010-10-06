package org.wwald.view;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.wwald.WWALDApplication;
import org.wwald.model.Competency;

public class EditLecture extends BasePage {
	public EditLecture(PageParameters pageParams) {
		super(pageParams);
		String courseId = pageParams.getString(HomePage.SELECTED_COURSE);
		String sCompetencyId = pageParams.getString(HomePage.SELECTED_COMPETENCY);
		Competency competency = getCompetency(courseId, sCompetencyId);
		add(new Label("course.name", courseId));
		add(getCompetencyEditForm(courseId, competency, pageParams));
	}

	private Form getCompetencyEditForm(final String courseId, final Competency competency, final PageParameters pageParams) {
		Form editCompetencyDescriptionForm = new Form("lecture.edit.form") {
			@Override
			public void onSubmit() {
				TextArea editCompetencyDescriptionTextArea = (TextArea)get(0);
				TextArea editCompetencyResourcesTextArea = (TextArea)get(1);
				
				WWALDApplication app = (WWALDApplication)getApplication();
				
				competency.setDescription((String)editCompetencyDescriptionTextArea.getModelObject());
				competency.setResource((String)editCompetencyResourcesTextArea.getModelObject());
				app.getDataStore().updateCompetency(courseId, competency);
				
				setResponsePage(CoursePage.class, pageParams);
			}
		};
		
		TextArea editCompetencyDescriptionTextArea = new TextArea("lecture.edit.form.description.textarea", new Model(competency.getDescription()));
		editCompetencyDescriptionForm.add(editCompetencyDescriptionTextArea);
		
		TextArea editCompetencyResourcesTextArea = new TextArea("lecture.edit.form.resources.textarea", new Model(competency.getResource()));
		editCompetencyDescriptionForm.add(editCompetencyResourcesTextArea);
		
		return editCompetencyDescriptionForm;
	}

	private Competency getCompetency(String courseId, String sCompetencyId) {
		WWALDApplication app = (WWALDApplication)getApplication();
		Competency competency = app.getDataStore().getCompetency(courseId, sCompetencyId);
		return competency;
	}

	@Override
	public Panel getSidebar() {
		return new EmptyPanel("rhs_sidebar");
	}
}
