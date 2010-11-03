package org.wwald.view;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Permission;
import org.wwald.model.StaticPagePOJO;
import org.wwald.service.DataException;

public class EditAbout extends AccessControlledPage {
	
	public EditAbout(PageParameters parameters) {
		super(parameters);
		try {
			String path = getRequest().getPath();
			add(getEditAboutForm(path));
		} catch(DataException de) {
			String msg = "We cannot display this page because an " +
						 "internal error has occured. We will look " +
						 "into this as soon as we can.";
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
	}

	private Form getEditAboutForm(final String path) throws DataException {
		Form editCoursesForm = new Form(WicketIdConstants.EDIT_ABOUT_FORM) {
			@Override
			public void onSubmit() {
				try {
					//TODO: Get by name and not index
					TextArea textArea = (TextArea)get(0);
					WWALDApplication app = (WWALDApplication)getApplication();
					StaticPagePOJO page = new StaticPagePOJO("about", (String)textArea.getModelObject());
					app.getDataFacade().upsertStaticPage(ConnectionPool.getConnection(), page);
					
				} catch(DataException de) {
					String msg = "Sorry we could not perform the action you requested, " +
								 "due to an internal error. We will look into this issue as soon as we can";
					PageParameters pageParams = getPage().getPageParameters();
					if(pageParams != null) {
						pageParams.add(WicketIdConstants.MESSAGES, msg);
					}
				}
				//TODO: When we make this generic we must change the page we redirect to based on which page was serviced
				setResponsePage(StaticPagePojo.class);
			}
		};
		WWALDApplication app = (WWALDApplication)getApplication();
		StaticPagePOJO page = app.getDataFacade().retreiveStaticPage(ConnectionPool.getConnection(), "about");
		TextArea editCoursesFormTextArea = new TextArea(WicketIdConstants.EDIT_ABOUT_FORM_TEXTAREA, new Model(page.getContents()));
		editCoursesForm.add(editCoursesFormTextArea);
		return editCoursesForm;
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.EDIT_COURSES;
	}
}
