import java.io.BufferedReader;
import java.io.IOException;

/**
 * Thread responsible for continuously fetching new messages from the room
 */
public class Thread2 extends Thread {
	
	private BufferedReader inbuffer; //This is a socket of a client
	
	public Thread2(BufferedReader inbuf) {
		inbuffer = inbuf;
	}
	
	public void run(){  
		
		System.out.println("Thread2 is running - fetching room messages from server...");  
		
		try {
			while(true) {
				String line = TCPClient.fetchMessageFromServer(inbuffer);
				String[] msgsArray = line.split("\n");
				System.out.println(msgsArray[0] + " Response from server fetched: ");
				System.out.println(msgsArray[1]+ ":" + msgsArray[2]);
			}
		
		} catch (IOException e) {e.printStackTrace();}
		
	}  
}
