package org.wwald.view;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.wwald.WicketIdConstants;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.view.UserForm.Field;

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
	
	public void setFieldVisible(Field field, boolean visible) {
		this.userForm.setFieldVisible(field, visible);
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
		add(new FeedbackPanel(WicketIdConstants.MESSAGES));
	}
}
