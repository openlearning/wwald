package org.wwald.view;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.validation.validator.EmailAddressPatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.model.UserMeta;
import org.wwald.service.IDataFacade;

public class UserForm extends Form {

	private User origUser;
	private User user;
	private String repeatPassword;

	private Label usernameFieldLabel;
	private TextField usernameField;
	private Label emailFieldLabel;
	private TextField emailField;
	private Label passwordFieldLabel;
	private TextField passwordField;
	private Label repeatPasswordFieldLabel;
	private TextField repeatPasswordField;
	private Label roleFieldLabel;
	private DropDownChoice roleField;
	
	private Field userFieldsToUpdate[];
	
	private Class<? extends Page> responsePage;
	
	//TODO: What is the implication of making this field transient?
	private transient Logger cLogger = Logger.getLogger(UserForm.class);
	
	public enum Field {
		USERNAME("username"),
		EMAIL("email"),
		PASSWORD("password"),
		REPEAT_PASSWORD(""),
		ROLE("role");
		
		private String dbColName;
		
		Field(String dbColName) {
			this.dbColName = dbColName;
		}
		
		public String getDbColName() {
			return this.dbColName;
		}
	}
	
	public UserForm(String id) {
		this(id, new User());
	}
	
	public UserForm(String id, User user, Field... userFields) {
		super(id);
		this.origUser = user;
		this.userFieldsToUpdate = userFields;
		this.user = this.origUser.duplicate();
		addTextFields();
	}

	public void setRoleChoices(Role... roles) {
		List<Role> rolesList = new ArrayList<Role>();
		for(Role role : roles) {
			rolesList.add(role);
		}
		roleField.setChoices(rolesList);
	}
	
	public void setSubmitResponsePage(Class<? extends Page> responsePage) {
		this.responsePage = responsePage;
	}
	
	public void setFieldVisible(Field field, boolean visible) {
		switch(field) {
			case USERNAME:
				this.usernameField.setVisible(visible);
				this.usernameFieldLabel.setVisible(visible);
				break;
			case EMAIL:
				this.emailField.setVisible(visible);
				this.emailFieldLabel.setVisible(visible);
			case PASSWORD:
				this.passwordField.setVisible(visible);
				this.passwordFieldLabel.setVisible(visible);
				break;				
			case REPEAT_PASSWORD:
				this.repeatPasswordField.setVisible(visible);
				this.repeatPasswordFieldLabel.setVisible(visible);
				break;
			case ROLE:
				this.roleField.setVisible(visible);
				this.roleFieldLabel.setVisible(visible);
				break;
			default:
				throw new RuntimeException(field  + " not handled");
		}
	}
	
	public void setFieldEditable(Field field, boolean editable) {
		switch(field) {
			case USERNAME:
				this.usernameField.setEnabled(editable);
				break;
			case EMAIL:
				this.emailField.setEnabled(editable);
			case PASSWORD:
				this.passwordField.setEnabled(editable);
				break;				
			case REPEAT_PASSWORD:
				this.repeatPasswordField.setEnabled(editable);
				break;
			case ROLE:
				this.roleField.setEnabled(editable);
				break;
			default:
				throw new RuntimeException(field  + " not handled");
		}
	}
	
	@Override
	public void onSubmit() {
		try {
			IDataFacade dataFacade = ((WWALDApplication)Application.get()).getDataFacade();
			Connection conn = ConnectionPool.getConnection(getDatabaseId());
			if(this.origUser.getUsername() == null || this.origUser.getUsername().equals("")) {
				UserMeta userMeta = new UserMeta();
				userMeta.setIdentifier(this.user.getUsername());
				userMeta.setLoginVia(UserMeta.LoginVia.INTERNAL);
				dataFacade.insertUser(conn, this.user, userMeta);
			}
			else {
				dataFacade.updateUser(conn, this.user, userFieldsToUpdate);
			}			
			if(this.responsePage != null) {
				setResponsePage(this.responsePage);
			}
		} catch(Exception de) {
			String msg = "Sorry we could not perform the action you requested " +
			 "due to an internal error. We will look into this " +
			 "issue as soon as we can";
			PageParameters parameters = new PageParameters();
			parameters.add(WicketIdConstants.MESSAGES, msg);
			setResponsePage(GenericErrorPage.class, parameters);
		}
	}
	
