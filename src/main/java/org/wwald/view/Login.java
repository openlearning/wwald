package org.wwald.view;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.User;

public class Login extends BasePage {

	public Login(PageParameters parameters) {
		super(parameters);
		add(getLoginForm(parameters));
	}
	
	public Form getLoginForm(final PageParameters parameters) {
		Form loginForm = new Form("login") {
			@Override
			public void onSubmit() {
				String username = (String)((TextField)get("username")).getModelObject();
				String password = (String)((TextField)get("password")).getModelObject();
				User user = 
					((WWALDApplication)getApplication()).
						getApplicationFacade().login(username, password);
				WWALDSession.get().setUser(user);
				if(user != null) {
					setResponsePage(HomePage.class);
				}
				else {
					parameters.add(WicketIdConstants.MESSAGES, 
								   "Incorrect username or password, please try again.");
					setResponsePage(Login.class, parameters);
				}
			}
		};
		Label usernameLabel = new Label("username_label", "username");
		TextField username = new TextField("username", new Model());
		Label passwordLabel = new Label("password_label", "password");
		TextField password = new PasswordTextField("password", new Model());
		loginForm.add(usernameLabel, username, passwordLabel, password);
		return loginForm;
	}
	
	@Override
	public Panel getSidebar() {
		return new EmptyPanel("rhs_sidebar");
	}

}
