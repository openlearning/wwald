package org.wwald.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CourseWikiParser {
	
	public static class CourseTitlePair {
		
		public final String courseId;
		public final String courseTitle;
		
		public CourseTitlePair(String courseId, String courseTitle) {
			this.courseId = courseId;
			this.courseTitle = courseTitle;
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
				String lineTokens[] = line.split("\\|");
				String courseId = lineTokens[0];
				String courseTitle = lineTokens[1];
				// It does not matter if the courseTitle is null, but as a rule we
				// want the wikiContent
				// to contain both for each course
				if (courseId != null && courseTitle != null) {
					CourseTitlePair ctp = new CourseTitlePair(courseId.trim(), 
															  courseTitle.trim());
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
}
