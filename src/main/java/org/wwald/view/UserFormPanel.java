package org.wwald.view;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.model.Role;
import org.wwald.model.User;

public class UserFormPanel extends Panel{
	private UserForm userForm;
	
	public UserFormPanel(String id) {
		this(id, null);		
	}
	
	public UserFormPanel(String id, User user, UserForm.Field... userFields) {
		super(id);
		init(user, userFields);
	}

	public void setRoleChoices(Role... roles) {
		this.userForm.setRoleChoices(roles);
	}
	
	public void setSubmitResponsePage(Class<? extends BasePage> responsePage) {
		this.userForm.setSubmitResponsePage(responsePage);
	}
	
	public void setFieldEditable(UserForm.Field field, boolean editable) {
		this.userForm.setFieldEditable(field, editable);
	}
	
	private void init(User user, UserForm.Field... userFields) {
		if(user != null) {
			this.userForm = new UserForm("user_form", user, userFields);
		}
		else {
			this.userForm = new UserForm("user_form");
		}
		
		add(this.userForm);
		add(new FeedbackPanel("messages"));
	}
}
