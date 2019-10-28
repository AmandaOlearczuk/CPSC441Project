import java.net.Socket;
import java.nio.channels.SocketChannel;

public class User {
	private boolean isAdmin;
	private boolean isGuest;
	private Room inRoom;
	Socket socket;
	SocketChannel socketChannel;
	
	/**
	 * Constructor
	 * @param isadmin
	 * @param isguest
	 * @param inroom
	 * @param skt
	 * @param sktChannel
	 */
	public User(boolean isadmin,boolean isguest,Room inroom,Socket skt,SocketChannel sktChannel) {
		isAdmin = isadmin;
		isGuest = isguest;
		inRoom = inroom;
		socket = skt;
		socketChannel = sktChannel;
	}
	
}
