package org.wwald.view;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.wwald.WWALDApplication;
import org.wwald.WWALDConstants;
import org.wwald.WWALDPropertiesEnum;
import org.wwald.WicketIdConstants;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Role;


public abstract class BasePage extends WebPage {
	private static Logger cLogger = Logger.getLogger(BasePage.class);
	
	Panel sidebar;
	
	public BasePage(PageParameters parameters) {		
		//we add an empty side bar which will be replaced by inheriting pages
		this.sidebar = new EmptyPanel(WicketIdConstants.RHS_SIDEBAR); 
		add(this.sidebar);
		add(new HeaderPanel(WicketIdConstants.HEADER_PANEL));
		add(new FooterPanel(WicketIdConstants.FOOTER_PANEL));
		add(new Label(WicketIdConstants.MESSAGES, new Model(getMessages(parameters))));
	}

	public void replaceSidebar(Panel sidebar) {
		Panel temp = this.sidebar;
		temp.replaceWith(sidebar);
		this.sidebar = sidebar;
	}
	
	@Override
	public void onBeforeRender() {
		super.onBeforeRender();
		//we will now replace the default sidebar with the
		//page specific sidebar
		replaceSidebar(getSidebar(this));
	}
	
	public final Panel getSidebar(BasePage viewPage) {
		Panel sidebar = null;		
		
		Class<?> viewPageClazz = viewPage.getClass();
		String sidebarFqcn = getSidebarFqcn(viewPageClazz);
		
		if(sidebarFqcn == null || sidebarFqcn.equals("")) {
			sidebarFqcn = getSidebarFqcn(BasePage.class);
		}
		
		if(sidebarFqcn != null && !sidebarFqcn.equals("")) {
			try {
				sidebar = dynamicallyConstructSidebar(sidebarFqcn, viewPage);
			} catch(Exception e) {
				String msg = "Could not construct sidebar due to an Exception. " +
							 "Will use the default sidebar " + e;
				cLogger.warn(msg);
			} 
		}
		
		if(sidebar == null) {
			sidebar = new Sidebar(WicketIdConstants.RHS_SIDEBAR, viewPage);
		}
		
		return sidebar; 
	}
	
	protected final String getRequestUrl() {
		ServletWebRequest request = (ServletWebRequest)getRequest();
		return request.getHttpServletRequest().getRequestURL().toString();
	}
	
	protected final String getDatabaseId() {
		String requestUrl = getRequestUrl();
		return ConnectionPool.getDatabaseIdFromRequestUrl(requestUrl);
	}

	private String getSidebarFqcn(Class<?> pageClass) {
		String clazzName = pageClass.getName();
		String sidebarKey = clazzName + "." + WWALDConstants.SIDEBAR_SUFFIX;
		String sidebarFqcn = WWALDPropertiesEnum.UI_CONFIG_PROERTIES.
										getProperties().getProperty(sidebarKey);
		return sidebarFqcn;
	}
	
	private Panel dynamicallyConstructSidebar(String sidebarFqcn, BasePage viewPage) 
									throws SecurityException, 
										   NoSuchMethodException, 
										   ClassNotFoundException, 
										   IllegalArgumentException, 
										   InstantiationException, 
										   IllegalAccessException, 
										   InvocationTargetException {		
		
		Panel sidebar = null;
		Class sidebarClass = Class.forName(sidebarFqcn);
		Constructor cons = sidebarClass.getConstructor(String.class, BasePage.class);
		Object sidebarObj = cons.newInstance(WicketIdConstants.RHS_SIDEBAR, viewPage);
		
		if(sidebarObj instanceof Panel ) {
			sidebar = (Panel)sidebarObj;
			return sidebar;
		}
		else {
			String msg = "The class specified for sidebar '" + sidebarFqcn + 
						 "' should be a subclass of org.apache.wicket.markup.html.panel";
			cLogger.warn(msg);
		}
		return sidebar;
	}
	
	private Serializable getMessages(PageParameters parameters) {
		String messages = parameters.getString(WicketIdConstants.MESSAGES);
		return messages ==  null ? "" : messages; 
	}
}
