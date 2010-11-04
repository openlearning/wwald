package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wwald.model.Competency;
import org.wwald.model.Course;
import org.wwald.model.Mentor;
import org.wwald.util.CompetencyUniqueIdGenerator;

public class CourseFileParser {
	
	private Course course;
	private String filePath;
	private Map<StatesEnum, State> statesMap;
	private State currentState;
	
	private interface State {
		void processText(String text) throws DataFileSyntaxException;
	}
	
	private enum StatesEnum {
		InitState,
		ReadingCourseTitle,
		ReadingMentors,
		ReadingDescription,
		ReadingCompetencies,
		FinalState;
	}
	
	private class ReadingCourseTitleState implements State {
		
		private int newlines = 0;
		
		public void processText(String text) throws DataFileSyntaxException {
			if(text != null) {
				if(text.trim().equals("")) {
					this.newlines++;
					if(this.newlines == 3) {
						currentState = statesMap.get(StatesEnum.ReadingMentors);
					}
				}
				else {
					populateIdAndTitle(text);
				}
			}
		}
		
		private void populateIdAndTitle(String text) throws DataFileSyntaxException {
			
			String tokens[] = text.split("\\|");
			
			StringBuffer msg = new StringBuffer();
			msg.append("Error in line '" + text + "'" + "\n");
			msg.append("Correct formal 'courseId | course title'");
			
			if(tokens == null || tokens.length != 2) {	
				throw new DataFileSyntaxException(msg.toString());
			}
			if(tokens[0] == null || tokens[1] == null) {
				throw new DataFileSyntaxException(msg.toString());
			}
			CourseFileParser.this.course.setId(tokens[0].trim());
			CourseFileParser.this.course.setTitle(tokens[1].trim());
		}
		
	}
	
	private class ReadingMentorState implements State {
		private int newlines = 0;
		public void processText(String text) throws DataFileSyntaxException {
			if(text != null) {
				if(text.trim().equals("")) {
					this.newlines++;
					if(this.newlines == 3) {
						currentState = statesMap.get(StatesEnum.ReadingDescription);
					}
				}
				else {
					Mentor mentor = new Mentor();
					MentorsFileParser.mentorNamePopulator(mentor, text);
					CourseFileParser.this.course.setMentor(mentor);
				}
			}
		}
		
	}
	
	private class ReadingDescriptionState implements State {
		
		private int newlines = 0;
		private StringBuffer descBuff = new StringBuffer("");
		
		public void processText(String text) throws DataFileSyntaxException {
			if(text != null) {
				if(text.trim().equals("")) {
					this.newlines++;
					if(this.newlines == 3) {
						CourseFileParser.this.course.setDescription(descBuff.toString());
						currentState = statesMap.get(StatesEnum.ReadingCompetencies);
					}
				}
				else {
					while(this.newlines > 0) {
						descBuff.append("\n");
						this.newlines--;
					}
					descBuff.append(text);
				}
			}
		}
		
	}
	
	private class ReadingCompetenciesState implements State {
		
		private List<Competency> competencies;
		private Competency currentCompetency;
		private State currentState;
		private Map<String, State> competenciesStateMap;
		
		private static final String INIT_STATE = "INIT_STATE";
		private static final String TITLE_STATE = "TITLE_STATE";
		private static final String DESCRIPTION_STATE = "DESCRIPTION_STATE";
		private static final String RESOURCES_STATE = "RESOURCES_STATE";
		private static final String FINAL_STATE = "FINAL_STATE";
		
		private class InitState implements State {

			public void processText(String text) throws DataFileSyntaxException {
				if(text == null || text.equals("")) {
					return;
				}
				if(!text.trim().equals("[title]")) {
					throw new DataFileSyntaxException("Was expecting [title] in file '" + filePath + "'");
				}
				else {
					ReadingCompetenciesState.this.currentState = 
						ReadingCompetenciesState.this.competenciesStateMap.get(TITLE_STATE);
				}
			}
			
		}
		
		private class TitleState implements State {
			private String nextTag = "[desc]";
			public void processText(String text) throws DataFileSyntaxException {
				if(text == null) {
					return;
				}
				if(text.equals(nextTag)) {
					ReadingCompetenciesState.this.currentState = 
						ReadingCompetenciesState.this.competenciesStateMap.get(DESCRIPTION_STATE);
				}
				else {
					ReadingCompetenciesState.this.currentCompetency = new Competency(CompetencyUniqueIdGenerator.getNextCompetencyId(null));
					ReadingCompetenciesState.this.currentCompetency.setTitle(text);
				}
			}
			
		}
		
		private class DescriptionState implements State {
			private String nextTag = "[resources]";
			private StringBuffer descriptionBuffer = new StringBuffer("");
			
