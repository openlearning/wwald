package org.wwald.view;

import javax.swing.text.html.HTMLDocument.HTMLReader.ParagraphAction;

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
import org.wwald.model.Competency;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Permission;
import org.wwald.service.DataException;

public class EditLecture extends AccessControlledPage {
	public EditLecture(PageParameters pageParams) {
		super(pageParams);
		try {
			String courseId = pageParams.getString(WWALDConstants.SELECTED_COURSE);
			String sCompetencyId = pageParams.getString(WWALDConstants.SELECTED_COMPETENCY);
			Competency competency = getCompetency(courseId, sCompetencyId);
			add(new Label("course.name", courseId));
			add(getCompetencyEditForm(courseId, competency, pageParams));
		} catch(DataException de) {
			String msg = "Sorry could not display page because a internal error has occured";
			pageParams.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, pageParams);
		}
	}

	private Form getCompetencyEditForm(final String courseId, final Competency competency, final PageParameters pageParams) {
		Form editCompetencyDescriptionForm = new Form("lecture.edit.form") {
			@Override
			public void onSubmit() {
				try {
					TextArea editCompetencyDescriptionTextArea = (TextArea)get(0);
					TextArea editCompetencyResourcesTextArea = (TextArea)get(1);
					
					WWALDApplication app = (WWALDApplication)getApplication();
					
					competency.setDescription((String)editCompetencyDescriptionTextArea.getModelObject());
					competency.setResource((String)editCompetencyResourcesTextArea.getModelObject());
					app.getDataFacade().updateCompetency(ConnectionPool.getConnection(), courseId, competency);
					
					setResponsePage(CoursePage.class, pageParams);
				} catch(DataException de) {
					String msg = "Sorry we could not perform the action you requested, due to an internal error. We will look into this problem as soon as we can.";
					pageParams.add(WicketIdConstants.MESSAGES, msg);
					setResponsePage(GenericErrorPage.class, pageParams);
				}
			}
		};
		
		TextArea editCompetencyDescriptionTextArea = new TextArea("lecture.edit.form.description.textarea", new Model(competency.getDescription()));
		editCompetencyDescriptionForm.add(editCompetencyDescriptionTextArea);
		
		TextArea editCompetencyResourcesTextArea = new TextArea("lecture.edit.form.resources.textarea", new Model(competency.getResource()));
		editCompetencyDescriptionForm.add(editCompetencyResourcesTextArea);
		
		return editCompetencyDescriptionForm;
	}

	private Competency getCompetency(String courseId, String sCompetencyId) throws DataException {
		WWALDApplication app = (WWALDApplication)getApplication();
		Competency competency = app.getDataFacade().retreiveCompetency(ConnectionPool.getConnection(), courseId, sCompetencyId);
		return competency;
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.EDIT_COURSE;
	}
}
