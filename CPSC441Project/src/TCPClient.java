
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
        	if(syntaxChecker.isMainMenuJoinRoom(line)) {}
        	if(syntaxChecker.isMainMenuHostRoom(line)) { ArrayList<String> roomInfo = hostRoom(outBuffer);}
        	if(syntaxChecker.isMainMenuSearchForRoom(line)) {}
        	if(syntaxChecker.isMainMenuExit(line)) {System.out.println("Goodbye!");clientSocket.close();System.exit(0);}
        	
        }
        
        /* 
		//Get user input and send to the server
		//FORMAT OF MESSSAGES BEING RECEIVED: First line is the size of the message following. For example: "453\nFile1\nFile2\nhello.java\n"
		
        System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
        line = inFromUser.readLine(); 
        while (!line.equals("logout") )
        {
			try{
                // Send to the server
				outBuffer.writeBytes(line + '\n'); 
            
				String incomingPort = inBuffer.readLine(); //First line reads a port #
				String isFile = inBuffer.readLine(); //Second line reads if the incoming msg is to be saved to file.
				String buff_capacity = inBuffer.readLine(); //Third line reads capacity (size) of the actual content in following message
				
				if(isFile.equals("1")){
					String filename = line.substring(4,line.length());
					
					//Save that content to a file
					PrintWriter out = new PrintWriter(filename);		
					
					int msg_length = Integer.parseInt(buff_capacity);
	
					char[] msgReceived = new char[msg_length];
					inBuffer.read(msgReceived,0,msg_length);
					line = new String(msgReceived);
					line = line.substring(0,line.length()-1); //For some reason line always includes 1 extra whitespace at the end
					
					out.println(line);
					out.close();
					
					String msg = "File saved in " + filename + "-" + incomingPort + " (" + (new File(filename).length()) + " bytes)";
					System.out.println(msg);
					
				}else{
				
					int msg_length = Integer.parseInt(buff_capacity);
	
					char[] msgReceived = new char[msg_length];
					inBuffer.read(msgReceived,0,msg_length);
					line = new String(msgReceived).trim();
					System.out.println(line);
				}
				
			}catch(SocketException e){}
			
				System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
				line = inFromUser.readLine(); 
        }


        // Close the socket
        clientSocket.close();    */       
    } 
    
    /**
     * This function is responsible for sending message to the server that client wants to host a room.
     * @return ArrayList<String> - response message from the server.
     */
    public static ArrayList<String> hostRoom(DataOutputStream outbuffer) throws IOException {
    	
    	//1.Prompt user to enter a room name
    	System.out.println("Enter a room name: ");
    	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
    	String roomName = inFromUser.readLine();
    	ArrayList<String> msgsInArray = new ArrayList<String>();
    	msgsInArray.add(roomName);
    	
    	//2.Send room information to the server “host  <roomname>”
    	ClientMessage message = new ClientMessage("host",msgsInArray);
    	if(message.isGoodToSend()) {
    		System.out.println("Sending the followng message to server: ");
    		System.out.println(message.getMessageToServer());
    		try{outbuffer.writeBytes(message.getMessageToServer());}catch(Exception e){System.out.println("socket exception");}
    	}
    		
        //3.Receive message from the server with the room information
    	//TODO
    	
    	ArrayList<String> al = new ArrayList<String>();
    	return al;
    }
} 
