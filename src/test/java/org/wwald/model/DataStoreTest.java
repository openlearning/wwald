package org.wwald.model;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.DataInitializer;

public class DataStoreTest {

	private DataFacadeRDBMSImpl dataStore;
	
	@Before
	public void setUp() throws Exception {
		this.dataStore = new DataFacadeRDBMSImpl();
		DataInitializer di = new DataInitializer();
		di.initData(ConnectionPool.getConnection());
	}
	
	@Test
	public void testGetAllCourses() {
		List<Course> courses = dataStore.retreiveCourses(ConnectionPool.getConnection());
		assertNotNull(courses);
		assertEquals(4, courses.size());
		
		for(int i = 0; i < 3; i++) {
			Course course = courses.get(i);
			assertEquals(DataInitializer.courses[i][0], course.getId());
			assertEquals(DataInitializer.courses[i][1], course.getTitle());
			assertEquals(DataInitializer.courses[i][2], course.getDescription());
			
			List<Competency> competencies = course.getCompetencies();
			assertNotNull(competencies);
			assertEquals(3,competencies.size());
			
			for(int j = 0; j < 2; j++) {
				Competency competency = competencies.get(j);
				assertEquals(Integer.valueOf(DataInitializer.competencies[j][0]).intValue(), competency.getId());
				assertEquals(DataInitializer.competencies[j][2], competency.getTitle());
				assertEquals(DataInitializer.competencies[j][3], competency.getDescription());
				assertEquals(DataInitializer.competencies[j][4], competency.getResource());
			}
			
		}
		
	}

	@After
	public void tearDown() throws Exception {
	}

}
