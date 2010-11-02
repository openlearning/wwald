package org.wwald.view;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class Register extends BasePage {
	public Register(PageParameters parameters) {
		super(parameters);
		add(getRegisterForm());
	}

	private Component getRegisterForm() {
		Form registerForm = new Form("register_form") {
			
		};
		registerForm.add(new Label("first_name_label", "First Name"));
		registerForm.add(new TextField("first_name_text"));
		registerForm.add(new Label("mi_label", "Middle Initial"));
		registerForm.add(new Label("mi_text"));
		registerForm.add(new Label("last_name_label", "Last Name"));
		registerForm.add(new TextField("last_name_text"));
		registerForm.add(new Label("screen_name_label", "Screen Name"));
		registerForm.add(new TextField("screen_name_text"));
		registerForm.add(new Label("username_label", "Username"));
		registerForm.add(new TextField("username_text"));
		registerForm.add(new Label("password1_label", "Password"));
		registerForm.add(new TextField("password1_text"));
		registerForm.add(new Label("password2_label", "Repeat Password"));
		registerForm.add(new TextField("password2_text"));
		registerForm.add(new Label("profile_label", "Profile"));
		registerForm.add(new TextArea("profile_text"));
		return registerForm;
	}

}
