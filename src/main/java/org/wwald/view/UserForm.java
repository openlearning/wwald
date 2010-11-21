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
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.service.DataException;
import org.wwald.service.IDataFacade;

public class UserForm extends Form {

	private User origUser;
	private User user;
	private String repeatPassword;
	
	private TextField firstNameField;
	private TextField miField;
	private TextField lastNameField;
	private TextField usernameField;
	private TextField passwordField;
	private TextField repeatPasswordField;
	private DropDownChoice roleField;
	
	private Class<? extends Page> responsePage;
	
	//TODO: What is the implication of making this field transient?
	private transient Logger cLogger = Logger.getLogger(UserForm.class);
	
	public enum Field {
		FIRST_NAME,
		MIDDLE_INITIAL,
		LAST_NAME,
		USERNAME,
		PASSWORD,
		REPEAT_PASSWORD,
		ROLE;
	}
	
	public UserForm(String id) {
		this(id, new User());
	}
	
	public UserForm(String id, User user) {
		super(id);
		this.origUser = user;
		copyUser();
		addTextFields();
	}
	
	private void copyUser() {
		this.user = new User(this.origUser.getFirstName(),
							 this.origUser.getMi(),
							 this.origUser.getLastName(),
							 this.origUser.getUsername(),
							 this.origUser.getJoinDate(),
							 this.origUser.getRole());
		
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
	
	public void setFieldEditable(Field field, boolean editable) {
		switch(field) {
			case FIRST_NAME:
				this.firstNameField.setEnabled(false);
				break;
			case MIDDLE_INITIAL:
				this.miField.setEnabled(false);
				break;
			case LAST_NAME:
				this.lastNameField.setEnabled(false);
				break;
			case USERNAME:
				this.usernameField.setEnabled(false);
				break;
			case PASSWORD:
				this.passwordField.setEnabled(false);
				break;				
			case REPEAT_PASSWORD:
				this.repeatPasswordField.setEnabled(false);
				break;
			case ROLE:
				this.roleField.setEnabled(false);
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
				//this is a new user
				this.user.setJoinDate(new Date());
				dataFacade.insertUser(conn, this.user);
			}
			else {
				dataFacade.updateUser(conn, this.user);
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
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_FIRST_NAME_LABEL, "First Name *"));
		this.firstNameField =
			new RequiredTextField(WicketIdConstants.USER_DETAILS_FORM_FIRST_NAME_FIELD, 
						  new PropertyModel(this.user, "firstName"));
		this.firstNameField.add(StringValidator.lengthBetween(2, 32));
		//TODO: Can we use a model that reads directly from a property file
		this.firstNameField.setLabel(new Model("First Name"));
		add(this.firstNameField);
		
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_MI_LABEL, "Middle Initial"));
		this.miField = 
			new TextField(WicketIdConstants.USER_DETAILS_FORM_MI_FIELD, 
						  new PropertyModel(this.user, "mi"));
		add(this.miField);
		
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_LAST_NAME_LABEL, "Last Name *"));
		this.lastNameField = 
			new RequiredTextField(WicketIdConstants.USER_DETAILS_FORM_LAST_NAME_FIELD, 
						  new PropertyModel(this.user, "lastName"));
		this.lastNameField.add(StringValidator.lengthBetween(1, 32));
		this.lastNameField.setLabel(new Model("Last Name"));
		add(this.lastNameField);
	
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_USERNAME_LABEL, "Username *"));
		this.usernameField = 
			new RequiredTextField(WicketIdConstants.USER_DETAILS_FORM_USERNAME_FIELD, 
						  new PropertyModel(this.user, "username"));
		this.usernameField.add(StringValidator.lengthBetween(6, 16));
		this.usernameField.add(new DuplicateUsernameValidator(getDatabaseId(), getDataFacade()));
		this.usernameField.setLabel(new Model("Username"));
		add(this.usernameField);
		
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_PASSWORD_LABEL, "Password *"));
		this.passwordField = 
			new PasswordTextField(WicketIdConstants.USER_DETAILS_FORM_PASSWORD_FIELD, 
						  new PropertyModel(this.user, "password"));
		this.passwordField.setRequired(true);
		this.passwordField.setLabel(new Model("Password"));
		this.passwordField.add(StringValidator.lengthBetween(6, 16));
		add(this.passwordField);
		
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_REPEAT_PASSWORD_LABEL, "Retype Password *"));
		this.repeatPasswordField = 
			new PasswordTextField(WicketIdConstants.USER_DETAILS_FORM_REPEAT_PASSWORD_FIELD, 
						  new PropertyModel(this, "repeatPassword"));
		this.repeatPasswordField.setRequired(true);
		this.repeatPasswordField.add(StringValidator.lengthBetween(6, 16));
		this.repeatPasswordField.setLabel(new Model("Retype Password"));
		add(this.repeatPasswordField);
		
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_ROLE_LABEL, "Role *"));
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
