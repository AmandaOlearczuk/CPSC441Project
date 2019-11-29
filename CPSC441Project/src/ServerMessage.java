import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.*;
/**
 * This class is used to structure a message that will be then sent to the client.
 *  The server will reply to all commands with the command name followed by integer status codes, 
 *  as well as any other relevant data that the user has requested. The server response message formats mirror the clients', 
 *  and are as follows:

	Server sends login confirmation/decline: "login <status>"
	Server sends logout confirmation status: "logout <status>"
	Server sends sign up confirmation/decline: "sign <status>"
	Server sends join room confirmation/decline: "join <status> <roomName> <adminName> <users> <messages>"
	Server sends host room information: "host <status> <roomcode>"
	Server sends found rooms: "search <status> <roomRecords>"
	Server sends exit room confirmation: "exit <status>"
	Server sends user's friends list over: "friends <status> <friendlist>"
	Server sends befriend confirmation/decline: "befriend <status>"
	Server sends unfriend confirmation/decline: "unfriend <status>"
	Server sends a list of people in the room: "list <status> <users>"
	Server sends confirmation/decline of kick: "kick <status>"
	Server sends a list of blacklisted users in a room: "black <status> <users>"
	Server sends confirmation/decline of un-kicking someone: "bremove <status>"
	Server sends updated message list: "msg <allMessages>"
	Server sends a part of linked list of msgs to the client: "part  <status> <partOfLinkedListOfMsgs>"
	
	
 */

public class ServerMessage {
	private String[] keywords = new String[] {"login","logout","sign","join","host","search","exit","quit","friends","befriend","unfriend","list",
			"kick","black","bremove","msg","part","last"};
	private Map<String, String[] > statusCodes = new HashMap<String, String[]>();	
	
	private String keyword = "";
	private ArrayList<String> rawMessage;
	private String msgToClient;
	private Boolean isGoodToSend;
	
	public ServerMessage(String keywd,ArrayList<String> rawmessage) {
		
		statusCodes.put("login", new String[] {"0","1"});
		statusCodes.put("logout", new String[] {"0","1"});
		statusCodes.put("sign", new String[] {"0","1","2","3"});
		statusCodes.put("join", new String[] {"0","1","2"});
		statusCodes.put("host", new String[] {"0","1"});
		statusCodes.put("search", new String[] {"0","1","2"});
		statusCodes.put("exit", new String[] {"0","1"});
		statusCodes.put("quit", new String[] {});
		statusCodes.put("friends", new String[] {"0","1"});
		statusCodes.put("befriend", new String[] {"0","1"});
		statusCodes.put("unfriend", new String[] {"0","1"});
		statusCodes.put("list", new String[] {"0","1"});
		statusCodes.put("kick", new String[] {"0","1","2","3"});
		statusCodes.put("black", new String[] {"0","1","2"});
		statusCodes.put("bremove", new String[] {"0","1","2"});
		statusCodes.put("part", new String[] {"0","1"});
		//No need for "msg" keyword status - always good.

		keyword = keywd;
		rawMessage = rawmessage;
		System.out.println("Raw message: " + rawmessage);
		Boolean isSyntaxCorrect = isSyntaxCorrect(keyword,rawMessage);
		if(isSyntaxCorrect) {
			System.out.println("Syntax is correct of the message you're trying to send from server to client..");
			isGoodToSend = true;
			//Convert arraylist of messages to String separated by "\n"s
			String listString = String.join("\n", rawMessage);
			msgToClient = keywd + "\n" + listString + "\n";
			msgToClient = includeCapacity(msgToClient); //Message is pre-fixed with it's size.
			
		} else {
			System.out.println("Syntax is INCORRECT of the message you're trying to send from server to client..");
			isGoodToSend=false;}
	}
	
