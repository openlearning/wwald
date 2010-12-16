package org.wwald.view;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.User;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

public class ChangePasswordPanel extends Panel {
	
	private transient static final Logger cLogger = Logger.getLogger(ChangePasswordPanel.class);
	
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
				String origPassword = validatable.getValue();
				String passwordInDb = dataFacade.retreivePassword(conn, this.user.getUsername());
				BasicPasswordEncryptor passwordEncrypter = new BasicPasswordEncryptor();				
				if(!passwordEncrypter.checkPassword(origPassword, passwordInDb)) {
					error(validatable);
				}				
			} catch(DataException de) {
				error(validatable);
			}
		}
		
	}

	public ChangePasswordPanel(String id, User user) {
		super(id);
		Form changePasswordForm = getChangePasswordForm(user);
		add(new FeedbackPanel(WicketIdConstants.MESSAGES));
		add(changePasswordForm);
	}
	
	private Form getChangePasswordForm(final User user) {
		final IModel oldPwdModel = new Model();
		final IModel newPwdModel = new Model();
		final IModel retypeNewPwdModel = new Model();
		final PasswordTextField oldPwdField;
		final PasswordTextField newPwdField;
		final PasswordTextField retypeNewPwdField;
		
		Form changePasswordForm = new Form(WicketIdConstants.PROFILE_CHANGE_PWD_FORM) {
			
			@Override
			public void onSubmit() {
				String databaseId = ConnectionPool.getDatabaseIdFromRequest((ServletWebRequest)getRequest());
				Connection conn = ConnectionPool.getConnection(databaseId);
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
		oldPwdField.
			add(new UserExistsValidator(ConnectionPool.getDatabaseIdFromRequest((ServletWebRequest)getRequest()), 
										user));
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
}
