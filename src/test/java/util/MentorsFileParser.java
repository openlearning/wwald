package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.wwald.model.Mentor;

public class MentorsFileParser {
	
	private String filePath;
	
	private LineProcessor lineProcessor;
	
	private static final String TAG = "[mentor]";
		
	private class LineProcessor {
		private List<Mentor> mentors = new ArrayList<Mentor>();
		private Mentor mentor;
		
		public void processText(String text) {
			if(text == null || text.equals("")) {
				return;
			}
			if(text.trim().startsWith("//")) {
				return;
			}
			if(text.equals(TAG)) {
				if(this.mentor != null) {
					this.mentors.add(this.mentor);
					this.mentor = null;
				}
				else {
					//the first time TAG is seen in the file
					this.mentor = new Mentor();
				}
				return;
			}
			if(this.mentor == null) {
				this.mentor = new Mentor();
			}
			if(namePopulated()) {
				String bio = this.mentor.getShortBio(); 
				if(bio == null) {
					bio = "";
				}
				bio += text;
				this.mentor.setShortBio(bio);
			}
			else {
				mentorNamePopulator(this.mentor, text);
			}
		}
		
		public Mentor[] getMentors() {
			Mentor mentorsArr[] = new Mentor[this.mentors.size()];
			mentors.toArray(mentorsArr);
			return mentorsArr;
		}

		

		private boolean namePopulated() {
			boolean retVal = false;
			if(this.mentor != null) {
				String firstName = this.mentor.getFirstName();
				String mi = this.mentor.getMiddleInitial();
				String lastName = this.mentor.getLastName();
				//the following three if's could have been combined
				//in 1 statement, but I have kept them as 3 for the
				//sake of clarity
				if(firstName != null && !firstName.equals("")) {
					retVal = true;
				}
				if(mi != null && !mi.equals("")) {
					retVal = true;
				}
				if(lastName != null && !lastName.equals("")) {
					retVal = true;
				}
			}
			return retVal;
		}
		
	}
	
	public MentorsFileParser(URL fileUrl) {
		if(fileUrl == null) {
			String msg = "fileUrl cannot be null";
			throw new IllegalArgumentException(msg);
		}
		this.filePath = fileUrl.getPath();
		this.lineProcessor = new LineProcessor();
	}
	
	public Mentor[] parse() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(this.filePath));
		String line = null;
	
		while((line = reader.readLine()) != null) {
			this.lineProcessor.processText(line);
		}
		
		return this.lineProcessor.getMentors();
	}
	
	public static void mentorNamePopulator(Mentor mentor, String text) {
		String nameParts[] = text.split(" ");
		mentor.setFirstName(nameParts[0]);
		mentor.setMiddleInitial(nameParts[1]);
		mentor.setLastName(nameParts[2]);
	}
	
	public static void main(String args[]) throws Exception {
		ClassLoader appClassLoader = DataInitializer.class.getClassLoader(); 
		URL mentorsCreationSqlFileUrl = appClassLoader.getResource("data/mentors.txt");
		MentorsFileParser parser = new MentorsFileParser(mentorsCreationSqlFileUrl);
		Mentor mentors[] = parser.parse();
		System.out.println("Printing mentors");
		for(Mentor mentor : mentors) {
			System.out.println(mentor);
		}
	}
}
