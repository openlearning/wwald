package org.wwald.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.wwald.WWALDApplication;

public class PropertyDirMap extends HashMap {
	
	public PropertyDirMap() throws IOException {
		InputStream dirmapIs = new FileInputStream(WWALDApplication.WWALDDIR + "dirmap.properties");
		Properties dirmapProps = new Properties();
		dirmapProps.load(dirmapIs);
		Set<?> dirmapKeys = dirmapProps.keySet();
		for(Object dirmapKey : dirmapKeys) {
			String val = dirmapProps.getProperty((String)dirmapKey);
			put((String)dirmapKey, val);		
		}
	}
}
