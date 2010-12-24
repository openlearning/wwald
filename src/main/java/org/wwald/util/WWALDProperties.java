package org.wwald.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.wwald.WWALDApplication;

public class WWALDProperties extends Properties {
	
	public static final String DATABASE_PROPERTIES = "db.properties";
	public static final String UI_PROPERTIES = "ui_config.properties";
	
	public WWALDProperties(String dbId, String propFileName) throws IOException {
		super();	
		String filePath = WWALDApplication.WWALDDIR + 
						  WWALDApplication.DIRMAP.get(dbId) + 
						  "/" + propFileName;
		InputStream is = new FileInputStream(filePath);
		load(is);
	}
	
}
