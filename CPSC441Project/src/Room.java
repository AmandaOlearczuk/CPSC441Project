
import java.util.ArrayList;

public class Room {
	private String roomCode;
	private String roomName;
	private User admin;
	private ArrayList<User> guests ;
	private ArrayList<AuthorizedUser> blacklist;
	
	public Room(String roomcode,String roomname,User adminUser,ArrayList<User> roomGuests,ArrayList<AuthorizedUser> blacklst) {
		roomCode = roomcode;
		roomName = roomname;
		admin = adminUser;
		guests = roomGuests;
		blacklist = blacklst;
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
	
	
}
