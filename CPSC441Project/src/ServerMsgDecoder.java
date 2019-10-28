
/**
 * This class is for server to decode the message sent from client
 *
 */
public class ServerMsgDecoder {
	private String message;
	private String[] messageAsArray;
	
	/**
	 * Constructor
	 * @param msg
	 */
	public ServerMsgDecoder(String msg){
		message = msg;
		String[] arrayMsg = msg.split("\n");
		for(String s:arrayMsg) {s.trim();}
		messageAsArray = arrayMsg;
	}
	
	public String getKeyword() {
		return messageAsArray[0];
	}
	
	public String[] getMsgArray() {
		return messageAsArray;
	}
}
