package org.wwald.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class CourseWikiParser {
	
	private static final Logger cLogger = Logger.getLogger(CourseWikiParser.class);
	
	public static class CourseTitlePair {
		
		public final String courseId;
		public final String courseTitle;
		public final String updatedCourseTitle;
		
		public CourseTitlePair(String courseId, 
							   String courseTitle) {
			this(courseId, courseTitle, null);
		}
		
		public CourseTitlePair(String courseId, 
							   String courseTitle, 
							   String updatedCourseTitle) {
			this.courseId = courseId;
			this.courseTitle = courseTitle;
			this.updatedCourseTitle = updatedCourseTitle;
		}
	}
	
	public static class UpdateHint {
		public final List<CourseTitlePair> updatedCourseTitlePairs;
		public final String updatedWikiContents;
		
		public UpdateHint(List<CourseTitlePair> courseTitlePairs,
						  String updatedWikiContents) {
			this.updatedCourseTitlePairs = courseTitlePairs;
			this.updatedWikiContents = updatedWikiContents;
		}
	}
	
	public List<CourseTitlePair> parse(String wikiContent) throws ParseException {
		List<CourseTitlePair> tokens = new ArrayList<CourseTitlePair>();
		BufferedReader bufferedReader = new BufferedReader(new StringReader(wikiContent));
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				if(line.trim().equals("")) {
					//ignore empty lines
					continue;
				}
				if(line.trim().startsWith("#")) {
					//# denotes a comment
					continue;
				}
				String lineTokens[] = line.split("\\|");
				String courseId = lineTokens[0];
				String courseTitle = lineTokens[1];
				// It does not matter if the courseTitle is null, but as a rule we
				// want the wikiContent
				// to contain both for each course
				if (courseId != null && courseTitle != null) {
					CourseTitlePair ctp = null;
					if(courseTitle.contains("->")) {
						String titleTokens[] = courseTitle.split("->");
						
						if(titleTokens != null && titleTokens.length == 2 && titleTokens[0] != null && titleTokens[1] != null) {
							String origTitle = titleTokens[0].trim();
							String updatedTitle = titleTokens[1].trim();
							ctp = new CourseTitlePair(courseId.trim(), origTitle, updatedTitle);
						}
					}
					else {
						ctp = new CourseTitlePair(courseId.trim(), 
												  courseTitle.trim());
					}
					tokens.add(ctp);
				}
				else {
					String msg = "The following line is not in the expected format\n" +
								 "Line: '" + line + "'\n" +
								 "Expected format: 'courseId | course title'";
					throw new ParseException(msg);
				}
			}
		} catch(IOException ioe) {
			String msg = "Could not parse the wiki contents due to an internal error";
			throw new ParseException(msg);
		}
		return tokens;
	}
	
	public UpdateHint parseForUpdate(String wikiContent) throws ParseException {
		StringBuffer updatedWikiContentBuff = new StringBuffer();
		List<CourseTitlePair> tokens = new ArrayList<CourseTitlePair>();
		
		BufferedReader bufferedReader = new BufferedReader(new StringReader(wikiContent));
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				if(line.trim().equals("")) {
					//ignore empty lines
					updatedWikiContentBuff.append(line + "\n");
					continue;
				}
				if(line.trim().startsWith("#")) {
					//# denotes a comment
					updatedWikiContentBuff.append(line + "\n");
					continue;
				}
				String lineTokens[] = line.split("\\|");
				String courseId = lineTokens[0];
				String courseTitle = lineTokens[1];
				// It does not matter if the courseTitle is null, but as a rule we
				// want the wikiContent
				// to contain both for each course
				if (courseId != null && courseTitle != null) {					
					CourseTitlePair ctp = null;
					if(courseTitle.contains("->")) {
						String titleTokens[] = courseTitle.split("->");
						
						if(titleTokens != null && titleTokens.length == 2 && titleTokens[0] != null && titleTokens[1] != null) {
							String origTitle = titleTokens[0].trim();
							String updatedTitle = titleTokens[1].trim();
							ctp = new CourseTitlePair(courseId.trim(), origTitle, updatedTitle);
						}
						else {
							String msg = "Wiki line has an incorrect syntax '" + line + "'";
							cLogger.error(msg);
							throw new ParseException(msg);
						}
					}
					else {
						ctp = new CourseTitlePair(courseId.trim(), 
												  courseTitle.trim());
					}
					//only add those tokens which signify an update
					if(ctp.updatedCourseTitle != null) {
						tokens.add(ctp);
					}					
					String newWikiLine = ctp.courseId + " | " + 
					  					 (ctp.updatedCourseTitle != null ? ctp.updatedCourseTitle : ctp.courseTitle);
							    
					updatedWikiContentBuff.append(newWikiLine+"\n");
				}
				else {
					String msg = "The following line is not in the expected format\n" +
								 "Line: '" + line + "'\n" +
								 "Expected format: 'courseId | course title'";
					throw new ParseException(msg);
				}
			}
		} catch(IOException ioe) {
			String msg = "Could not parse the wiki contents due to an internal error";
			throw new ParseException(msg);
		}
		UpdateHint updateHint = new UpdateHint(tokens, 
											   updatedWikiContentBuff.toString());
		return updateHint;
	}
}
