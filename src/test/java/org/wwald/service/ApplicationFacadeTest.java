package org.wwald.service;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wwald.model.ConnectionPool;
import org.wwald.model.Course;
import org.wwald.model.UserMeta;
import org.wwald.util.CompetencyUniqueIdGenerator;

import util.DataInitializer;
import util.TestObjectsRepository;

public class ApplicationFacadeTest {

	private ApplicationFacade appFacade;
	private DataFacadeRDBMSImpl dataFacade;
	private Connection conn;
	public static final String DATABASE_ID = "localhost";
	
	@Before
	public void setUp() throws Exception {
		CompetencyUniqueIdGenerator.reset(null);
		this.dataFacade = new DataFacadeRDBMSImpl();
		DataInitializer di = new DataInitializer();
		this.conn = ConnectionPool.getConnection(DATABASE_ID);
		di.initData(conn);
		
		this.appFacade = new ApplicationFacade(this.dataFacade);
	}
	
	@After
	public void tearDown() throws Exception {		
		Statement stmt = this.conn.createStatement();
		stmt.execute("SHUTDOWN");
		ConnectionPool.closeConnection(DATABASE_ID);
	}
	
	@Test
	public void testLogin() throws Exception {
		UserMeta user = 
			TestObjectsRepository.getInstance().getUserUserMeta("dvidakovich").userMeta;
		UserMeta loggedInUser = 
			this.appFacade.login(user.getIdentifier(), 
								 user.getIdentifier(), 
								 DATABASE_ID);
		assertNotNull(loggedInUser);
		assertEquals(user.getIdentifier(), loggedInUser.getIdentifier());
		assertEquals(user.getUserid(), loggedInUser.getUserid());
		assertEquals(user.getLoginVia(), loggedInUser.getLoginVia());
		assertEquals(user.getRole(), loggedInUser.getRole());
	}

	@Test
	public void testLogout() {
		System.out.println("method logout() needs a WWALDSession. Test it from a Wicket setup");
	}

	@Test
	public void testEnrollInCourse() throws Exception {
		UserMeta user = 
			TestObjectsRepository.getInstance().getUserUserMeta("dvidakovich").userMeta;
		Course course =
			TestObjectsRepository.getInstance().getCourse("Physics");
		
		this.appFacade.enrollInCourse(user, course, DATABASE_ID);
		
		//verify if a row was inserted in the COURSE_ENROLLMENT_ACTIONS table
		String sql = String.format(Sql.RETREIVE_COURSE_ENROLLMENT_STATUS,
								DataInitializer.wrapForSQL(course.getId()),
								user.getUserid());
		
		//verify if a row was inserted in COURSE_ENROLLMENTS table
		
	}

//	@Test
//	public void testDropCourse() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetUserCourseStatus() {
//		fail("Not yet implemented");
//	}

}
