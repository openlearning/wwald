package org.wwald.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.jasypt.util.password.PasswordEncryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wwald.model.Course;
import org.wwald.model.CourseEnrollmentStatus;
import org.wwald.model.Question;
import org.wwald.model.User;
import org.wwald.model.UserCourseStatus;
import org.wwald.model.UserMeta;

import util.TestObjectsRepository;

public class ApplicationFacadeTest {

	private ApplicationFacade appFacade;
	private IDataFacade dataFacade;
	private Connection conn;
	public static final String DATABASE_ID = "localhost";
	
	@Before
	public void setUp() throws Exception {
		this.dataFacade = createMock(IDataFacade.class);
		this.appFacade = new ApplicationFacade(this.dataFacade);
	}
	
	@After
	public void tearDown() throws Exception {		
	}
	
	@Test
	public void testLogin() throws Exception {
		UserMeta user = 
			TestObjectsRepository.getInstance().getUserUserMeta("dvidakovich").userMeta;
		String username = user.getIdentifier();
		String password = username;
		
		//create some mocks we need for this test case
		Connection mockConn = createMock(Connection.class);
		PasswordEncryptor mockPasswordEncryptor = 
			createMock(PasswordEncryptor.class);
		
		//record expected behavior in the mock objects
		expect(mockPasswordEncryptor.checkPassword(password, password)).
			andReturn(true);
		expect(this.dataFacade.retreivePassword(mockConn, username)).
			andReturn(password);
		expect(this.dataFacade.retreiveUserByUsername(mockConn, username)).
			andReturn(new User(username));
		expect(this.dataFacade.retreiveUserMetaByIdentifierLoginVia(mockConn, username, UserMeta.LoginVia.INTERNAL)).
			andReturn(new UserMeta());
		replay(mockPasswordEncryptor);
		replay(this.dataFacade);		
		
		//call the API under test
		UserMeta loggedInUser = 
			this.appFacade.login(user.getIdentifier(), 
								 user.getIdentifier(), 
								 mockConn,
								 mockPasswordEncryptor);
		
		//verify expected behavior
		verify(this.dataFacade);
		verify(mockPasswordEncryptor);
	}

	@Test
	public void testEnrollInCourse() throws Exception {
		UserMeta user = 
			TestObjectsRepository.getInstance().getUserUserMeta("dvidakovich").userMeta;
		Course course = TestObjectsRepository.getInstance().getCourse("Physics");
		
		//create mock objects and record expected bahavior
		Connection mockConn = createMock(Connection.class);
		Statement mockStmt = createMock(Statement.class);
		ResultSet mockRs = createMock(ResultSet.class);
		
		expect(mockConn.createStatement()).andReturn(mockStmt);
		String sql = 
			String.format(Sql.RETREIVE_COURSE_ENROLLMENTS_BY_USER_AND_COURSE, 
						  user.getUserid(), 
						  DataFacadeRDBMSImpl.wrapForSQL(course.getId()));
		expect(mockStmt.executeQuery(sql)).andReturn(mockRs);
		expect(mockRs.next()).andReturn(false);
		CourseEnrollmentStatus expectedCourseEnrollmentStatus = 
			new CourseEnrollmentStatus(course.getId(), 
									   user.getUserid(), 
									   UserCourseStatus.ENROLLED, 
									   null);
		this.dataFacade.
			addCourseEnrollmentAction(eq(mockConn), 
									  eqCourseEnrollmentStatusWithoutCheckingTimestamp(expectedCourseEnrollmentStatus));
		this.dataFacade.insertCourseEnrollment(mockConn, user, course);
		
		replay(mockConn, mockStmt, mockRs, this.dataFacade);
		
		this.appFacade.enrollInCourse(user, course, mockConn);
		
		verify(mockConn);
		verify(mockStmt);
		verify(mockRs);
		verify(this.dataFacade);
		
	}
	
	@Test(expected=ApplicationException.class)
	public void testEnrollInCourseWhenAlreadyEnrolled() throws Exception {
		UserMeta user = 
			TestObjectsRepository.getInstance().getUserUserMeta("dvidakovich").userMeta;
		Course course = TestObjectsRepository.getInstance().getCourse("Physics");
		
		//create mock objects and record expected bahavior
		Connection mockConn = createMock(Connection.class);
		Statement mockStmt = createMock(Statement.class);
		ResultSet mockRs = createMock(ResultSet.class);
		
		expect(mockConn.createStatement()).andReturn(mockStmt);
		String sql = 
			String.format(Sql.RETREIVE_COURSE_ENROLLMENTS_BY_USER_AND_COURSE, 
						  user.getUserid(), 
						  DataFacadeRDBMSImpl.wrapForSQL(course.getId()));
		expect(mockStmt.executeQuery(sql)).andReturn(mockRs);
		expect(mockRs.next()).andReturn(true);
		CourseEnrollmentStatus expectedCourseEnrollmentStatus = 
			new CourseEnrollmentStatus(course.getId(), 
									   user.getUserid(), 
									   UserCourseStatus.ENROLLED, 
									   null);
		this.dataFacade.
			addCourseEnrollmentAction(eq(mockConn), 
									  eqCourseEnrollmentStatusWithoutCheckingTimestamp(expectedCourseEnrollmentStatus));
		this.dataFacade.insertCourseEnrollment(mockConn, user, course);
		
		replay(mockConn, mockStmt, mockRs, this.dataFacade);
		
		this.appFacade.enrollInCourse(user, course, mockConn);
		
		verify(mockConn);
		verify(mockStmt);
		verify(mockRs);
		verify(this.dataFacade);
		
	}

