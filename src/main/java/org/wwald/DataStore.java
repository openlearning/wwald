package org.wwald;

import java.util.ArrayList;
import java.util.List;

public class DataStore {
	private List<Course> courses;
	
	public DataStore() {
		this.courses = new ArrayList<Course>();
		initData();
	}

	public List<Course> getAllCourses() {
		return this.courses;
	}
	
	private void initData() {

		String resource = 
			"<embed src=\"http://blip.tv/play/gtQk6o5MkPxE\" type=\"application/x-shockwave-flash\" width=\"500\" height=\"311\" allowscriptaccess=\"always\" allowfullscreen=\"true\"></embed><p style=\"font-size:11px;font-family:tahoma,arial\">Watch it on <a style=\"text-decoration:underline\" href=\"http://academicearth.org/lectures/malan-hardware/\">Academic Earth</a></p>";
		Course ucati = new Course("UCATI", 
				   				  "Understanding Computers And The Internet", 
		   						  "This course is all about understanding: understanding what's going on inside your computer when you flip on the switch, why tech support has you constantly rebooting your computer, how everything you do on the Internet can be watched by ...");
		List<Competency> competencies = new ArrayList<Competency>();
		Competency competency = new Competency("Hardware - Part 1", "Describes the basics of how computer hardware works", "");
		competency.setResource(resource);
		competencies.add(competency);
		competencies.add(new Competency("Hardware - Part 2", "Describes advanced concepts of how computer hardware works", ""));
		competencies.add(new Competency("The Internet - Part 1", "Describes the basics of how the Internet works", ""));
		competencies.add(new Competency("The Internet - Part 2", "Describes advanced concepts of how the Internet works", ""));
		ucati.setCompetencies(competencies);
		ucati.setMentor(new Mentor("David J. Malan", "7", "7/11/2010"));
    	courses.add(ucati);
    	
		Course introcs = new Course("INTROCS", 
				   					"Introduction To Computer Science", 
		   							"Introduction to Computer Science I is a first course in computer science at Harvard College for concentrators and non-concentrators alike. More than just teach you how to program, this course teaches you how to think more methodically and how to ..."); 
    	courses.add(introcs);

    	Course introcspyprog = new Course("INTROCSPYPROG",
				   						  "Introduction to Computer Science and Programming (using Python)", 
		   								  "This subject is aimed at students with little or no programming experience. It aims to provide students with an understanding of the role computation can play in solving problems. It also aims to help students, regardless of their major, to ..."); 
    	courses.add(introcspyprog);
	}

	public Course getCourse(String id) {
		Course course = null;
		for(Course aCourse : this.courses) {
			if(aCourse.getId().equals(id)) {
				course = aCourse;
				break;
			}
		}
		return course;
	}
}
