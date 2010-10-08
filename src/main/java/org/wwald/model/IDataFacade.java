package org.wwald.model;

import java.util.List;

public interface IDataFacade {
	
	//Course
	public List<Course> retreiveCourses();
	public List<Course> retreiveCouresesListedInCourseWiki();
	public Course retreiveCourse(String courseId);
	public String retreiveCourseWiki();
	public void updateCourse(Course course);
	public void insertCourse(Course course);
	public void upsertCourse(Course course);
	public void updateCourseWiki(String wikiContents);
	
	//Competencies
	public List<Competency> retreiveAllCompetencies();
	public List<Competency> retreiveCompetenciesForCourse(Course course);
	public String retreiveCompetenciesWiki(String courseId);
	public void updateCompetency(String courseId, Competency competency);
	public Competency insertCompetency(String title);
	public void upsertCompetency(Competency competency);
	public void deleteCompetency(Competency competency);
	
	//Mentors
	public List<Mentor> retreiveAllMentors();
	public List<Mentor> retreiveMentorsForCourse();
	public List<Mentor> retreiveMentorsForCompetency();
	public void updateMentor(Mentor mentor);
	public void insertMentor(Mentor mentor);
	public void upsertMentor(Mentor mentor);
	public void deleteMentor(Mentor mentor);
	
}
