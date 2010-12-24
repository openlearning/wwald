package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wwald.model.Competency;
import org.wwald.model.Course;
import org.wwald.model.Mentor;
import org.wwald.model.Role;
import org.wwald.model.User;
import org.wwald.model.UserMeta;

import util.UsersFileParser.UserUserMeta;

public class TestObjectsRepository {
	
	private Map<String, UserUserMeta> users;
	private Map<String, Course> courses;
	private Mentor mentor;
	private Mentor fullyPopulatedMentor;
	
	private static TestObjectsRepository instance = new TestObjectsRepository();
	
	public TestObjectsRepository() {
		this.users = new HashMap<String, UserUserMeta>();
		this.courses = new HashMap<String, Course>();
		populateUsers();
		populateMentors();
		populateCourses();
	}
	
	public static TestObjectsRepository getInstance() {
		return instance;
	}
	
	public UserUserMeta getUserUserMeta(String username) {
		UserUserMeta userUserMeta = this.users.get(username);
		User user = userUserMeta.user;
		UserMeta userMeta = userUserMeta.userMeta;
		
		User clonedUser = new User(user.getUsername(), user.getEmail());
		clonedUser.setPassword(user.getPassword());
		
		UserMeta clonedUserMeta = new UserMeta();
		clonedUserMeta.setUserid(userMeta.getUserid());
		clonedUserMeta.setIdentifier(userMeta.getIdentifier());
		clonedUserMeta.setLoginVia(userMeta.getLoginVia());
		clonedUserMeta.setRole(userMeta.getRole());
		
		UserUserMeta clonedUserUserMeta = new UserUserMeta(clonedUser,
														   clonedUserMeta);
		return clonedUserUserMeta;
	}
	
	public Mentor getMentor() {
		
		return this.mentor;
	}
	
	public Mentor getFullyPopulatedMentor() {
		return this.fullyPopulatedMentor;
	}
	
	public Course getCourse(String id) {
		return this.courses.get(id);
	}

	private void populateUsers() {
		User expectedUser = null;
		UserMeta userMeta = null;
		UserUserMeta userUserMeta = null;
		
		expectedUser = new User("dvidakovich", "dvidakovich@mail.com");
		expectedUser.setPassword("dvidakovich");
		UserMeta expectedUserMeta = new UserMeta();
		expectedUserMeta.setIdentifier("dvidakovich");
		expectedUserMeta.setRole(Role.STUDENT);
		expectedUserMeta.setLoginVia(UserMeta.LoginVia.INTERNAL);		
		userUserMeta = new UserUserMeta(expectedUser, expectedUserMeta);
		this.users.put("dvidakovich", userUserMeta);
		
		
		expectedUser = new User("admindude", "admindude@gmail.com");
		expectedUser.setPassword("admindude");
		expectedUserMeta = new UserMeta();
		expectedUserMeta.setIdentifier("admindude");
		expectedUserMeta.setRole(Role.ADMIN);
		expectedUserMeta.setLoginVia(UserMeta.LoginVia.INTERNAL);
		userUserMeta = new UserUserMeta(expectedUser, expectedUserMeta);
		this.users.put("admindude", userUserMeta);
		
		expectedUser = new User("mevans", "mevans@hotmail.com");
		expectedUser.setPassword("mevans");
		expectedUserMeta = new UserMeta();
		expectedUserMeta.setIdentifier("mevans");
		expectedUserMeta.setRole(Role.MENTOR);
		expectedUserMeta.setLoginVia(UserMeta.LoginVia.INTERNAL);
		userUserMeta = new UserUserMeta(expectedUser, expectedUserMeta);
		this.users.put("mevans", userUserMeta);
	}
	
	private void populateMentors() {
		//we have the same mentor for all the courses
		this.mentor = new Mentor(2);
		
		this.fullyPopulatedMentor = new Mentor(2);
		this.fullyPopulatedMentor.setIdentifier("mevans");
		this.fullyPopulatedMentor.setLoginVia(UserMeta.LoginVia.INTERNAL);
		this.fullyPopulatedMentor.setRole(Role.MENTOR);
	}

	private void populateCourses() {
		Course course = null;
		Competency competency = null;

		//Physics course
		course = new Course("Physics", "Introduction to Physics", "Introduction to advanced physics");
		course.setMentor(mentor);
		competency = new Competency(0, 
						   			"Physics lecture 1", 
						   			"Description to physics lecture 1\n", 
						   			"Resources for physics lecture 1\n");
		course.getCompetencies().add(competency);
		competency = new Competency(1, 
									"Physics lecture 2", 
									"Description of physics lecture 2\n", 
									"Resources for physics lecture 2\n");
		course.getCompetencies().add(competency);
		competency = new Competency(2, 
				"Physics lecture 3", 
				"Description of physics lecture 3\n", 
				"Resources for physics lecture 3\n");
		course.getCompetencies().add(competency);
		this.courses.put("Physics", course);
		
		//Biology course
		course = new Course("Bio101", 
						    "Introduction to Biology", 
						    "Introduction to high school biology");
		course.setMentor(mentor);
		this.courses.put("Bio101", course);
		
		//Chem course		 
		course = new Course("OrganicChem", "Introduction to Organic Chemistry", "Description of Organic Chemistry");
		course.setMentor(mentor);
		competency = new Competency(3, 
									"Introduction and Drawing Lewis Structures", 
									"An introduction to organic chemistry along with a primer for drawing Lewis structures.\n",
									"Lewis Structures\n");
		course.getCompetencies().add(competency);
		this.courses.put("OrganicChem", course);
		
		
	}
	
	
}
