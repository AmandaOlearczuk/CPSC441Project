import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * This class will format the outgoing messages from client (to the server)
 * This class will NOT send the message. It will only make sure the message is in correct format to send to server
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
			msgToServer = includeCapacity(msgToServer);
			
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
		if(keyword.equals(keywords[3])){}
		
		//4 - host room
		if(keyword.equals(keywords[4])){
			//Check if there's only one field for room name
			if (message.size()==1) { return true;}
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
		if(keyword.equals(keywords[15])){}
		if(keyword.equals(keywords[16])){}
		return false;
	}

	public boolean isGoodToSend() {
		return isGoodToSend;
	}
	
	/** The following method takes a string and appends it's own capacity in front plus a "\n" symbol for separation.
	* For example: Input: "HelloThere" Output: "10\nHelloThere"
	* What's it used for? For sending messages to the server, so the server knows the size of message to read. 
	* Returns: String
	*/
	public String includeCapacity(String s){
		String str = "\n" + s;
		ByteBuffer bb = ByteBuffer.wrap(str.getBytes());
		str = Integer.toString(bb.capacity()) + str; //Format : "SIZEOFMSG\nFile1\nFile2\nFile3...."
		return str;
	}
	
	public String getMessageToServer() {
		return msgToServer;
	}
}
