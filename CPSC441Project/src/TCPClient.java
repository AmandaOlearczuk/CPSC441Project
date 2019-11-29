

/*
 * A simple TCP client that sends messages to a server and display the message
   from the server. 
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 */


import java.io.*; 
import java.net.*;
import java.util.ArrayList;

class TCPClient { 
	
	public static String currentUsername;

    public static void main(String args[]) throws Exception 
    { 
        if (args.length != 2)
        {
            System.out.println("Usage: TCPClient <Server IP> <Server Port>");
            System.exit(1);
        }

        // Initialize a client socket connection to the server
        Socket clientSocket = new Socket();
        try {
        	clientSocket = new Socket(args[0], Integer.parseInt(args[1])); 
        }catch (ConnectException e) {
        	System.out.println("Server is down, please try again later.");
        	System.exit(0);
        }

        // Initialize input and an output streams to read and write to and from SOCKET
        DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream()); 
        BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

        // Initialize user input stream to read from USER
        String line; 
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

        System.out.println("Welcome! Enter a corresponding number for a menu option.");
		boolean loop = true;
        while(loop) {
        	//1.Display main menu options
        	System.out.println("[1. Log in] [2. Sign up] [3. Join room] [4. Host room] [5. Search for room] [6. Exit]");
        	
        	//2.Read input from user
        	line = inFromUser.readLine(); 
        	MainMenuSyntaxChecker syntaxChecker = new MainMenuSyntaxChecker();
        	
        	//3.Check if input is valid
        	if(!syntaxChecker.isMainMenuOption(line)) {System.out.println("Error: Enter a valid menu option");continue;}
        	
        	//4.See what option did user select and execute appropriate action
            if(syntaxChecker.isMainMenuLogIn(line)) {}
        	if(syntaxChecker.isMainMenuSignUp(line)) {}
        	if(syntaxChecker.isMainMenuJoinRoom(line)) { joinRoom(outBuffer, inBuffer);}
        	if(syntaxChecker.isMainMenuHostRoom(line)) { hostRoom(outBuffer,inBuffer);}
        	if(syntaxChecker.isMainMenuSearchForRoom(line)) {}
        	if(syntaxChecker.isMainMenuExit(line)) {System.out.println("Goodbye!");clientSocket.close();System.exit(0);}
        	
        }
          
    } 
    
    /**
     * This function is responsible for sending message to the server that client wants to join a room.
     * 
     */

    public static void joinRoom(DataOutputStream outbuffer, BufferedReader inBuffer) throws IOException, InterruptedException {
	
    	Boolean successSending;
    	
    	while(true) {
		
    		//1.Promt user to enter the room code
    		System.out.println("Enter a room code: ");
    		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    		String roomCode = inFromUser.readLine();
    		if (roomCode.trim().equals("")) {System.out.println("Invalide room code, please try again.");continue;}
		
    		ArrayList<String> msgsInArray = new ArrayList<String>();
    		msgsInArray.add(roomCode);
		
    		//2.Send join information to the server 'join <joincode>'
    		successSending = sendMessageToServer(outbuffer,"join",msgsInArray);
    		if(!successSending){System.out.println("ERROR: Message you were trying to send to server has invalid syntax.");System.exit(0);}

    		//3.Receive message from the server with the room information
    		//System.out.println("Fetching response from server..");
    		String line = fetchMessageFromServer(inBuffer);
    		//System.out.println("Response from server fetched: ");
    		//System.out.println(line);
        
    		//4. Server sends join room confirmation/decline: 
    		//  'join <status> <roomName> <adminName> <users> <messages> <username>'
    		// Note: 2 is the error status code for join.
    		String errorStatus = "2";
    		String[] msgsArray = line.split("\n");
    		if(!msgsArray[1].equals(errorStatus)) {System.out.println("Couldn't connect to that room.");break;} 
    		currentUsername = msgsArray[6];
    		System.out.println("Welcome " + msgsArray[6] + "! Room: " + roomCode + " '"+msgsArray[2]+"'"+ " Admin: "+ msgsArray[3]);
    		System.out.println("Currently in room: "+ msgsArray[3] + msgsArray[4]);
    		System.out.println("Type '/o' for options.");
		int size = msgsArray.length;
		for(int i = 7; i < size; i++){
			System.out.println(msgsArray[i]);
		}
    		//5. Start up threads for reading user upput & fetching msgs from a server.
    		Thread1 t1= new Thread1(outbuffer); //This thread is asking for user input to send message to the chat & server
    		Thread2 t2= new Thread2(inBuffer); //This thread is constantly fetching new messages to the chatroom
		
    		t1.start();
    		t2.start();
		
    		t1.join();
    		t2.join();
		
    		break;
        }
}


    public static void hostRoom(DataOutputStream outbuffer,BufferedReader inBuffer) throws IOException, InterruptedException {
    	
    	Boolean successSending;
    	
    	while(true) { //loop just in case room name was entered as empty.
    		
    	//1.Prompt user to enter a room name
    	System.out.println("Enter a room name: ");
    	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
    	String roomName = inFromUser.readLine();
    	if (roomName.trim().equals("")) {System.out.println("Invalid room name, please try again.");continue;} //go to top of loop if roomname was empty
    	ArrayList<String> msgsInArray = new ArrayList<String>();
    	msgsInArray.add(roomName);
    	
    	//2.Send room information to the server 'host  <roomname>'
    	successSending = sendMessageToServer(outbuffer,"host",msgsInArray);
    	
    	if(!successSending){System.out.println("ERROR: Message you were trying to send to server has invalid syntax.");System.exit(0);}
    		
        //3.Receive message from the server with the room information
    	//System.out.println("Fetching response from server..");
    	String line = fetchMessageFromServer(inBuffer);
    	//System.out.println("Response from server fetched: ");
    	String[] msgsArray = line.split("\n");
    	currentUsername = msgsArray[3];
		System.out.println("Welcome " + msgsArray[3] + "! Room: " + msgsArray[2] + " '"+roomName+"'"+ " Admin: "+ msgsArray[3]);
		System.out.println("Type '/o' for options.");
		
		//3. Continuously ask the user for input, as well as fetch incoming messages.
		//   We need 2 threads.
		// For now, just implement the chat functionality and NO menu options - '/o' command doesn't work
		
		Thread1 t1= new Thread1(outbuffer);
		Thread2 t2= new Thread2(inBuffer);
		
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();
		
		
    	break;
    	
    	}
    }
    
    /**
     * This is a method to fetch message from a server - BLOCKS ON .readLine(). Basically waits for something in buffer and THEN proceeds.
     * @param inBuffer
     * @return
     * @throws IOException
     */
    public static String fetchMessageFromServer(BufferedReader inBuffer) throws IOException {

    	String buff_capacity = inBuffer.readLine();
    	int msg_length = Integer.parseInt(buff_capacity);
    	
		char[] msgReceived = new char[msg_length];
		inBuffer.read(msgReceived,0,msg_length);
		String line = new String(msgReceived).trim();
		
		return line;
    }
    
    /**
     * This is a method to send message to a server.
     * @param outBuffer
     * @param keyword
     * @param theRest
     * @return boolean - if sending was successful
     */
    public static boolean sendMessageToServer(DataOutputStream outBuffer,String keyword,ArrayList<String> theRest) {
    	ClientMessage message = new ClientMessage(keyword,theRest);
    	
    	if(message.isGoodToSend()) {
    		//System.out.println("Sending the followng message to server: ");
    		//System.out.println(message.getMessageToServer());
    		try{outBuffer.writeBytes(message.getMessageToServer());return true;}catch(Exception e){System.out.println("socket exception");return false;}
    	}
    	
		return false;
    }
} 
