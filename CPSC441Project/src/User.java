
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class User {
	private boolean isOnline;
	private boolean isAdmin;
	private boolean isGuest;
	private Room inRoom;
	Socket socket;
	SocketChannel socketChannel;
	
	/**
	 * Constructor 1
	 * @param isadmin
	 * @param isguest
	 * @param inroom
	 * @param skt
	 * @param sktChannel
	 */
	public User(boolean isonline,boolean isadmin,boolean isguest,Socket skt,SocketChannel sktChannel) {
		isOnline = isonline;
		isAdmin = isadmin;
		isGuest = isguest;
		socket = skt;
		socketChannel = sktChannel;
	}
	
	/**
	 * Constructor 2
	 * @param isadmin
	 * @param isguest
	 * @param inroom
	 * @param skt
	 * @param sktChannel
	 */
	public User(boolean isonline,boolean isadmin,boolean isguest,Room inroom,Socket skt,SocketChannel sktChannel) {
		isOnline = isonline;
		isAdmin = isadmin;
		isGuest = isguest;
		inRoom = inroom;
		socket = skt;
		socketChannel = sktChannel;
	}
	
	public void updateInRoom(Room room) {
		inRoom = room;
	}
	
	public void updateIsAdmin(Boolean bool) {
		isAdmin = bool;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}
	
}
