package org.wwald.view;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.wwald.WWALDApplication;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.UserMeta;
import org.wwald.service.ApplicationException;
import org.wwald.util.LoginLogger;

public class LoginPage extends BasePage {

	private static final Logger cLogger = Logger.getLogger(LoginPage.class);
	
	public LoginPage(PageParameters parameters) {
		super(parameters);
		add(getSocialLoginPanel());
		add(getLoginForm(parameters));
	}
	
	private Panel getSocialLoginPanel() {
		return new SocialLoginPanel(WicketIdConstants.SOCIAL_LOGIN_PANEL);
	}

	public Form getLoginForm(final PageParameters parameters) {
		Form loginForm = new Form("login") {
			@Override
			public void onSubmit() {
				try {
					String username = (String)((TextField)get("username")).getModelObject();
					String password = (String)((TextField)get("password")).getModelObject();
					
					UserMeta userMeta = 
						((WWALDApplication)getApplication()).
							getApplicationFacade().
								login(username, 
									  password, 
									  ConnectionPool.getConnection(getDatabaseId()),
									  new BasicPasswordEncryptor());
					
					
					WWALDSession.get().setUserMeta(userMeta);
					if(userMeta != null) {
						LoginLogger.successfullLogin(userMeta.getIdentifier(), UserMeta.LoginVia.INTERNAL, WWALDSession.get().getId());
						setResponsePage(HomePage.class);
					}
					else {
						LoginLogger.unsuccessfullLoginInternal(username);
						parameters.add(WicketIdConstants.MESSAGES, 
									   "Incorrect username or password, please try again.");
						setResponsePage(LoginPage.class, parameters);
					}
				} catch(ApplicationException ae) {
					cLogger.error("Could not login user " + ae);
					String msg = "Sorry we could not log you into the application due to an internal error. We will look into this problem as soon as we can";
					parameters.add(WicketIdConstants.MESSAGES, msg);
					setResponsePage(GenericErrorPage.class, parameters);
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
	
}
