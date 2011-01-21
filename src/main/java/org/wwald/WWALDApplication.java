package org.wwald;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.apache.wicket.settings.IExceptionSettings;
import org.wwald.service.ApplicationFacade;
import org.wwald.service.DataFacadeRDBMSImpl;
import org.wwald.service.IDataFacade;
import org.wwald.util.AbstractMarkdownProcessor;
import org.wwald.util.PropertyDirMap;
import org.wwald.view.CallbackHandlerPage;
import org.wwald.view.CoursePage;
import org.wwald.view.ErrorPage404;
import org.wwald.view.ErrorPageInternal;
import org.wwald.view.ForumsPage;
import org.wwald.view.HomePage;
import org.wwald.view.LoginPage;
import org.wwald.view.StaticPage;
import org.wwald.view.UserProfiles;
import org.wwald.view.components.CourseThumbnailImageResource;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see org.wwald.Start#main(String[])
 */
public class WWALDApplication extends WebApplication
{
	public static final String COURSE_THUMBNAIL_IMAGE = "course_image";
	public static final String USER_THUMBNAIL_IMAGE = "user_image";
	public static final String HOMEDIR; 
	public static final String WWALDDIR;
	public static PropertyDirMap DIRMAP;
	private static Logger cLogger = Logger.getLogger(WWALDApplication.class);
	
	private IDataFacade dataStore;
	private ApplicationFacade applicationFacade;
	
	static {
		HOMEDIR = System.getProperty("user.home");
		WWALDDIR = HOMEDIR + "/.wwald/";
		if(HOMEDIR == null || HOMEDIR.equals("")) {
			throw new RuntimeException("CANNOT START APPLICATION BECAUSE THE user.home SYSTEM PROPERTY DOES NOT EXIST !!!");
		}
		try {
			DIRMAP = new PropertyDirMap();
		} catch(IOException ioe) {
			cLogger.error("Could not find dirmap.properties");
		}
	}
	
    /**
     * Constructor
     */
	public WWALDApplication()
	{
		this.dataStore = new DataFacadeRDBMSImpl();
		this.applicationFacade = new ApplicationFacade(this.dataStore);		 
	}
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<HomePage> getHomePage()
	{
		return HomePage.class;
	}
	
	@Override
	public Session newSession(Request request, Response response) {
		return new WWALDSession(request);
	}
	
	@Override
	public void init() {
		mountBookmarkablePage("courses", CoursePage.class);
		mountBookmarkablePage("login", LoginPage.class);
//		mountBookmarkablePage("register", Register.class);
		mountBookmarkablePage("users", UserProfiles.class);
		mountBookmarkablePage("static", StaticPage.class);
		mountBookmarkablePage("callback", CallbackHandlerPage.class);
		mountBookmarkablePage("forums", ForumsPage.class);
		mount(new QueryStringUrlCodingStrategy("error404", ErrorPage404.class));
		getApplicationSettings().setInternalErrorPage(ErrorPageInternal.class);
		getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
		
		getSharedResources().add(COURSE_THUMBNAIL_IMAGE, 
								 new CourseThumbnailImageResource());
	}
	
	public IDataFacade getDataFacade() {
		return this.dataStore;
	}
	
	public ApplicationFacade getApplicationFacade() {
		return this.applicationFacade;
	}
	
	public synchronized AbstractMarkdownProcessor getMarkDown() {
		return AbstractMarkdownProcessor.JMD_MARKDOWN_PROCESSOR;
	}
	
	public static WWALDApplication get() {
		return (WWALDApplication)Application.get();
	}

}
