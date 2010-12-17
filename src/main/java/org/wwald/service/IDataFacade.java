package org.wwald.service;

import java.sql.Connection;
import java.util.List;

import org.wwald.model.Competency;
import org.wwald.model.Course;
import org.wwald.model.CourseEnrollmentStatus;
import org.wwald.model.Mentor;
import org.wwald.model.StaticPagePOJO;
import org.wwald.model.StatusUpdate;
import org.wwald.model.User;
import org.wwald.model.UserMeta;
import org.wwald.view.UserForm;

public interface IDataFacade {
	
	//Course
	public List<Course> retreiveCourses(Connection c) throws DataException ;
	public Course retreiveCourse(Connection c, String courseId) throws DataException;
	public String retreiveCourseWiki(Connection c) throws DataException;
	public void updateCourse(Connection c, Course course) throws DataException;
	public void insertCourse(Connection c, Course course) throws DataException;
	public void upsertCourse(Connection c, Course course) throws DataException;
	public void updateCourseWiki(Connection c, String wikiContents) throws DataException;
	public CourseEnrollmentStatus getCourseEnrollmentStatus(Connection c, UserMeta userMeta, Course course) throws DataException;
	public void addCourseEnrollmentAction(Connection c, CourseEnrollmentStatus courseEnrollmentStatus) throws DataException;
	
	//Competencies
	public void updateCompetenciesWikiContents(Connection c, String courseId, String contents) throws DataException;	
	public List<Competency> retreiveAllCompetencies(Connection c) throws DataException;
	public List<Competency> retreiveCompetenciesForCourse(Connection c, Course course) throws DataException;
	public Competency retreiveCompetency(Connection c, String courseId, String competencyId) throws DataException;
	public String retreiveCompetenciesWiki(Connection c, String courseId) throws DataException;
	public void updateCompetency(Connection c, String courseId, Competency competency) throws DataException;
	public Competency insertCompetency(Connection c, Course course, String title) throws DataException;
	public void upsertCompetency(Connection c, Competency competency) throws DataException;
	public void deleteCompetency(Connection c, Competency competency) throws DataException;
	
	//Mentors
	public List<Mentor> retreiveAllMentors(Connection c) throws DataException;
	public List<Mentor> retreiveMentorsForCourse(Connection c) throws DataException;
	public List<Mentor> retreiveMentorsForCompetency(Connection c) throws DataException;
	public void updateMentor(Connection c, Mentor mentor) throws DataException;
	public void insertMentor(Connection c, Mentor mentor) throws DataException;
	public void upsertMentor(Connection c, Mentor mentor) throws DataException;
	public void deleteMentor(Connection c, Mentor mentor) throws DataException;
	
	//Status updates
	public List<StatusUpdate> getStatusUpdates(Connection c) throws DataException;
	
	//User
	public void insertUser(Connection conn, User user, UserMeta userMeta) throws DataException;
	public void updateUser(Connection conn, User user, UserForm.Field... userFields) throws DataException;
	public List<User> retreiveAllUsers(Connection c) throws DataException ;
	public User retreiveUserByUsername(Connection conn, String username) throws DataException;
	public String retreivePassword(Connection conn, String username) throws DataException;
	
	//UserMeta
	public void insertUserMeta(Connection conn, UserMeta userMeta) throws DataException;
	public UserMeta retreiveUserMetaByIdentifierLoginVia(Connection conn, String identifer, UserMeta.LoginVia loginVia) throws DataException;
	public void updateUserMetaRole(Connection conn, UserMeta userMeta) throws DataException;
	
	//Static Pages
	public StaticPagePOJO retreiveStaticPage(Connection c, String id) throws DataException;
	public void upsertStaticPage(Connection c, StaticPagePOJO page) throws DataException;
	
	//KVTable
	public String retreiveFromKvTable(Connection c, String k) throws DataException;
	public void upsertKvTable(Connection c, String k, String v) throws DataException;
	public String retreiveFromKvTableClob(Connection c, String k) throws DataException;
	public void upsertKvTableClob(Connection c, String k, String v) throws DataException;
	
	
}
