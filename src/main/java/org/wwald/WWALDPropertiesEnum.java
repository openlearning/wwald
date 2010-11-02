package org.wwald;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public enum WWALDPropertiesEnum {
	UI_CONFIG_PROERTIES("ui_config.properties");
	
	private Logger cLogger = Logger.getLogger(WWALDPropertiesEnum.class);
	
	private Properties properties;
	
	private WWALDPropertiesEnum(String name) {
		properties = new Properties();
		
		try {
			InputStream is = 
				WWALDPropertiesEnum.class.getClassLoader().
					getResourceAsStream(name);
			
			properties.load(is);
		} catch(Exception e) {
			String msg = "Could not load properties file. " +
						 "This application may not function properly. " +
						 "'" + name + "'";
			cLogger.error(msg);
		}
	}
	
	public Properties getProperties() {
		return properties;
	}
}
