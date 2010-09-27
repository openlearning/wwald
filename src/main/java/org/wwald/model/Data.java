package org.wwald.model;

public class Data {
	public static String courses[][] = {
			{"INTROCS", "Introduction To Computer Science", "Introduction to Computer Science I is a first course in computer science at Harvard College for concentrators and non-concentrators alike."},
			{"INTROCSPROG", "Introduction to Computer Science and Programming (using Python)", "This subject is aimed at students with little or no programming experience."},
			{"PROGPAR", "Programming Paradigms", "Lecture by Professor Jerry Cain for Programming Paradigms (CS107) in the Stanford University Computer Science department. Professor Cain provides an overview of the course."},
			{"UCATI", "Understanding Computers And The Internet", "This course is all about understanding: understanding what is going on inside your computer when you flip on the switch"},
		};

	public static String competencies[][] = {
			{"L1","Lecture 1","Description of lecture 1","Resources for lecture 1"},
			{"L2","Lecture 2","Description of lecture 2","Resources for lecture 2"},
			{"L3","Lecture 3","Description of lecture 3","Resources for lecture 3"},
		  };

	public static String mentors[][] = {
			{"1", "David", "J", "Malan", "Professor at Harvard"}
		 };

	public static String courseCompetencies[][] = {
						{"UCATI","L1"},	
						{"UCATI","L2"},
						{"UCATI","L3"},
						{"INTROCS","L1"},	
						{"INTROCS","L2"},
						{"INTROCS","L3"},
						{"INTROCSPROG","L1"},	
						{"INTROCSPROG","L2"},
						{"INTROCSPROG","L3"},
						{"PROGPAR","L1"},	
						{"PROGPAR","L2"},
						{"PROGPAR","L3"},
					};

	public static String courseMentors[][] = {
					{"UCATI","1"},
					{"INTROCS","1"},
					{"INTROCSPROG","1"},
					{"PROGPAR","1"},
			   };
}
