/*
 * A simple TCP client that sends messages to a server and display the message
   from the server. 
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 */

import java.io.*; 
import java.net.*; 

class TCPClient { 

    public static void main(String args[]) throws Exception 
    { 
        if (args.length != 2)
        {
            System.out.println("Usage: TCPClient <Server IP> <Server Port>");
            System.exit(1);
        }

        // Initialize a client socket connection to the server
        Socket clientSocket = new Socket(args[0], Integer.parseInt(args[1])); 

        // Initialize input and an output stream for the connection(s)
        DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream()); 
        BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

        // Initialize user input stream
        String line; 
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

        /**
		Get user input and send to the server
		FORMAT OF MESSSAGES BEING RECEIVED: First line is the size of the message following. For example: "453\nFile1\nFile2\nhello.java\n"
		*/
		
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
        clientSocket.close();           
    } 
} 
