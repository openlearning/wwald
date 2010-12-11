package org.wwald.view;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;
import org.wwald.view.UserForm.Field;

public class ProfilePage extends BasePage {
	
	private transient Logger cLogger = Logger.getLogger(ProfilePage.class);
	
	public static class UserExistsValidator extends AbstractValidator<String> {
		
		private String databaseId;
		private User user;
		
		public UserExistsValidator(String databaseId, User user) {
			this.databaseId = databaseId;
			this.user = user;
		}
		
		@Override
		protected void onValidate(IValidatable<String> validatable) {
			Connection conn = ConnectionPool.getConnection(databaseId);
			IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
			try {
				User verifiedUser = dataFacade.retreiveUser(conn, this.user.getUsername(), validatable.getValue());
				if(verifiedUser == null) {
					error(validatable);
				}				
			} catch(DataException de) {
				error(validatable);
			}
		}
		
	}
	
	public ProfilePage(PageParameters parameters) {
		super(parameters);
		try {
			User userInSession =  WWALDSession.get().getUser();
			add(getUserFormPanel(userInSession));
			add(new FeedbackPanel(WicketIdConstants.MESSAGES));
			add(getChangePasswordForm(userInSession));
		} catch(DataException de) {
			String msg = "Sorry but we could not process the request due to an error. We will look into this as soon as we can.";
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
	}
	
	private Component getChangePasswordForm(final User user) {
		final IModel oldPwdModel = new Model();
		final IModel newPwdModel = new Model();
		final IModel retypeNewPwdModel = new Model();
		final PasswordTextField oldPwdField;
		final PasswordTextField newPwdField;
		final PasswordTextField retypeNewPwdField;
		
		Form changePasswordForm = new Form(WicketIdConstants.PROFILE_CHANGE_PWD_FORM) {
			
			@Override
			public void onSubmit() {
				Connection conn = ConnectionPool.getConnection(getDatabaseId());
				IDataFacade dataFacade = WWALDApplication.get().getDataFacade();
				try {
					user.setPassword((String)newPwdModel.getObject());
					dataFacade.updateUser(conn, user, UserForm.Field.PASSWORD);
				} catch(DataException de) {
					String msg = "Could not update password due to an Exception";
					cLogger.error(msg);
				}
			}
			
		};
		
		
		oldPwdField = new PasswordTextField(WicketIdConstants.OLD_PWD, oldPwdModel);
		oldPwdField.add(new UserExistsValidator(getDatabaseId(), user));
		newPwdField = new PasswordTextField(WicketIdConstants.NEW_PWD, newPwdModel);
		//TODO: We are setting the label to provide a better error msg when the pwd and retype pwd fields do not have the same value
		//Udeally this should be controlled through a message in a property file
		newPwdField.setLabel(new Model("New Password"));
		retypeNewPwdField = new PasswordTextField(WicketIdConstants.RETYPE_NEW_PWD, retypeNewPwdModel);
		retypeNewPwdField.setLabel(new Model("Retype New Password"));
		
		changePasswordForm.add(oldPwdField);
		changePasswordForm.add(newPwdField);
		changePasswordForm.add(retypeNewPwdField);
		changePasswordForm.add(new EqualPasswordInputValidator(newPwdField, retypeNewPwdField));
		
		return changePasswordForm;
	}

	private Component getUserFormPanel(User userInSession) throws DataException {				
		Connection conn = ConnectionPool.getConnection(getDatabaseId());
		User user = WWALDApplication.get().getDataFacade().retreiveUserByUsername(conn, userInSession.getUsername());
		UserFormPanel userFormPanel = 
			new UserFormPanel(WicketIdConstants.PROFILE_USER_DETAILS, 
							  user, getUserFieldsToUpdate());
		userFormPanel.setFieldEditable(UserForm.Field.USERNAME, false);
		userFormPanel.setFieldEditable(UserForm.Field.ROLE, false);
		userFormPanel.setFieldEditable(UserForm.Field.PASSWORD, false);
		userFormPanel.setFieldVisible(UserForm.Field.PASSWORD, false);
		userFormPanel.setFieldEditable(UserForm.Field.REPEAT_PASSWORD, false);
		userFormPanel.setFieldVisible(UserForm.Field.REPEAT_PASSWORD, false);
		userFormPanel.setRoleChoices(Role.ADMIN, Role.MENTOR, Role.STUDENT);
		
		
		
		return userFormPanel;
	}

	private Field[] getUserFieldsToUpdate() {
		return new UserForm.Field[] {Field.FIRST_NAME,  
				 					 Field.LAST_NAME,
				 					 Field.EMAIL};
	}

}
