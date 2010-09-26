package org.wwald;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see org.wwald.Start#main(String[])
 */
public class WWALDApplication extends WebApplication
{
	private DataStore dataStore;
    /**
     * Constructor
     */
	public WWALDApplication()
	{
		this.dataStore = new DataStore();
	}
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<HomePage1> getHomePage()
	{
		return HomePage1.class;
	}
	
	public DataStore getDataStore() {
		return this.dataStore;
	}
	
}