	/**
	 * Checks if format of message sent to server is valid.
	 * @param keywd
	 * @param message
	 * @return boolean
	 */
	public boolean isSyntaxCorrect(String keywd, ArrayList<String> message) {
		
		keyword = keywd;
		rawMessage = message;
		
	    boolean keywordCorrect = false;
		for(String s:keywords) {
			if(keyword == s) { 
				keywordCorrect = true;
				System.out.println("Keyword is correct in message you're trying to send..");
				break;}
		}
		if(keywordCorrect = false) {keyword = "";return false;}
		
		//By this point, we know keyword is correct.
		
		//We check if there's a status needed for that message, and if so, check if status code is correct 
		if(statusCodes.containsKey(keyword)) {
			System.out.println("Status Code is: " + message.get(0));
			boolean statusCorrect = isStatusCodeCorrect(keyword,message.get(0));
			if(statusCorrect == false) {System.out.println("Status is INCORRECT in message you're trying to send..");return false;}
		}
		
		System.out.println("Status is correct in message you're trying to send..");
		
		// Now, we check if actual message corresponding to keyword is correct
		if(keyword.equals(keywords[0])){}
		if(keyword.equals(keywords[1])){}
		if(keyword.equals(keywords[2])){}
		if(keyword.equals(keywords[3])){
			//3.Join room
			System.out.println("You selected a join room keyword in message to send..");
			//Check if the number of fields in message (not including keyword) is appropriate
			if (rawMessage.size()>=6) {
				System.out.println("Number of fields is correct in a message..");
				//Here, we already know status is correct and message has 3 fields. All good!
				return true;
			}System.out.println("Number of fields is INCORRECT in a message..");return false; //here, we know msg size is not 2 - wrong # of fields - syntax incorrect
		}
		
		//4 - host room
		if(keyword.equals(keywords[4])){
			System.out.println("You selected a host room keyword in message to send..");
			//Check if the number of fields in message (not including keyword) is appropriate
			if (rawMessage.size()==3) {
				System.out.println("Number of fields is correct in a message..");
				//Here, we already know status is correct and message has 2 fields. All good!
				return true;
			}System.out.println("Number of fields is INCORRECT in a message..");return false; //here, we know msg size is not 2 - wrong # of fields - syntax incorrect
		}
		
		if(keyword.equals(keywords[5])){
			return true;
		}
		if(keyword.equals(keywords[6])){}
		if(keyword.equals(keywords[7])){}
		if(keyword.equals(keywords[8])){}
		if(keyword.equals(keywords[9])){}
		if(keyword.equals(keywords[10])){}
		if(keyword.equals(keywords[11])){}
		if(keyword.equals(keywords[12])){}
		if(keyword.equals(keywords[13])){}
		if(keyword.equals(keywords[14])){}
		if(keyword.equals(keywords[15])){}
		if(keyword.equals(keywords[16])){}
		if(keyword.equals(keywords[17])){
			//17 - 'last' message
			System.out.println("You selected a 'last' keyword in message to send..");
			if (rawMessage.size()==2) {
				System.out.println("Number of fields is correct in a message..");
				//Here, we already know status is correct and message has 2 fields. All good!
				return true;
			}
			System.out.println("Number of fields is INCORRECT in a message..");return false; //here, we know msg size is not 2 - wrong # of fields - syntax incorrect
		}
		return false;
	}

	public boolean isGoodToSend() {
		return isGoodToSend;
	}
	
	/**
	 * Check if status number is correct for that keyword
	 * @param keywd
	 * @param status
	 * @return boolean
	 */
	public boolean isStatusCodeCorrect(String keywd, String status) {

		String[] keywordStatuses = statusCodes.get(keywd); //Get statuses for that keyword
		for (String status1 : keywordStatuses) { //Check if status matches the array of statuses for that keyword.
			System.out.println("stored status: " + status1 + "my Status: " + status); //remove later
			if(status.equals(status1)) {return true;}
		}
           return false;
	}
	
	public String getMessageToClient() {
		return msgToClient;
	}
	
	public String includeCapacity(String s){
		String str = "\n" + s;
		ByteBuffer bb = ByteBuffer.wrap(str.getBytes());
		str = Integer.toString(bb.capacity()) + str; //Format : "SIZEOFMSG\nFile1\nFile2\nFile3...."
		return str;
	}
}
