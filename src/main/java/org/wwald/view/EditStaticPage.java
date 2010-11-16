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

public class EditStaticPage extends AccessControlledPage {
	
	public EditStaticPage(PageParameters parameters) {
		super(parameters);
		try {
			String pageBeingEdited = parameters.getString(WicketIdConstants.PAGE);
			
			if(pageBeingEdited != null && !pageBeingEdited.equals("")) {
				add(getEditAboutForm(pageBeingEdited));
			} else {
				gotoGenericErrorPage(parameters);
			}
			
		} catch(DataException de) {
			gotoGenericErrorPage(parameters);
		}
	}

	private Form getEditAboutForm(final String path) throws DataException {
		Form editCoursesForm = new Form(WicketIdConstants.EDIT_STATIC_PAGE_FORM) {
			@Override
			public void onSubmit() {
				
				PageParameters parameters = getPageParameters();
				if(parameters == null) {
					parameters = new PageParameters();
				}
				
				try {
					//TODO: Get by name and not index
					TextArea textArea = (TextArea)get(0);
					WWALDApplication app = (WWALDApplication)getApplication();
					StaticPagePOJO page = new StaticPagePOJO(path, (String)textArea.getModelObject());
					app.getDataFacade().upsertStaticPage(ConnectionPool.getConnection(getDatabaseId()), page);
					
				} catch(DataException de) {
					String msg = "Sorry we could not perform the action you requested, " +
								 "due to an internal error. We will look into this issue as soon as we can";					
					parameters.add(WicketIdConstants.MESSAGES, msg);
				}
				
				parameters.add(WicketIdConstants.PAGE, path);
				setResponsePage(StaticPage.class, parameters);
			}
		};
		WWALDApplication app = (WWALDApplication)getApplication();
		StaticPagePOJO page = app.getDataFacade().retreiveStaticPage(ConnectionPool.getConnection(getDatabaseId()), path);
		TextArea editCoursesFormTextArea = new TextArea(WicketIdConstants.EDIT_STATIC_PAGE_FORM_TEXTAREA, new Model(page.getContents()));
		editCoursesForm.add(editCoursesFormTextArea);
		return editCoursesForm;
	}
	
	//TODO: Move this to PageUtils so all web pages can access it
	private void gotoGenericErrorPage(PageParameters parameters) {
		String msg = "We cannot display this page because an " +
					 "internal error has occured. We will look " +
					 "into this as soon as we can.";
		parameters.add(WicketIdConstants.MESSAGES, msg);
		setResponsePage(GenericErrorPage.class, parameters);
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.EDIT_COURSES;
	}
}
