import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *  Responsible for constantly asking for user input to send message to the chat and sending it to server
 */
public class Thread1 extends Thread{
	private DataOutputStream outbuffer;
	
	public Thread1(DataOutputStream outbuf) {
		outbuffer = outbuf;
	}
	
	public void run(){  
		System.out.println("Thread1 is running - Reading input for chatroom...");
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
    	
		boolean successSending;
		boolean loopCondition = true;
		while (loopCondition) {
			
			String userLine = "";
			try {userLine = inFromUser.readLine();} catch (IOException e) {}
			System.out.println(TCPClient.currentUsername + ":" + userLine);
			ArrayList<String> message = new ArrayList<String>(); 
			message.add(userLine);
			successSending = TCPClient.sendMessageToServer(outbuffer, "msg", message);
			if(!successSending){System.out.println("ERROR: Message you were trying to send to server has invalid syntax.");System.exit(0);}
	    	
		} 
	}
}
