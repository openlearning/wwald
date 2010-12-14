package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wwald.model.Role;
import org.wwald.model.User;

public class UsersFileParser {
	
	private String filePath;
	private List<User> users;
	private User user;
	private State currentState;
	private Map<StateEnum, State> stateMap;
	
	private enum StateEnum {
		ReadingAuthDetailsState,
		ReadingEmailState,
		ReadingRoleState,
		EndState;
	}
	
	private interface State {
		void processLine(String line) throws DataFileSyntaxException;
	}
	
	private class ReadingAuthDetailsState implements State {

		public void processLine(String line) throws DataFileSyntaxException {
			if(line != null) {
				if(line.trim().equalsIgnoreCase("[end]")) {
					currentState = stateMap.get(StateEnum.EndState);
				}
				else {
					if(!line.trim().equals("")) {
						String authDetails[] = line.split("\\|");
						if(authDetails.length != 2) {
							String msg = "Expected auth details in the following format 'username|pwd'";
							throw new DataFileSyntaxException(msg);
						}
						user = new User();
						user.setUsername(authDetails[0]);
						user.setPassword(authDetails[1]);
						currentState = stateMap.get(StateEnum.ReadingEmailState);	
					}
				}
			}	
		}
		
	}
	
	private class ReadingEmailState implements State {
		public void processLine(String line) throws DataFileSyntaxException {
			user.setEmail(line);
			currentState = stateMap.get(StateEnum.ReadingRoleState);
		}
	}
	
	private class ReadingRoleState implements State {

		public void processLine(String line) throws DataFileSyntaxException {
			if(line != null) {
				Role role = Role.valueOf(line);
				if(role != null) {
					user.setRole(role);
					users.add(user);
					user = null;
					currentState = stateMap.get(StateEnum.ReadingAuthDetailsState);
				}
				else {
					throw new DataFileSyntaxException("could not find role for " + line);
				}
				
			}
		}
		
	}
	
	private class EndState implements State {
		public void processLine(String line) {
			// do nothing
		}		
	}
	
	public UsersFileParser(URL url) {
		this.filePath = url.getPath();
		this.users = new ArrayList<User>();
		this.stateMap = new HashMap<StateEnum, State>();
		this.stateMap.put(StateEnum.ReadingAuthDetailsState, new ReadingAuthDetailsState());
		this.stateMap.put(StateEnum.ReadingEmailState, new ReadingEmailState());
		this.stateMap.put(StateEnum.ReadingRoleState, new ReadingRoleState());
		this.stateMap.put(StateEnum.EndState, new EndState());
		this.currentState = stateMap.get(StateEnum.ReadingAuthDetailsState);
	}
	
	public User[] parse() throws IOException, DataFileSyntaxException {
		BufferedReader reader = new BufferedReader(new FileReader(this.filePath));
		String line = null;
	
		while((line = reader.readLine()) != null) {
			this.currentState.processLine(line);
		}
		
		User userArr[] = new User[this.users.size()];
		return this.users.toArray(userArr);
	}
	
	public static void main(String args[]) throws IOException, DataFileSyntaxException {
		URL url = UsersFileParser.class.getClassLoader().getResource("data/users.txt");
		UsersFileParser parser = new UsersFileParser(url);
		User users[] = parser.parse();
		for(User user : users) {
			System.out.println(user);
		}
	}
}
