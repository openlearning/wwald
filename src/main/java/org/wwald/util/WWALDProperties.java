package org.wwald.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.wwald.WWALDApplication;

public class WWALDProperties extends Properties {
	
	private PropertyDirMap dirmap;
	
	public static final String DATABASE_PROPERTIES = "db.properties";
	
	public WWALDProperties(String dbId, String propFileName) throws IOException {
		super();
		this.dirmap = new PropertyDirMap();	
		String filePath = WWALDApplication.WWALDDIR + dirmap.get(dbId) + "/" + propFileName;
		InputStream is = new FileInputStream(filePath);
		load(is);
	}
	
}
