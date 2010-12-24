package org.wwald.view.components;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;
import org.wwald.WWALDApplication;

public class CourseThumbnailImageResource extends WebResource {
	
	private static Logger cLogger = 
		Logger.getLogger(CourseThumbnailImageResource.class);
	private static String DEFAULT_IMAGE_NAME = "default_course_image.png";
	
	@Override
	public IResourceStream getResourceStream() {
		return new IResourceStream() {

			private InputStream is;
			private Locale locale;
			
			public void close() throws IOException {
				if(is != null) {
					this.is.close();
					is = null;
				}				
			}

			public String getContentType() {
				// TODO Auto-generated method stub "image/jpeg" etc
				return "image/png";
			}

			public InputStream getInputStream()
					throws ResourceStreamNotFoundException {				
				
				String courseId = getParameters().getString("courseId");
				if(courseId == null) {
					cLogger.warn("courseId has not been specified, " +
								 "the default course image will be used");
				}
				
				String dbId = getParameters().getString("dbId");
				if(dbId == null) {
					cLogger.warn("dbId has not been specified, " +
								 "the default course image will be used");
				}
				
				String filePath = WWALDApplication.WWALDDIR + 
				  				  WWALDApplication.DIRMAP.get(dbId) + 
				  				  "/courseimg/" + courseId + ".png";
				
				try {
					is = new FileInputStream(filePath); 
				} catch(IOException ioe) {
					String msg = "Could not find thumbnail image for course " + 
								 courseId + " using default course thumbnail " +
								 "image";
					cLogger.warn(msg, ioe);
					
					String defaultImageFilePath = WWALDApplication.WWALDDIR +  
	  				  							  "/defaults/" + DEFAULT_IMAGE_NAME;
					try {
						is = new FileInputStream(defaultImageFilePath);
					} catch(IOException ioe1) {
						String msg1 = "Could not load default thumbnail image " +
									  "for courses " + DEFAULT_IMAGE_NAME;
						cLogger.error(msg, ioe1);
						throw new ResourceStreamNotFoundException(msg, ioe1);
					}
				}
				return is;
			}

			public Locale getLocale() {				
				return this.locale;
			}

			public long length() {
				// TODO Auto-generated method stub
				return -1;
			}

			public void setLocale(Locale locale) {
				this.locale = locale;
			}

			public Time lastModifiedTime() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}

}
