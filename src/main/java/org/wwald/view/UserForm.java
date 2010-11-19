package org.wwald.view;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.service.IDataFacade;

public class UserForm extends Form {

	private User user;
	private String repeatPassword;
	
	private TextField firstNameTextField;
	private TextField miTextField;
	private TextField lastNameTextField;
	private TextField usernameTextField;
	private TextField passwordTextField;
	private TextField repeatPasswordTextField;
	private DropDownChoice roleTextField;
	
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
		this.user = user;
		addTextFields();
	}
	
	public void setFieldEditable(Field field, boolean editable) {
		switch(field) {
			case FIRST_NAME:
				this.firstNameTextField.setEnabled(false);
				break;
			case MIDDLE_INITIAL:
				this.miTextField.setEnabled(false);
				break;
			case LAST_NAME:
				this.lastNameTextField.setEnabled(false);
				break;
			case USERNAME:
				this.usernameTextField.setEnabled(false);
				break;
			case PASSWORD:
				this.passwordTextField.setEnabled(false);
				break;				
			case REPEAT_PASSWORD:
				this.repeatPasswordTextField.setEnabled(false);
				break;
			case ROLE:
				this.roleTextField.setEnabled(false);
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
			dataFacade.updateUser(conn, this.user);
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
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_FIRST_NAME_LABEL, "First Name"));
		firstNameTextField = 
			new TextField(WicketIdConstants.USER_DETAILS_FORM_FIRST_NAME_FIELD, 
						  new PropertyModel(this.user, "firstName"));
		add(firstNameTextField);
		
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_MI_LABEL, "Middle Initial"));
		miTextField = 
			new TextField(WicketIdConstants.USER_DETAILS_FORM_MI_FIELD, 
						  new PropertyModel(this.user, "mi"));
		add(miTextField);
		
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_LAST_NAME_LABEL, "Last Name"));
		lastNameTextField = 
			new TextField(WicketIdConstants.USER_DETAILS_FORM_LAST_NAME_FIELD, 
						  new PropertyModel(this.user, "lastName"));
		add(lastNameTextField);
	
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_USERNAME_LABEL, "Username"));
		usernameTextField = 
			new TextField(WicketIdConstants.USER_DETAILS_FORM_USERNAME_FIELD, 
						  new PropertyModel(this.user, "username"));
		add(usernameTextField);
		
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_PASSWORD_LABEL, "Password"));
		passwordTextField = 
			new TextField(WicketIdConstants.USER_DETAILS_FORM_PASSWORD_FIELD, 
						  new PropertyModel(this.user, "password"));
		add(passwordTextField);
		
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_REPEAT_PASSWORD_LABEL, "Retype Password"));
		repeatPasswordTextField = 
			new TextField(WicketIdConstants.USER_DETAILS_FORM_REPEAT_PASSWORD_FIELD, 
						  new PropertyModel(this.user, "password"));
		add(repeatPasswordTextField);
		
		add(new Label(WicketIdConstants.USER_DETAILS_FORM_ROLE_LABEL, "Role"));
		roleTextField = new DropDownChoice(WicketIdConstants.USER_DETAILS_FORM_ROLE_FIELD, new PropertyModel(this.user, "role"), getRoles());
		add(roleTextField);
	}

	private List getRoles() {
		List<Role> roles = Arrays.asList(Role.values());
		return roles;
	}
	
	private final String getDatabaseId() {
		ServletWebRequest request = (ServletWebRequest)getRequest();
		String requestUrl = request.getHttpServletRequest().getRequestURL().toString();
		return ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
	}
}
