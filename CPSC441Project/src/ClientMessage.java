
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * This class will format the outgoing messages from client (to the server)
 * This class will NOT send the message. It will only make sure the message is in correct format to send to server

	User wants to log in: "login <username> <password>"
	User wants to log out of account: "logout"
	User wants to sign up: "sign <email> <username> <password>"
	User wants to join a room: "join <roomcode>"	User wants to host a room: "host  <roomname>"
	User searches for a room: "search <roomname>"
	User wants to exit the room: "exit" 
	User wants to exit the application: "quit"
	Authorized User wants to list their friends: "friends"
	Authorized User wants to be-friend someone: "befriend <username>"
	Authorized User wants to unfriend someone: "unfriend <username>"
	User wants to list people in the room: "list"
	Admin wants to blacklist someone: "kick <username>"
	Admin wants to list blacklisted users: "black <username>"
	Admin wants to unkick someone: "bremove <username>"
	User wants to send regular message to the room: "msg <content>"
	User wants to retrieve only the last messages that it doesn't have: "part lastMessageAsString" (Each client keeps track of last message that was sent to them as string and uses this information to request the missing part of a linked list of messages from the server)

 */
public class ClientMessage {

	private String[] keywords = new String[] {"login","logout","sign","join","host","search","exit","quit","friends","befriend","unfriend","list",
			"kick","black","bremove","msg","part"};
	
	private String keyword = "";
	private ArrayList<String> rawMessage;
	private String msgToServer;
	private Boolean isGoodToSend;
	
	/**
	 * Constructor
	 * @param keywd - selected from the list of keywords
	 * @param message - other parts of the message in an array
	 */
	public ClientMessage(String keywd, ArrayList<String> message) {
		
		keyword = keywd;
		rawMessage = message;
		Boolean isSyntaxCorrect = isSyntaxCorrect(keyword,rawMessage);
		if(isSyntaxCorrect) {
			isGoodToSend = true;
			//Convert arraylist of messages to String separated by "\n"s
			String listString = String.join("\n", rawMessage);
			msgToServer = keywd + "\n" + listString + "\n";
			
			}
		else {isGoodToSend=false;}
	
	}
	
	/**
	 * Checks if format of message sent to server is valid.
	 * @param keywd
	 * @param message
	 * @return boolean
	 */
	public boolean isSyntaxCorrect(String keywd, ArrayList<String> message) {
		
	    boolean keywordCorrect = false;
		for(String s:keywords) {
			if(keywd == s) { 
				keywordCorrect = true;
				break;}
		}
		if(keywordCorrect = false) {keyword = "";return false;}
		
		//By this point, we know keyword is correct.
		// Now, we check if actual message corresponding to keyword is correct
		
		if(keyword.equals(keywords[0])){}
		if(keyword.equals(keywords[1])){}
		if(keyword.equals(keywords[2])){}
		if(keyword.equals(keywords[3])){
			//3- join room
			//Check if there's only one field for room name
			if (message.size()==1 || message.size()==2) {return true;}
			return false;
		}
		//4 - host room
		if(keyword.equals(keywords[4])){
			//Check if there's only one field for room name
			if (message.size()==1 || message.size()==2) { return true;}
			return false;
		}
		if(keyword.equals(keywords[5])){}
		if(keyword.equals(keywords[6])){}
		if(keyword.equals(keywords[7])){}
		if(keyword.equals(keywords[8])){}
		if(keyword.equals(keywords[9])){}
		if(keyword.equals(keywords[10])){}
		if(keyword.equals(keywords[11])){}
		if(keyword.equals(keywords[12])){}
		if(keyword.equals(keywords[13])){}
		if(keyword.equals(keywords[14])){}
		if(keyword.equals(keywords[15])){
			if(keyword.equals(keywords[15])){
				//15 - send msg to chat room
				//Check if there's only one field for chat message
				if (message.size()==1) {return true;}
				return false;
			}
		}
		if(keyword.equals(keywords[16])){}
		return false;
	}

	public boolean isGoodToSend() {
		return isGoodToSend;
	}
	
	
	public String getMessageToServer() {
		return msgToServer;
	}
}