			public void processText(String text) throws DataFileSyntaxException {
				if(text != null) {
					if(text.trim().equals(nextTag)) {						
						ReadingCompetenciesState.this.
							currentCompetency.
								setDescription(this.descriptionBuffer.toString());
						
						this.descriptionBuffer = new StringBuffer("");
						
						ReadingCompetenciesState.this.currentState = 
							ReadingCompetenciesState.this.competenciesStateMap.get(RESOURCES_STATE);
					}
					else {
						//use system line separator
						this.descriptionBuffer.append(text + "\n");
					}
				}
			}	
		}
		
		private class ResourcesState implements State {
			private String nextTag = "[title]";
			private String endTag = "[end]";
			private StringBuffer resourceBuffer = new StringBuffer("");
			
			public void processText(String text) throws DataFileSyntaxException {
				if(text != null) {
					if(text.trim().equals(nextTag)) {						
						ReadingCompetenciesState.this.
							currentCompetency.
								setResource(this.resourceBuffer.toString());
						
						ReadingCompetenciesState.this.
							competencies.add(ReadingCompetenciesState.this.currentCompetency);
						ReadingCompetenciesState.this.currentCompetency = null;
						
						this.resourceBuffer = new StringBuffer("");
						
						ReadingCompetenciesState.this.currentState = 
							ReadingCompetenciesState.this.competenciesStateMap.get(TITLE_STATE);
					}
					else if(text.trim().equals(endTag)) {
						ReadingCompetenciesState.this.
						currentCompetency.
							setResource(this.resourceBuffer.toString());
						
						ReadingCompetenciesState.this.
						competencies.add(ReadingCompetenciesState.this.currentCompetency);
			
						CourseFileParser.this.course.setCompetencies(ReadingCompetenciesState.this.competencies);
						
						ReadingCompetenciesState.this.currentState = 
							ReadingCompetenciesState.this.competenciesStateMap.get(FINAL_STATE);
					}
					else {
						//use system line separator
						this.resourceBuffer.append(text + "\n");
					}
				}
			}	
		}
		
		private class FinalState implements State {

			public void processText(String text) throws DataFileSyntaxException {}
			
		}
		
		public ReadingCompetenciesState() {
			this.competencies = new ArrayList<Competency>();
			this.competenciesStateMap = new HashMap<String, State>();
			this.competenciesStateMap.put(TITLE_STATE, new TitleState());
			this.competenciesStateMap.put(DESCRIPTION_STATE, new DescriptionState());
			this.competenciesStateMap.put(RESOURCES_STATE, new ResourcesState());
			this.competenciesStateMap.put(INIT_STATE, new InitState());
			this.competenciesStateMap.put(FINAL_STATE, new FinalState());
			this.currentState = this.competenciesStateMap.get(INIT_STATE);
		}
		
		public void processText(String text) throws DataFileSyntaxException {
			this.currentState.processText(text);
		}
		
	}
	
	public CourseFileParser(URL url) {
		if(url == null) {
			throw new IllegalArgumentException("url cannot be null");
		}
		this.filePath = url.getPath();
		this.course = new Course();
		
		// create all states
		this.statesMap = new HashMap<StatesEnum, State>();
		this.statesMap.put(StatesEnum.ReadingCourseTitle, new ReadingCourseTitleState());
		this.statesMap.put(StatesEnum.ReadingMentors, new ReadingMentorState());
		this.statesMap.put(StatesEnum.ReadingDescription, new ReadingDescriptionState());
		this.statesMap.put(StatesEnum.ReadingCompetencies, new ReadingCompetenciesState());
		this.currentState = this.statesMap.get(StatesEnum.ReadingCourseTitle);
	}
	
	public Course parse() throws IOException, DataFileSyntaxException {
		BufferedReader reader = new BufferedReader(new FileReader(this.filePath));
		String line = null;
	
		while((line = reader.readLine()) != null) {
			this.currentState.processText(line);
		}
		
		return this.course;
	}
	
	public static void main(String args[]) throws Exception {
		String basePath = "data/course/";
		ClassLoader appClassLoader = DataInitializer.class.getClassLoader(); 
		URL url = appClassLoader.getResource(basePath);
		File coursesDataDir = new File(url.getPath());
		File courseDataFiles[] = coursesDataDir.listFiles();
		for(File courseDataFile : courseDataFiles) {
			String courseFileName = courseDataFile.getName();
			URL courseDataFileUrl = appClassLoader.getResource(basePath + courseFileName);
			CourseFileParser parser = new CourseFileParser(courseDataFileUrl);
			Course course = parser.parse();
			System.out.println("course : \n" + course);
		}
	}
}
