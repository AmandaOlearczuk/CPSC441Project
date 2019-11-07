import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
/**
 * The following class broadcasts a message to the room except the user who actually sent a message (to avoid duplicate message on
 *   user's screen)
 * @author amand
 *
 */
public class Broadcaster {
	private User userWhoSends;
	private ArrayList<User> allGuestsInRoom;
	private Room room;
	private String message;
	
	public Broadcaster(Room r,String msg,User excludedUser) {
		userWhoSends = excludedUser;
		room = r;
		message=msg;
		allGuestsInRoom = room.getGuests(); //All guests
	}
	
	/**
	 * This class broadcasts message to all users in the room, as well as adds the message to linked list of messages for that room.
	 * @throws IOException 
	 */
	public void broadcastMsg() throws IOException {
		ArrayList<String> messageArray = new ArrayList<String>();
		messageArray.add(userWhoSends.getUsername());
		messageArray.add(message);

			for(User user : allGuestsInRoom) {
				System.out.println("Sending msg to users:"+user.getUsername());
				boolean isSuccessful = SelectServer.sendMessage("last",messageArray,user.getSocketChannel());	
				if(!isSuccessful){System.out.println("Couldnt not send message to client because format is incorrect");}
			}
			//Plus the admin.
			boolean isSuccessful = SelectServer.sendMessage("last",messageArray,room.getAdmin().getSocketChannel());	
			if(!isSuccessful){System.out.println("Couldnt not send message to client because format is incorrect");}
		
	}
}
