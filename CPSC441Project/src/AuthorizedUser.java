import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class AuthorizedUser extends User {
	private String email;
	private String username;
	private String password;
	private ArrayList<AuthorizedUser> friends;
	
	/**
	 * Parent Constructor Call
	 * @param isadmin
	 * @param isguest
	 * @param inroom
	 * @param skt
	 * @param sktChannel
	 */
	public AuthorizedUser(boolean isonline, boolean isadmin, boolean isguest, Room inroom, Socket skt, 
			SocketChannel sktChannel) {
		super(isonline, isadmin, isguest, inroom, skt, sktChannel);
	}
	
	/**
	 * Constructor: Upgrade User to Auth-User
	 * @return
	 */
//	public AuthorizedUser(User toUpgrade) {
//		super(isOnline, isAdmin, isGuest, inRoom, socket, socketChannel);
//	} //TODO
	
	public String getEmail() {
		return email;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public ArrayList<AuthorizedUser> getFriends(){
		return friends;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void username(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setFriendsList(ArrayList<AuthorizedUser> friends) {
		this.friends = friends;
	}
	
	public void addFriend(AuthorizedUser toAdd) {
		friends.add(toAdd);
	}
	
	public void removeFriend(AuthorizedUser toRemove) {
		friends.remove(toRemove);
	}
}
