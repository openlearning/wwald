package org.wwald.view;

import org.apache.wicket.PageParameters;
import org.wwald.WWALDSession;
import org.wwald.WicketIdConstants;
import org.wwald.model.Permission;
import org.wwald.model.User;

public abstract class AccessControlledPage extends BasePage {

	public AccessControlledPage(PageParameters parameters) {
		super(parameters);
		if(!hasPermission(WWALDSession.get().getUser())) {
			parameters.add(WicketIdConstants.MESSAGES, 
						   "you do not have authorization to access the requested page");
			setResponsePage(HomePage.class, parameters);
		}
	}

	private boolean hasPermission(User user) {
		boolean retVal = false;
		Permission requiredPermission = getRequiredPermission();
		if(requiredPermission == null) {
			throw new RuntimeException("requiredPermission should never be null - " + 
									   this.getClass().getName());
		}
		if(user != null) {
			Permission userPermissions[] = user.getRole().getPermissions();
			
			if(userPermissions != null) {
				for(Permission permission : userPermissions) {
					if(permission.equals(requiredPermission)) {
						retVal = true;
						break;
					}
				}
			}
		}
		return retVal;
	}

	protected abstract Permission getRequiredPermission();

}