	@Test
	public void testDropCourse() throws Exception {
		UserMeta user = 
			TestObjectsRepository.getInstance().getUserUserMeta("dvidakovich").userMeta;
		Course course = TestObjectsRepository.getInstance().getCourse("Physics");
		
		//create mock objects and record expected bahavior
		Connection mockConn = createMock(Connection.class);
		Statement mockStmt = createMock(Statement.class);
		ResultSet mockRs = createMock(ResultSet.class);
		
		expect(mockConn.createStatement()).andReturn(mockStmt);
		String sql = 
			String.format(Sql.RETREIVE_COURSE_ENROLLMENTS_BY_USER_AND_COURSE, 
						  user.getUserid(), 
						  DataFacadeRDBMSImpl.wrapForSQL(course.getId()));
		expect(mockStmt.executeQuery(sql)).andReturn(mockRs);
		expect(mockRs.next()).andReturn(true);
		
		CourseEnrollmentStatus courseEnrollmentStatus = 
			new CourseEnrollmentStatus(course.getId(), 
									   user.getUserid(), 
									   UserCourseStatus.DROPPED,
									   null);
		this.dataFacade.
			addCourseEnrollmentAction(eq(mockConn), eqCourseEnrollmentStatusWithoutCheckingTimestamp(courseEnrollmentStatus));
		
		this.dataFacade.
			deleteCourseEnrollment(mockConn, user, course);
		
		replay(mockConn, mockStmt, mockRs, this.dataFacade);
		
		//call the API which we are testing
		this.appFacade.dropCourse(user, course, mockConn);
		
		//verify behavior
		verify(mockConn);
		verify(mockStmt);
		verify(mockRs);
		verify(this.dataFacade);
	}
	
	@Test(expected=ApplicationException.class)
	public void testDropCourseWhenNotEnrolled() throws Exception {
		UserMeta user = 
			TestObjectsRepository.getInstance().getUserUserMeta("dvidakovich").userMeta;
		Course course = TestObjectsRepository.getInstance().getCourse("Physics");
		
		//create mock objects and record expected bahavior
		Connection mockConn = createMock(Connection.class);
		Statement mockStmt = createMock(Statement.class);
		ResultSet mockRs = createMock(ResultSet.class);
		
		expect(mockConn.createStatement()).andReturn(mockStmt);
		String sql = 
			String.format(Sql.RETREIVE_COURSE_ENROLLMENTS_BY_USER_AND_COURSE, 
						  user.getUserid(), 
						  DataFacadeRDBMSImpl.wrapForSQL(course.getId()));
		expect(mockStmt.executeQuery(sql)).andReturn(mockRs);
		expect(mockRs.next()).andReturn(false);
		
		CourseEnrollmentStatus courseEnrollmentStatus = 
			new CourseEnrollmentStatus(course.getId(), 
									   user.getUserid(), 
									   UserCourseStatus.DROPPED,
									   null);
		this.dataFacade.
			addCourseEnrollmentAction(eq(mockConn), eqCourseEnrollmentStatusWithoutCheckingTimestamp(courseEnrollmentStatus));
		
		this.dataFacade.
			deleteCourseEnrollment(mockConn, user, course);
		
		replay(mockConn, mockStmt, mockRs, this.dataFacade);
		
		//call the API which we are testing
		this.appFacade.dropCourse(user, course, mockConn);
		
		//verify behavior
		verify(mockConn);
		verify(mockStmt);
		verify(mockRs);
		verify(this.dataFacade);
	}
//
//	@Test
//	public void testGetUserCourseStatus() {
//		fail("Not yet implemented");
//	}
	
	@Test
	public void testAskQuestion() throws Exception {
		//TODO: Complete this test
		UserMeta userMeta = TestObjectsRepository.getInstance().getUserUserMeta("dvidakovich").userMeta;
		Question question = new Question("question title", "question discussion", "Physics");
		question.setUserMeta(userMeta);
		
		//create mocks
		Connection mockConn = createMock(Connection.class);
		
		//record expected behavior
		
	}
	
	public static CourseEnrollmentStatus eqCourseEnrollmentStatusWithoutCheckingTimestamp(CourseEnrollmentStatus in) {
		EasyMock.reportMatcher(new CourseEnrollmentStatusMatcher(in));
		return null;
	}
	
	public static class CourseEnrollmentStatusMatcher implements IArgumentMatcher {

		private CourseEnrollmentStatus expected;
		
		public CourseEnrollmentStatusMatcher(CourseEnrollmentStatus expected) {
			this.expected = expected;
		}
		
		public void appendTo(StringBuffer arg0) {
			StringBuffer buff = new StringBuffer();
			buff.append("Checks the expected and actual " +
						"CourseEnrollmentStatus for equal userid, course id, " +
						"and UserCourseStatus. Ignores timesatmp");
		}

		public boolean matches(Object actual) {
			if(actual == null) {
				return false;
			}
			if(!expected.getClass().equals(actual.getClass())) {
				return false;
			}
			CourseEnrollmentStatus actualStatus = (CourseEnrollmentStatus)actual;
			if(expected.getUserCourseStatus().equals(actualStatus.getUserCourseStatus()) && 
			   expected.getCourseId().equals(actualStatus.getCourseId()) &&
			   expected.getUserid() == actualStatus.getUserid()) {
				return true;
			}
			return false;
		}		
	}

}
