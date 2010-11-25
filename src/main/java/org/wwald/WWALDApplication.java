package org.wwald;

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
import org.wwald.view.ErrorPageInternal;
import org.wwald.view.StaticPage;
import org.wwald.view.StaticPagePojo;
import org.wwald.view.CoursePage;
import org.wwald.view.ErrorPage404;
import org.wwald.view.HomePage;
import org.wwald.view.LoginPage;
import org.wwald.view.Register;

import com.cforcoding.jmd.MarkDown;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see org.wwald.Start#main(String[])
 */
public class WWALDApplication extends WebApplication
{
	public static final String HOMEDIR; 
	public static final String WWALDDIR;
	
	private static Logger cLogger = Logger.getLogger(WWALDApplication.class);
	
	private IDataFacade dataStore;
	private ApplicationFacade applicationFacade;
	private MarkDown markDownLib;
	
	static {
		HOMEDIR = System.getProperty("user.home");
		WWALDDIR = HOMEDIR + "/.wwald/";
		if(HOMEDIR == null || HOMEDIR.equals("")) {
			throw new RuntimeException("CANNOT START APPLICATION BECAUSE THE user.home SYSTEM PROPERTY DOES NOT EXIST !!!");
		}
	}
	
    /**
     * Constructor
     */
	public WWALDApplication()
	{
		this.dataStore = new DataFacadeRDBMSImpl();
		this.applicationFacade = new ApplicationFacade(this.dataStore);
		this.markDownLib = new MarkDown();		 
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
		mountBookmarkablePage("register", Register.class);
		mountBookmarkablePage("static", StaticPage.class);
		mount(new QueryStringUrlCodingStrategy("error404", ErrorPage404.class));
		getApplicationSettings().setInternalErrorPage(ErrorPageInternal.class);
		getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE); 
	}
	
	public IDataFacade getDataFacade() {
		return this.dataStore;
	}
	
	public ApplicationFacade getApplicationFacade() {
		return this.applicationFacade;
	}
	
	public synchronized MarkDown getMarkDown() {
		return this.markDownLib;
	}
	
	public static WWALDApplication get() {
		return (WWALDApplication)Application.get();
	}

}