	private void addTextFields() {
		this.usernameFieldLabel = new Label(WicketIdConstants.USER_DETAILS_FORM_USERNAME_LABEL, "Username *"); 
		add(this.usernameFieldLabel);
		this.usernameField = 
			new RequiredTextField(WicketIdConstants.USER_DETAILS_FORM_USERNAME_FIELD, 
						  new PropertyModel(this.user, "username"));
		this.usernameField.add(StringValidator.lengthBetween(6, 16));
		this.usernameField.add(new DuplicateUsernameValidator(getDatabaseId(), getDataFacade()));
		this.usernameField.setLabel(new Model("Username"));
		add(this.usernameField);
		
		this.emailFieldLabel = new Label(WicketIdConstants.USER_DETAILS_FORM_EMAIL_FIELD_LABEL, "Email *");
		add(this.emailFieldLabel);
		this.emailField = new RequiredTextField(WicketIdConstants.USER_DETAILS_FORM_EMAIL_FIELD, 
												new PropertyModel(this.user, "email"));
		this.emailField.add(new EmailAddressPatternValidator());
		this.emailField.setLabel(new Model("Email"));
		add(this.emailField);
		
		this.passwordFieldLabel = new Label(WicketIdConstants.USER_DETAILS_FORM_PASSWORD_LABEL, "Password *"); 
		add(this.passwordFieldLabel);
		this.passwordField = 
			new PasswordTextField(WicketIdConstants.USER_DETAILS_FORM_PASSWORD_FIELD, 
						  new PropertyModel(this.user, "password"));
		this.passwordField.setRequired(true);
		//TODO: We are setting the label to provide a better error msg when the pwd and retype pwd fields do not have the same value
		//Udeally this should be controlled through a message in a property file
		this.passwordField.setLabel(new Model("Password"));
		this.passwordField.add(StringValidator.lengthBetween(6, 16));
		add(this.passwordField);
		
		this.repeatPasswordFieldLabel = new Label(WicketIdConstants.USER_DETAILS_FORM_REPEAT_PASSWORD_LABEL, "Retype Password *"); 
		add(this.repeatPasswordFieldLabel);
		this.repeatPasswordField = 
			new PasswordTextField(WicketIdConstants.USER_DETAILS_FORM_REPEAT_PASSWORD_FIELD, 
						  new PropertyModel(this, "repeatPassword"));
		this.repeatPasswordField.setRequired(true);
		this.repeatPasswordField.add(StringValidator.lengthBetween(6, 16));
		//TODO: We are setting the label to provide a better error msg when the pwd and retype pwd fields do not have the same value
		//Udeally this should be controlled through a message in a property file
		this.repeatPasswordField.setLabel(new Model("Retype Password"));
		add(this.repeatPasswordField);
		
		this.roleFieldLabel = new Label(WicketIdConstants.USER_DETAILS_FORM_ROLE_LABEL, "Role *"); 
		add(this.roleFieldLabel);
		this.roleField = new DropDownChoice(WicketIdConstants.USER_DETAILS_FORM_ROLE_FIELD, new PropertyModel(this.user, "role"), getRoles());
		this.roleField.setRequired(true);
		this.roleField.setLabel(new Model("Role"));
		add(this.roleField);
	
		add(new EqualPasswordInputValidator(this.passwordField, this.repeatPasswordField));
	}

	private IDataFacade getDataFacade() {
		return ((WWALDApplication)getApplication()).getDataFacade();
	}

	private List getRoles() {
		List<Role> roles = new ArrayList<Role>();
		roles.add(Role.STUDENT);
		return roles;
	}
	
	private final String getDatabaseId() {
		ServletWebRequest request = (ServletWebRequest)getRequest();
		String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
		return ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
	}
}
