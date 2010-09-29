package org.wwald;

import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.WebApplication;
import org.wwald.model.DataFacade;
import org.wwald.view.HomePage1;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see org.wwald.Start#main(String[])
 */
public class WWALDApplication extends WebApplication
{
	private static Logger cLogger = Logger.getLogger(WWALDApplication.class);
	
	private DataFacade dataStore;
    /**
     * Constructor
     */
	public WWALDApplication()
	{
		this.dataStore = new DataFacade();
	}
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<HomePage1> getHomePage()
	{
		return HomePage1.class;
	}
	
	public DataFacade getDataStore() {
		return this.dataStore;
	}
	
}
