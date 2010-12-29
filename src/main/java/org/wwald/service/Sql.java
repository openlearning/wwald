package org.wwald.service;

public class Sql {
	public static String INSERT_USER = "INSERT into USER (username, password, email) VALUES (%s, %s, %s); " +
									   "INSERT into USER_META (userid, identifier, login_via, role) VALUES (NULL, %s, %s, %s);";
	public static String UPDATE_USER = "UPDATE USER SET email=%s, password=%s WHERE username=%s;";
	public static String RETREIVE_ALL_USERS = "SELECT * from USER;";
	public static String RETREIVE_USER =
		"SELECT * FROM USER WHERE username=%s AND password=%s;";
	public static String RETREIVE_USER_BY_USERNAME = "SELECT * FROM USER WHERE username=%s;";
	
	public static String INSERT_USER_META = "INSERT into USER_META (userid, identifier, login_via, role) VALUES (NULL, %s, %s, %s);";
	public static String UPDATE_USER_META_ROLE = "UPDATE USER_META set role=%s WHERE userid=%s;";
	public static String RETREIVE_ALL_USER_META = "SELECT * from USER_META;";
	public static String RETREIVE_USER_META = "SELECT * from USER_META where userid=%s;";
	public static String RETREIVE_USER_META_BY_IDETIFIER_LOGIN_VIA = "SELECT * from USER_META where identifier=%s AND login_via=%s;";
	
	public static String RETREIVE_PASSWORD = "SELECT password from USER where username=%s";
	
	public static String RETREIVE_ALL_MENTORS = "SELECT * FROM USER_META WHERE ROLE='MENTOR';";
	
	public static String INSERT_COURSES_WIKI = "INSERT INTO COURSES_WIKI (id, content) VALUES (%s,%s);";
	
	public static String RETREIVE_COURSE = "SELECT * FROM COURSE WHERE id=%s";
	public static String INSERT_COURSE_BASIC = "INSERT INTO COURSE (id, title) VALUES (%s,%s)";
	public static String INSERT_COURSE = "INSERT INTO COURSE (id, title, description) VALUES (%s, %s, %s);";
	public static String UPDATE_COURSE = "UPDATE COURSE SET title=%s, description=%s WHERE id=%s;";
	
	public static String INSERT_COURSE_MENTOR = "INSERT INTO COURSE_MENTORS (course_id, mentor_userid) VALUES (%s, %s);";
	public static String UPDATE_COURSE_MENTORS = "UPDATE COURSE_MENTORS SET mentor_userid=%s WHERE course_id=%s;";
	public static String RETREIVE_MENTORS_FOR_COURSE = "SELECT (mentor_userid) FROM COURSE_MENTORS WHERE course_id=%s";
	
	public static String RETREIVE_COURSES_WIKI = "SELECT * FROM COURSES_WIKI;";
	public static String UPDATE_COURSES_WIKI = "UPDATE COURSES_WIKI SET content=%s WHERE id=1";
	
	public static String INSERT_COURSE_COMPETENCIES_WIKI = "INSERT INTO COURSE_COMPETENCIES_WIKI (course_id, contents) VALUES (%s, %s);";
	public static String UPDATE_COMPETENCIES_WIKI = "UPDATE COURSE_COMPETENCIES_WIKI SET contents=%s WHERE course_id=%s;";
	public static String UPDATE_COMPETENCY_TITLE = "UPDATE COMPETENCY SET title=%s WHERE course_id=%s AND title=%s";
	public static String RETREIVE_COMPETENCIES_WIKI = "SELECT * FROM COURSE_COMPETENCIES_WIKI WHERE course_id=%s;";
	
	public static String RETREIVE_COMPETENCIES = "SELECT * FROM COMPETENCY;";
	public static String RETREIVE_COMPETENCIES_FOR_COURSE = "SELECT * FROM COMPETENCY WHERE course_id=%s;";
	public static String RETREIVE_COMPETENCIES_BY_COURSE_AND_COMPETENCY_TITLE = "SELECT * FROM COMPETENCY WHERE COMPETENCY.course_id=%s AND COMPETENCY.title=%s";
	public static String INSERT_COMPETENCY_BASIC = "INSERT INTO COURSE_COMPETENCIES_WIKI (course_id, contents) VALUES (%s,'');";
	public static String INSERT_COMPETENCY = "INSERT INTO COMPETENCY (id, course_id, title, description, resources) VALUES (%s,%s,%s,%s,%s);";
	public static String UPDATE_COMPETENCY = "UPDATE COMPETENCY SET COMPETENCY.description=%s, COMPETENCY.resources=%s WHERE COMPETENCY.id=%s";
	
	public static String RETREIVE_ALL_COURSE_ENROLLMENTS = "SELECT * FROM COURSE_ENROLLMENTS;";
	public static String RETREIVE_COURSE_ENROLLMENTS_BY_USER = "";
	public static String RETREIVE_COURSE_ENROLLMENTS_BY_COURSE = "";
	public static String RETREIVE_COURSE_ENROLLMENTS_BY_USER_AND_COURSE = "SELECT userid, course_id FROM COURSE_ENROLLMENTS WHERE userid=%s AND course_id=%s;";
	public static String INSERT_COURSE_ENROLLMENT = "INSERT INTO COURSE_ENROLLMENTS VALUES (%s, %s)";
	public static String DELETE_COURSE_ENROLLMENT = "DELETE FROM COURSE_ENROLLMENTS WHERE userid=%s AND course_id=%s;";
	
	public static String INSERT_COURSE_ENROLLMENT_STATUS = "INSERT INTO COURSE_ENROLLMENT_ACTIONS VALUES (%s, %s, %s, %s);";
	public static String RETREIVE_COURSE_ENROLLMENT_STATUS = "SELECT * FROM COURSE_ENROLLMENT_ACTIONS WHERE course_id=%s AND userid=%s ORDER BY tstamp DESC;";
	public static String RETREIVE_ALL_COURSE_ENROLLMENT_STATUSES = "SELECT * FROM COURSE_ENROLLMENT_ACTIONS;";
	
	public static String RETREIVE_STATIC_PAGE = "SELECT contents FROM STATIC_PAGES WHERE id=%s";
	public static String INSERT_STATIC_PAGE = "INSERT INTO STATIC_PAGES VALUES (%s,%s);";
	public static String UPDATE_STATIC_PAGE = "UPDATE STATIC_PAGES SET contents=%s WHERE id=%s;";
	
	public static String INSERT_KVTABLE = "INSERT INTO KVTABLE (k, v) VALUES (%s, %s);";
	public static String UPDATE_KVTABLE = "UPDATE KVTABLE SET v=%s WHERE k=%s;";
	public static String RETREIVE_KVTABLE = "SELECT v FROM KVTABLE WHERE k=%s;";
	
	public static String INSERT_KVTABLE_CLOB = "INSERT INTO KVTABLE_CLOB (k, v) VALUES (%s, %s);";
	public static String UPDATE_KVTABLE_CLOB = "UPDATE KVTABLE_CLOB SET v=%s WHERE k=%s;";
	public static String RETREIVE_KVTABLE_CLOB = "SELECT v FROM KVTABLE_CLOB WHERE k=%s;";
}
