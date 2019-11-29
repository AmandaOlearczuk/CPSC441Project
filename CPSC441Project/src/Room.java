
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Room {
	private String roomCode;
	private String roomName;
	private User admin;
	private ArrayList<User> guests ;
	private ArrayList<AuthorizedUser> blacklist;
	private ArrayList<Integer> anonNumberTracker = new ArrayList<Integer>();
	private LinkedList<String> conversation = new LinkedList<String>();
	//private LinkedList<User,Message> messages;
	
	
	public Room(String roomcode,String roomname,User adminUser,ArrayList<User> roomGuests,ArrayList<AuthorizedUser> blacklst) {
		roomCode = roomcode;
		roomName = roomname;
		admin = adminUser;
		guests = roomGuests;
		blacklist = blacklst;
		if(adminUser.getUsername().contains("anon")) {anonNumberTracker.add(0);} //if adminUser is anonymous.
	}

	public LinkedList<String> getConversation() {
		return conversation;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public User getAdmin() {
		return admin;
	}

	public void setAdmin(User admin) {
		this.admin = admin;
	}

	public ArrayList<User> getGuests() {
		return guests;
	}

	public void setGuests(ArrayList<User> guests) {
		this.guests = guests;
	}


	public void addGuest(User guest) {
		this.guests.add(guest);
	}

	public ArrayList<AuthorizedUser> getBlacklist() {
		return blacklist;
	}

	public void setBlacklist(ArrayList<AuthorizedUser> blacklist) {
		this.blacklist = blacklist;
	}
	
	public boolean isBanned(AuthorizedUser user) {
		//TODO
		return false;
	}
	
	
	/**
	 * Generates username for anonymous users, based on the numbers that have been used so far.
	 * @return string - username
	 */
	public String generateAnonUsername() {
		int max = Collections.max(anonNumberTracker);
		anonNumberTracker.add(max+1);
		return "anon" + Integer.toString(max+1);
	}
	
	public String getGuestUsernamesAsString() {
		String result="";
		for(User u:guests) {
			result+=" "+u.getUsername();
		}
		return result;
	}

	public boolean addMessage(String msg){
		if(this.conversation.size() >= 400) this.conversation.removeFirst();
		this.conversation.add(msg);
		return true;
	}



}
