
//This class needs to be a bit more organized/broken down because it is getting too lengthy.
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.nio.channels.spi.*;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;
import java.io.File.*;
import java.lang.System.*;
import java.lang.Thread.*;
import java.util.concurrent.TimeUnit.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.net.InetAddress;

public class SelectServer {
    public static int BUFFERSIZE = 10000000;
    public static ServerData serverData = new ServerData();
    
    public static void main(String args[]) throws Exception 
    {
        if (args.length != 1)
        {
            System.out.println("Usage: TCPServer <Listening Port>");
            System.exit(1);
        }

        // Initialize buffers and coders for channel receive and send
        //String line = "";
		
		//Encoder and decoders between bytes<->chars
        //Charset charset = Charset.forName( "us-ascii" ); //For converting 16-bit unicode characters to bytes.
        //CharsetDecoder decoder = charset.newDecoder();  // Transform a sequence of bytes into a sequence of 16-bit Unicode characters.
        //CharsetEncoder encoder = charset.newEncoder(); // Transform 16-bit Unicode characters into a bytes.
		
        //ByteBuffer inBuffer = null; //Buffer to send data
        //CharBuffer charBuffer = null; //Buffer to convert inbuffer's bytes to chars
        //int bytesSent, bytesRecv;     // number of bytes sent or received - for error checking if msg was delivered to client
        
        // Initialize the selector
        Selector selector = Selector.open(); //Can examine one or more Java NIO Channel instances, 
		                                     // and determine which channels are ready for e.g. reading or writing.
											 // This way a single thread can manage multiple channels,
											 // and thus multiple network connections.
										
		
        /**                   TCP CHANNEL CREATION                    */
		
        // Create a server channel and make it non-blocking
        ServerSocketChannel tcp_channel = ServerSocketChannel.open(); //tcp_channel that can listen for incoming TCP connections, 
		                                                          // just like a ServerSocket in standard Java Networking.
        tcp_channel.configureBlocking(false); //Adjusts this channel's blocking mode.
       
        // Get the port number and bind the socket
        InetSocketAddress isa1 = new InetSocketAddress(Integer.parseInt(args[0])); //Socket Address with specific port and wildcard IP address.
        tcp_channel.socket().bind(isa1); //Assign port and IP wildcard address to socket.

        // Register that the server selector is interested in connection requests
        tcp_channel.register(selector, SelectionKey.OP_ACCEPT); 
		
        // Wait for something happen among all registered sockets
       try{
            boolean terminated = false;
            while (!terminated) 
            {
                if (selector.select(500) < 0) //500miliseconds
                {
                    System.out.println("select() failed");
                    System.exit(1);
                }
                
                // Get set of ready sockets
                Set readyKeys = selector.selectedKeys(); //selector's selected keys set
                Iterator readyItor = readyKeys.iterator(); 	

                // Walk through the ready set
                while (readyItor.hasNext()) 
                {    
                    // Get 1 key from set {key,key,key..}
                    SelectionKey key = (SelectionKey)readyItor.next(); 
                    // Remove current key entry from iteration because we saved that key in a variable "key"
                    readyItor.remove();

                    // Accept new connections, if any
                    if (key.isAcceptable()) //if socket is accepting connections & has some to accept
                    {
                        SocketChannel cchannel = ((ServerSocketChannel)key.channel()).accept();
                        cchannel.configureBlocking(false);
                        System.out.println("Accepted connection from " + cchannel.socket().getLocalAddress().toString().substring(1)+":"+ cchannel.socket().getPort());
                       
                        //Create User class instance for that person, add them to the system
                        User newUser = new User(true,false,false,cchannel.socket(),cchannel); //Basically once client opens their app, they are connected, and by default, anonymous.
                        serverData.addUser(newUser);
                        
                        serverData.printAllDataAsString();
                        
                        // Register the new connection for read operation
                        cchannel.register(selector, SelectionKey.OP_READ);
                      
                    } 
                    
                    if (key.isReadable()) 
                    {             
						SocketChannel cchannel  = (SocketChannel)key.channel();
							
							if (key.isReadable())
							{
								Socket socket = cchannel.socket();
								User user = serverData.getUserCorresponding(socket); //<- Identify user that we are serving now based on socket 
						
								String message = readMessageFromClient(cchannel,socket,key);
								if (message.trim().equals("")) {continue;} //nothing to read
								
								
								System.out.println("TCP Client "+ cchannel.socket().getLocalAddress().toString().substring(1)+":"+cchannel.socket().getPort() + " sent request: "+ message);
								
								// Now, decode this message and act appropriately
								
								ServerMsgDecoder messageDecoder = new ServerMsgDecoder(message);
								String keyword = messageDecoder.getKeyword();
								String[] msgArray = messageDecoder.getMsgArray();
								
								//Keyword is one of: {"login","logout","sign","join","host","search",
								//   "exit","quit","friends","befriend","unfriend","list",
								//   "kick","black","bremove","msg","part"}
								
								if(keyword.equals("host")){ //Host room, send reply message to client. DONE.
									hostRoom(cchannel,socket,messageDecoder.getMsgArray(),user);
								}
								
								if(keyword.equals("msg")){ 
									Broadcaster broadcaster = new Broadcaster(user.getInRoom(),msgArray[1],user);
									broadcaster.broadcastMsg();
								}
								
								
								if(keyword.equals("join")){ //join room with room code
									String roomCode = messageDecoder.getArg1();
									joinRoom(cchannel, socket, roomCode, user);
								}
								
								serverData.printAllDataAsString();
								
								continue;
							}	
                    }		             
                }      
            }
				
        // close all connections (terminated = true)
        Set keys = selector.keys();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) 
        {
            SelectionKey key = (SelectionKey)itr.next();
            //itr.remove();
            if (key.isAcceptable()){
                try{((ServerSocketChannel)key.channel()).socket().close();}catch(ClassCastException e){}
			}
            else if (key.isValid()){
				try{((SocketChannel)key.channel()).socket().close();}catch(ClassCastException e){}			
            }
        }
        
    }catch(IOException e) {System.out.println(e);}
 }  

	/**
	 * This method is for reading the message from the client's socket
	 * @param socketChannel
	 * @param socket
	 * @param key
	 * @return String - message from client. "" means no message was read
	 * @throws IOException
	 */
	public static String readMessageFromClient(SocketChannel socketChannel,Socket socket,SelectionKey key) throws IOException {
		
		// Open input and output streams (Receive bytes but send chars)
		ByteBuffer inBuffer = ByteBuffer.allocateDirect(BUFFERSIZE);
		CharBuffer charBuffer = CharBuffer.allocate(BUFFERSIZE); //Size the inBuffer to accomodate BUFFERSIZE bytes
		Charset charset = Charset.forName( "us-ascii" ); //For converting 16-bit unicode characters to bytes.
		CharsetDecoder decoder = charset.newDecoder();  // Transform a sequence of bytes into a sequence of 16-bit Unicode characters.
		
		// Read from socket - and if socket doen't exist - close connection with that client
		int bytesRecv=0;					
		try{ bytesRecv = socketChannel.read(inBuffer);
		} catch(Exception e) {
			System.out.println("Closed connection: " + socketChannel.socket().getLocalAddress().toString().substring(1)+":"+ socketChannel.socket().getPort());
			//TODO: Set user status to OFFLINE, if they had a room, DELETE room, if they had guests in room, SEND MSG to guests saying room was closed.
			key.cancel();
			socket.close();
			return "";
		}
		
		//Check if connection was closed
		if (bytesRecv <= 0)
		{
			System.out.println("read() error, or connection closed");
			key.cancel();  // deregister the socket
			return "";
		}
		
		inBuffer.flip();      // make buffer available 
		decoder.decode(inBuffer, charBuffer, false); //decode message from inbuffer -> outbuffer
		charBuffer.flip();
		String message = charBuffer.toString().trim();
		return message;
		}
	

	/**
	 * This method is for creating room and sending reply message to the client.
	 * @param socketChannel
	 * @param socket
	 * @param msgArray
	 * @param user
	 * @throws IOException 
	 */
	public static void hostRoom(SocketChannel socketChannel,Socket socket,String[] msgArray,User user) throws IOException {
		
		user.setUsername("anon0*"); //For now, admin is anonymous
		
		//1.Generate random room code
		RandomCode rndCode = new RandomCode(7);
		String roomCode = rndCode.getCode();
		 
		System.out.println("Room code generated is: " + roomCode);
		
		//2.Get room name that the user provided
		String roomName = msgArray[1];
		
		//3.Create that room 
		Room newRoom = new Room(roomCode,roomName,user,new ArrayList<User>(),new ArrayList<AuthorizedUser>());
		serverData.addRoom(newRoom);
		
		//4.Update the user - room and isAdmin.
		user.updateInRoom(newRoom);
		user.updateIsAdmin(true);
		
		//5.Send reply message to the client with the room info - status 1(corresponding to success)
		Boolean isSuccessful = sendMessage("host",new ArrayList<String>(Arrays.asList("1",roomCode,user.getUsername())),socketChannel);							
		if(!isSuccessful) {
			System.out.println("Count not send message to client because format is incorrect");
			return;
		}
	}

	public static void joinRoom(SocketChannel socketChannel, Socket socket, String roomCode, User user) throws IOException {

		//1.Check if a room with that code exists
		// If it doesnt, sent error msg to client.
		Boolean roomExists = serverData.roomExists(roomCode);
		
		if(!roomExists) {
			boolean isSuccessful = sendMessage("join",new ArrayList<String>(Arrays.asList("0","x","x","x","x","x")),socketChannel);							
			if(!isSuccessful) {
				System.out.println("Count not send message to client because format is incorrect");
				return;
			}		
			
			return;}
		
		//By this point, we know the room exists.
		
		Room room = serverData.getRoom(roomCode);
		
		//boolean isUserBannedFromRoom = room.isBanned(user); TODO check once we have authorized users.
		
		user.setUsername(room.generateAnonUsername());
		
		room.addGuest(user);
		
		user.updateInRoom(room);
		user.updateIsAdmin(false);
		
		//“join <status> <roomName> <adminName> <users> <messages> <givenUsername>”
		boolean isSuccessful = sendMessage("join",
				new ArrayList<String>(Arrays.asList("2",room.getRoomName(),room.getAdmin().getUsername()
						,room.getGuestUsernamesAsString(),"blablabla",user.getUsername())),socketChannel);	
		
		//TODO : notify everyone users who just joined a room, by sending msg to one above in exact same way

	}
	
	/**
	 * Use this ONLY after check syntax, to ensure you're sending properly formatted message
	 * @param message
	 * @param socket
	 * @param socketChannel
	 * @throws IOException 
	 */
	public static boolean sendMessage(String keyword, ArrayList<String> rest,SocketChannel socketChannel) throws IOException {
		
		ServerMessage msgToClient = new ServerMessage(keyword,rest);
		if(msgToClient.isGoodToSend() == true) {
			String sendThisMsg = msgToClient.getMessageToClient();
			
			System.out.println("Sending this msg to client: ");
			System.out.println(sendThisMsg);
			
			ByteBuffer inBuffer = ByteBuffer.wrap(sendThisMsg.getBytes());
			int bytesWritten = socketChannel.write(inBuffer); //probably don't need to store this is a variable..
			inBuffer.rewind();	//Probably don't need to rewind the buffer..
			return true;
		}
		
		
		return false;
	}
}

	


