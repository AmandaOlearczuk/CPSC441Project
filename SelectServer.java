/**
*  Amanda Olearczuk
*  TCP/UDP accepting server
*  UDPServer is an echo server
*  TCPClient can ask TCPServer for list of files in current directory, and download those files.
*
* Works perfectly: TCPClient - Server interaction.
* Doesn't work: Server is UDP-blocking, and once a UDPClient logs out, it won't accept another UDPClient.
*/

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
    public static void main(String args[]) throws Exception 
    {
        if (args.length != 1)
        {
            System.out.println("Usage: TCPServer <Listening Port>");
            System.exit(1);
        }

        // Initialize buffers and coders for channel receive and send
        String line = "";
		
		//Encoder and decoders between bytes<->chars
        Charset charset = Charset.forName( "us-ascii" ); //For converting 16-bit unicode characters to bytes.
        CharsetDecoder decoder = charset.newDecoder();  // Transform a sequence of bytes into a sequence of 16-bit Unicode characters.
        CharsetEncoder encoder = charset.newEncoder(); // Transform 16-bit Unicode characters into a bytes.
		
        ByteBuffer inBuffer = null; //Buffer to send data
        CharBuffer outBuffer = null; //Buffer to convert inbuffer's bytes to chars
        int bytesSent, bytesRecv;     // number of bytes sent or received - for error checking if msg was delivered to client
        
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
		
		/**                   UDP CHANNEL CREATION                    */
		//UDP Server
		DatagramChannel udp_channel = DatagramChannel.open();
		udp_channel.configureBlocking(false);
		InetSocketAddress isa2 = new InetSocketAddress(Integer.parseInt(args[0]));
		udp_channel.socket().bind(isa2);
		udp_channel.register(selector, SelectionKey.OP_READ); 
		

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
                        
                        // Register the new connection for read operation
                        cchannel.register(selector, SelectionKey.OP_READ);
                    } 
                    if (key.isReadable()) 
                    {   
						SocketChannel cchannel;
						DatagramChannel cchannel_dtg;
						
						try{
							cchannel = (SocketChannel)key.channel();
							
							if (key.isReadable())
							{
								Socket socket = cchannel.socket();
							
								// Open input and output streams (Receive bytes but send chars)
								inBuffer = ByteBuffer.allocateDirect(BUFFERSIZE);
								outBuffer = CharBuffer.allocate(BUFFERSIZE); //Size the inBuffer to accomodate BUFFERSIZE bytes
								
								
								// Read from socket
								bytesRecv = cchannel.read(inBuffer);
								
								//Check if connection was closed
								if (bytesRecv <= 0)
								{
									//System.out.println("read() error, or connection closed");
									key.cancel();  // deregister the socket
									continue;
								}
								
								inBuffer.flip();      // make buffer available 
								decoder.decode(inBuffer, outBuffer, false); //decode message from inbuffer -> outbuffer
								outBuffer.flip();
								line = outBuffer.toString().trim();
								
								System.out.println("TCP Client: "+ line);
								
								//Terminate command Command: terminate
								if (line.equals("terminate")){
									terminated = true;
									System.exit(0);
								}
								
								//List files in directory Command: list
								else if(line.equals("list")){
									
									//Get files in current directory as string + the string's size in front
									String filesStr = getFilesInCurrentDir();
									String ready_filesStr = addInformation(filesStr,false,cchannel.socket().getPort());
									
									//Put data into the buffer for sending.
									inBuffer = ByteBuffer.wrap(ready_filesStr.getBytes());
									
									//Send data to client
									int bytesWritten = cchannel.write(inBuffer);
									inBuffer.rewind();
									
									continue;
									
								}
	
								//Send over a file Command: get <filename>
								else if(line.length() >=4){
									if(line.charAt(0) == 'g' && line.charAt(1) =='e' && line.charAt(2) == 't' && line.charAt(3) == ' '){
										String filename = line.substring(4,line.length());
										System.out.println("Open file: " + filename);
										
										try{
											String content = new String(Files.readAllBytes(Paths.get(filename)), "us-ascii");
											String msg = content;
											String ready_msg = addInformation(content,true,cchannel.socket().getPort());
											
											//Put data into the buffer for sending.
											inBuffer = ByteBuffer.wrap(ready_msg.getBytes());
											//Send data to client
											int bytesWritten = cchannel.write(inBuffer);
											inBuffer.rewind();	
											
											continue;
																		
										}catch(Exception e){
											System.out.println("open() failed");
											
											String msg = "Server: Error in opening file " + filename;
											String ready_msg = addInformation(msg,false,cchannel.socket().getPort());
											
											//Put data into the buffer for sending.
											inBuffer = ByteBuffer.wrap(ready_msg.getBytes());
											//Send data to client
											int bytesWritten = cchannel.write(inBuffer);
											inBuffer.rewind();	
											
											continue;
										}	
									} else {
										//Put message into bytebuffer
										String unknown_str = "Server: Unknown command: " + line ; //line already includes "\n"
										String ready_unknown_str = addInformation(unknown_str,false,cchannel.socket().getPort());
										
										//Put data into the buffer for sending.
										inBuffer = ByteBuffer.wrap(ready_unknown_str.getBytes());
								
										//Send data to client
										int bytesWritten = cchannel.write(inBuffer);
										inBuffer.rewind();
										continue;										
									}
					
								}
								
								/// !!! THIS SECTION OF CODE IS ONLY REACHED IF COMMAND ISN'T RECOGNIZED !!! " ///
								//Put message into bytebuffer
							
								String unknown_str = "Server: Unknown command: " + line ; //line already includes "\n"
								String ready_unknown_str = addInformation(unknown_str,false,cchannel.socket().getPort());
								
								//Put data into the buffer for sending.
								inBuffer = ByteBuffer.wrap(ready_unknown_str.getBytes());
								
								//Send data to client
								int bytesWritten = cchannel.write(inBuffer);
								inBuffer.rewind();
		
							}
							
						}catch (ClassCastException e){}
						
					    try{
							cchannel_dtg = (DatagramChannel)key.channel();
							// Register the new connection for read operation
							if (key.isReadable())
							{
								// Declare a UDP server socket
								DatagramSocket echoServer = cchannel_dtg.socket();
							
								if (args.length != 1)
								{
									System.out.println("Usage: UDPServer <Listening Port>");
									System.exit(1);
								}
								
								// Try to open a server socket on the given port
								// Note that we can't choose a port less than 1023 if we are not
								// privileged users (root)
								
								try {
									// As long as we receive data, echo that data back to the client.
										inBuffer = ByteBuffer.allocateDirect(BUFFERSIZE);
										outBuffer = CharBuffer.allocate(BUFFERSIZE); //Size the inBuffer to accomodate BUFFERSIZE bytes to send
										
										inBuffer.clear();
										outBuffer.clear();
										
										// Receive data into inBuffer from the socket 
										
										bytesRecv = 0;
										
										if(!cchannel_dtg.isConnected()){
											SocketAddress client = cchannel_dtg.receive(inBuffer);
											cchannel_dtg.connect(client); 
											bytesRecv = inBuffer.position();
											
										}else{							
											bytesRecv = cchannel_dtg.read(inBuffer);
										}
										
										if (bytesRecv <= 0)
										{
											System.out.println("read() error, or connection closed");
											key.cancel();  // deregister the socket
											continue;
										}
											
										inBuffer.flip();      // make buffer available  
										decoder.decode(inBuffer, outBuffer, false);
										outBuffer.flip();
										line = outBuffer.toString().trim();
										System.out.println("UDP Client: " + line);
										
										
										// Echo the message back
										inBuffer.flip();
										bytesSent = cchannel_dtg.write(inBuffer); //write bytes to socket
	
										if (bytesSent != bytesRecv)
										{
											System.out.println("write() error, or connection closed");
											key.cancel();  // deregister the socket
											continue;
										}
										

									}catch(IOException e) {
										System.out.println(e);
									}
								}
							} catch(ClassCastException e){}	
					} 
					
				// end of while (readyItor.hasNext()) 
			} // end of while (!terminated)
	   }}catch(IOException e){System.out.println(e);}
 
        // close all connections
        Set keys = selector.keys();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) 
        {
            SelectionKey key = (SelectionKey)itr.next();
            //itr.remove();
            if (key.isAcceptable()){
                try{((ServerSocketChannel)key.channel()).socket().close();}catch(ClassCastException e){}
				try{((DatagramChannel)key.channel()).socket().close();}catch(ClassCastException e){}
			}
            else if (key.isValid()){
				try{((SocketChannel)key.channel()).socket().close();}catch(ClassCastException e){}
				try{((DatagramChannel)key.channel()).socket().close();}catch(ClassCastException e){}
			}
                
        }
    }
	
	/** The following method takes a string and appends it's own capacity in front plus a "\n" symbol for separation.
	* For example: Input: "HelloThere" Output: "10\nHelloThere"
	* What's it used for? For sending messages to the client, so the client knows the size of message to read. 
	* Returns: String
	*/
	public static String includeCapacity(String s){
		String str = "\n" + s;
		ByteBuffer bb = ByteBuffer.wrap(str.getBytes());
		str = Integer.toString(bb.capacity()) + str; //Format : "SIZEOFMSG\nFile1\nFile2\nFile3...."
		return str;
	}
	
	/** This method takes a string and appends either "0\n" or "1\n" to indicate whether the following message is a file,
	* and therefore should be treated differently by a client.
	* String s - string 
	* Boolean b - 1 if message is to be treated as file, 0 if not.
	*/
	public static String includeIsFile(String s,Boolean b){
		String str = s;
		if(b == true){
			str = "1\n"+str;
		}else{
			str = "0\n"+str;
		}
		
		return str;
		
	}
	
	/** The string combines includeCapacity() and includeIsFile() methods AND includes port number in front.
	* String s - string to send
	* Boolean b - 1 if the msg to be sent is to be treated as a file, 0 if not.
	* int portNum - port number of current connection
	*/
	public static String addInformation(String s,Boolean b,int portNum){
		String str1 = includeCapacity(s);  //eg. "HelloThere" -> "20\nHelloThere"
		String str2 = includeIsFile(str1,b); // eg. "20\nHelloThere" -> "0\n20\nHelloThere"
		str2 = Integer.toString(portNum) + "\n" + str2;
		
		return str2;
		
	}

/**
* The following method returns current files in a directory in a string format, each file is separated with "\n" for display purposes.
* Returns: String
*/
	public static String getFilesInCurrentDir(){
		File folder = new File(System.getProperty("user.dir"));
		File[] listOfFiles = folder.listFiles();
		//Get list of files as string
		String filesStr = "";
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				filesStr = filesStr.concat(listOfFiles[i].getName() + "\n");
				
			} else if (listOfFiles[i].isDirectory()) {
				continue;
			}
		}
		filesStr = filesStr.substring(0,filesStr.length()-1); //Remove the "\n" on the end.
		return filesStr;
	}
}

