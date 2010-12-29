package org.wwald.util;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.*;

import org.wwald.util.CourseWikiParser.CourseTitlePair;
import org.wwald.util.CourseWikiParser.UpdateHint;


public class TestCourseWikiParser {
	
	private CourseWikiParser courseWikiParser;
	private String wikiContents[] = {"C1 | course 1\n" + 
			  						 "C2 | course 2\n" + 
			  						 "C3 | course 3\n",
			  						 
			  						"#Comment\n" +
									 "C1 | course 1\n" + 
									 "C2 | course 2\n" + 
									 "C3 | course 3\n",
									 
									 "#Comment\n" +
									 "C1 | course 1\n" +
									 "\n" +
									 "C2 | course 2\n" + 
									 "C3 | course 3\n",
									 
									 "\n",
									 
									 "#Comment\n" +
									 "C1 | course 1-> c1\n" + 
									 "C2 | course 2\n" + 
									 "C3 | course 3\n"			
	};
	
	@Before
	public void setUp() {
		this.courseWikiParser = new CourseWikiParser();
	}
	
	@After
	public void tearDown() {
		this.courseWikiParser = null;
	}
	
	@Test
	public void testParse1() throws Exception {
		List<CourseTitlePair> courseTitlePairs = 
			this.courseWikiParser.parse(wikiContents[0]);
		assertNotNull(courseTitlePairs);
		
		assertEquals("C1", courseTitlePairs.get(0).courseId);
		assertEquals("course 1", courseTitlePairs.get(0).courseTitle);
		assertNull(courseTitlePairs.get(0).updatedCourseTitle);
		assertEquals("C2", courseTitlePairs.get(1).courseId);
		assertEquals("course 2", courseTitlePairs.get(1).courseTitle);
		assertNull(courseTitlePairs.get(1).updatedCourseTitle);
		assertEquals("C3", courseTitlePairs.get(2).courseId);
		assertEquals("course 3", courseTitlePairs.get(2).courseTitle);
		assertNull(courseTitlePairs.get(2).updatedCourseTitle);
	}
	
	@Test
	public void testParse2() throws Exception {
		List<CourseTitlePair> courseTitlePairs = 
			this.courseWikiParser.parse(wikiContents[1]);
		assertNotNull(courseTitlePairs);
		
		assertEquals("C1", courseTitlePairs.get(0).courseId);
		assertEquals("course 1", courseTitlePairs.get(0).courseTitle);
		assertNull(courseTitlePairs.get(0).updatedCourseTitle);
		assertEquals("C2", courseTitlePairs.get(1).courseId);
		assertEquals("course 2", courseTitlePairs.get(1).courseTitle);
		assertNull(courseTitlePairs.get(1).updatedCourseTitle);
		assertEquals("C3", courseTitlePairs.get(2).courseId);
		assertEquals("course 3", courseTitlePairs.get(2).courseTitle);
		assertNull(courseTitlePairs.get(2).updatedCourseTitle);
	}
	
	@Test
	public void testParse3() throws Exception {
		List<CourseTitlePair> courseTitlePairs = 
			this.courseWikiParser.parse(wikiContents[2]);
		assertNotNull(courseTitlePairs);
		
		assertEquals("C1", courseTitlePairs.get(0).courseId);
		assertEquals("course 1", courseTitlePairs.get(0).courseTitle);
		assertNull(courseTitlePairs.get(0).updatedCourseTitle);
		assertEquals("C2", courseTitlePairs.get(1).courseId);
		assertEquals("course 2", courseTitlePairs.get(1).courseTitle);
		assertNull(courseTitlePairs.get(1).updatedCourseTitle);
		assertEquals("C3", courseTitlePairs.get(2).courseId);
		assertEquals("course 3", courseTitlePairs.get(2).courseTitle);
		assertNull(courseTitlePairs.get(2).updatedCourseTitle);
	}
	
	@Test
	public void testParse4() throws Exception {
		List<CourseTitlePair> courseTitlePairs = 
			this.courseWikiParser.parse(wikiContents[3]);
		assertNotNull(courseTitlePairs);
		assertEquals(0, courseTitlePairs.size());
	}
	
	@Test
	public void testParse5() throws Exception {
		List<CourseTitlePair> courseTitlePairs = 
			this.courseWikiParser.parse(wikiContents[4]);
		assertNotNull(courseTitlePairs);
		
		assertEquals("C1", courseTitlePairs.get(0).courseId);
		assertEquals("course 1", courseTitlePairs.get(0).courseTitle);
		assertEquals("c1", courseTitlePairs.get(0).updatedCourseTitle);
		assertEquals("C2", courseTitlePairs.get(1).courseId);
		assertEquals("course 2", courseTitlePairs.get(1).courseTitle);
		assertNull(courseTitlePairs.get(1).updatedCourseTitle);
		assertEquals("C3", courseTitlePairs.get(2).courseId);
		assertEquals("course 3", courseTitlePairs.get(2).courseTitle);
		assertNull(courseTitlePairs.get(2).updatedCourseTitle);
	}
	
	@Test
	public void testParseForUpdate1() throws Exception {
		UpdateHint updateHint = this.courseWikiParser.parseForUpdate(wikiContents[0]);
		assertEquals(wikiContents[0], updateHint.updatedWikiContents);
		assertNotNull(updateHint.updatedCourseTitlePairs);
		assertEquals(0, updateHint.updatedCourseTitlePairs.size());
	}
	
	@Test
	public void testParseForUpdate2() throws Exception {
		UpdateHint updateHint = this.courseWikiParser.parseForUpdate(wikiContents[1]);
		assertEquals(wikiContents[1], updateHint.updatedWikiContents);
		assertNotNull(updateHint.updatedCourseTitlePairs);
		assertEquals(0, updateHint.updatedCourseTitlePairs.size());
	}
	
	@Test
	public void testParseForUpdate3() throws Exception {
		UpdateHint updateHint = this.courseWikiParser.parseForUpdate(wikiContents[2]);
		assertEquals(wikiContents[2], updateHint.updatedWikiContents);
		assertNotNull(updateHint.updatedCourseTitlePairs);
		assertEquals(0, updateHint.updatedCourseTitlePairs.size());
	}
	
	@Test
	public void testParseForUpdate4() throws Exception {
		UpdateHint updateHint = this.courseWikiParser.parseForUpdate(wikiContents[3]);
		assertEquals(wikiContents[3], updateHint.updatedWikiContents);
		assertNotNull(updateHint.updatedCourseTitlePairs);
		assertEquals(0, updateHint.updatedCourseTitlePairs.size());
	}
	
	@Test
	public void testParseForUpdate5() throws Exception {		
		String expectedWikiContent = "#Comment\n" +
		 							 "C1 | c1\n" + 
		 							 "C2 | course 2\n" + 
		 							 "C3 | course 3\n";
		
		UpdateHint updateHint = this.courseWikiParser.parseForUpdate(wikiContents[4]);
		assertEquals(expectedWikiContent, updateHint.updatedWikiContents);
		assertNotNull(updateHint.updatedCourseTitlePairs);
		assertEquals(1, updateHint.updatedCourseTitlePairs.size());
		CourseTitlePair updatedCourseTitlePair = updateHint.updatedCourseTitlePairs.get(0);
		assertEquals("C1", updatedCourseTitlePair.courseId);
		assertEquals("course 1", updatedCourseTitlePair.courseTitle);
		assertEquals("c1", updatedCourseTitlePair.updatedCourseTitle);
	}
	
	
}
