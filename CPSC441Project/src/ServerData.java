import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * This class deals with all data that is relevant to the server - rooms, all users (online or offline)
 *
 */
public class ServerData {
	ArrayList<Room> liveRooms ;
	ArrayList<User> usersInSystem; //Can be online or offline. Offline users are stored in file somewhere on a server
	
	public ServerData() {
		liveRooms=new ArrayList<Room>();
		usersInSystem=new ArrayList<User>();
	}
	
	/**
	 * This method adds user to usersInSystem array.
	 * It does NOT check if that user is in array already.
	 * @param user
	 */
	public void addUser(User user) {
		System.out.println("Adding User: " + user.getSocket().getLocalAddress().toString().substring(1) +" : " + user.getSocket().getPort() +" to the system.");
		usersInSystem.add(user);
	}
	
	public void addRoom(Room room) {
		liveRooms.add(room);
	}
	
	/**
	 * User can be uniquely identified by socket
	 * @param socket
	 * @return User
	 */
	public User getUserCorresponding(Socket socket) {
		for (User user:usersInSystem) {
			if(user.getSocket().getLocalAddress().toString().equals(socket.getLocalAddress().toString())
					&& user.getSocket().getPort() == socket.getPort()) {
					return user;
			}
		}
		return null;
	}
	
	public void printAllDataAsString() {
		System.out.println(">>> Printing data from ServerData.java class .. <<<");
		System.out.println(">>> Live rooms: <<<");
		for(Room room:liveRooms) {
			System.out.println("Room: " + room + ",Room name: " + room.getRoomName() + ",Room code: "+ room.getRoomCode() +",Admin is: "+ room.getAdmin() + ",Guests: " +
					room.getGuests() + ",Blacklist: " + room.getBlacklist());
		}
		
		System.out.println(">>> Total users in system: <<<");
		for(User user:usersInSystem) {
			System.out.println("User: " + user + ", Online status: " + user.isOnline() + ",In room: "+user.getInRoom());	
		}
	}
}
