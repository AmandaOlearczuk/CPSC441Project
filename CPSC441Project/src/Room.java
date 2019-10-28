
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
}
