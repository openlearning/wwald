package org.wwald;

import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.wwald.model.DataFacadeRDBMSImpl;
import org.wwald.view.CoursePage;
import org.wwald.view.ErrorPage404;
import org.wwald.view.HomePage;
import org.wwald.view.Login;
import org.wwald.view.Register;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see org.wwald.Start#main(String[])
 */
public class WWALDApplication extends WebApplication
{
	private static Logger cLogger = Logger.getLogger(WWALDApplication.class);
	
	private DataFacadeRDBMSImpl dataStore;
    /**
     * Constructor
     */
	public WWALDApplication()
	{
		this.dataStore = new DataFacadeRDBMSImpl();
	}
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<HomePage> getHomePage()
	{
		return HomePage.class;
	}
	
	@Override
	public void init() {
//		mount(new IndexedParamUrlCodingStrategy("courses", CoursePage.class));
		mountBookmarkablePage("courses", CoursePage.class);
		mountBookmarkablePage("login", Login.class);
		mountBookmarkablePage("register", Register.class);
		mount(new QueryStringUrlCodingStrategy("error404", ErrorPage404.class));
	}
	
	public DataFacadeRDBMSImpl getDataFacade() {
		return this.dataStore;
	}

}
