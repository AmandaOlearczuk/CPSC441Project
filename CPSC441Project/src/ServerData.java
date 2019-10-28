import java.util.ArrayList;

/**
 * This class deals with all data that is relevant to the server - rooms, all users (online or offline)
 *
 */
public class ServerData {
	ArrayList<Room> liveRooms;
	ArrayList<User> usersInSystem; //Can be online or offline. Offline users are stored in file somewhere on a server
	
	public ServerData() {
		liveRooms=null;
		usersInSystem=null;
	}
	
	/**
	 * This method adds user to usersInSystem array.
	 * It does NOT check if that user is in array already.
	 * @param user
	 */
	public void addUser(User user) {
		usersInSystem.add(user);
	}
	
	public void addRoom(Room room) {
		liveRooms.add(room);
	}
}
