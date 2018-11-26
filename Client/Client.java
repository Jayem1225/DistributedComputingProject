   import java.awt.image.BufferedImage;
   import java.io.*;
   import java.net.*;

import javax.imageio.ImageIO;

    public class Client {
		// Constants
		final static int IP = 0;
	   	final static int SOCKET = 1;
	   	final static int HOSTNAME = 2;

	   	static DatagramSocket dataSocket;
	   	static Socket serverSocket;
	   	static BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
		static BufferedReader serverInput;
		static PrintWriter serverOutput;
		static boolean serverConnected;
		static String username;
		
		// Handles initial connection to peer server
    	private static Socket connectToServer() throws IOException {
    		Socket socket;
    		
			System.out.println("Please enter the hostname of the server: ");
			String hostName = getUserInput();
			System.out.println("Please enter the socket for connection to " + hostName + ": ");
		    int socketNum = Integer.parseInt(getUserInput());
    		System.out.println("Attempting to connect to server " + hostName + " on port number " + socketNum + "... ");
    		
    		// Tries to connect to the ServerRouter
            try { socket = new Socket(hostName, socketNum); } 
            catch (UnknownHostException e) {
            	System.err.println("Don't know about device: " + hostName);
                return null;
            } 
            catch (IOException e) {
            	System.err.println("Couldn't get I/O for the connection to: " + hostName);
                return null;
            }
            
            System.out.println("Connection Successful!");
            return socket;
    	}

    	private static void initializeServerConnection() {
			try { serverSocket = connectToServer(); }
	    	catch (IOException e){
		    	System.out.println("Connection Failed!");
		    	System.out.println("Ensure server crudentials are correct and try again.");
		    	return;
	    	}
	    	try {
	    		serverInput = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
	    		serverOutput = new PrintWriter(serverSocket.getOutputStream());

		    	// Give server username for server record
    		   	serverOutput.println(username);
    		   	serverConnected = true;
		    }
		    catch (IOException e) {
		    	System.err.println("Fatal Error: Lost connection to Server!");
		    	System.err.println("Please try to re-establish connection. ");
		    	serverConnected = false;
		    	return;
		    }
    	}
		
    	// Assists in capturing input from the user.
    	private static String getUserInput() {
    		try {
    			return userInput.readLine();
    		}
    		catch (IOException e) {
    			System.err.println("FATAL ERROR: Could not read from standard input");
    			System.err.println("Aborting program...");
    			System.exit(0);
    			return null;
    		}
    	}
    	
    	// Gets peer connection data from server
    	private static String[] getPeerData(String peerUsername) {
    		String[] connectData = new String[3];
    		
    		try {
    			serverOutput.println(peerUsername);// initial send (IP of the destination Server)
    			connectData[IP] = getUserInput();//receives the IP from the ServerRouter
    		
    			// If server could not find user, return null array
    			if (connectData[IP].equals("DNF"))
    				return null;
    		
    			connectData[SOCKET] = serverInput.readLine(); //receives the Socket from the ServerRouter
    			connectData[HOSTNAME] = serverInput.readLine(); //receives the HostName from the ServerRouter
    		}
    		catch (IOException e) {
    			System.err.println("Fatal error! Server connection lost!");
    			serverConnected = false;
    		}
			//System.out.println("IP: " + connectData[IP]);
			//System.out.println("Socket: " + connectData[SOCKET]);
			//System.out.println("Host Name: " + connectData[HOSTNAME]);
    		
			return connectData;
    	}
    	
    	// Sends image file to user connected to socket.
    	private static void sendImage(File file, DatagramSocket socket) {
    		try {
    			System.out.println("Attempting to send file " + file.getName());
    			if (!file.exists())
    				throw new IOException("File does not exist!");    			
    			BufferedImage img = ImageIO.read(file);
        		ByteArrayOutputStream out = new ByteArrayOutputStream();
        		ImageIO.write(img, file.getName().substring(file.getName().lastIndexOf('.') + 1), out);
        		out.flush();
        		byte[] buffer = out.toByteArray();
        		DatagramSocket clientSocket = socket;
        		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socket.getLocalAddress(), socket.getLocalPort());
        		clientSocket.send(packet);
        		System.out.println("File sent to " + clientSocket.getLocalAddress() + " on port number " + clientSocket.getLocalPort());
    		}
    		catch (IOException e) {
    			if (e.getMessage().equals("File does not exist!"))
    				System.err.println("Couldn't find file at path " + file.getAbsolutePath());
    			else
    				System.err.println("Couldn't connect to peer at " + socket.getLocalAddress());
    			return;
    		}
    	}

    	// Handles capturing data from the user and calls methods to send file to a user.
    	private static void findAndSendImageToUser() {
    		String[] connectInfo = new String[3];
    		File file;
    		
    		try {
    			System.out.println("To whom would you like to send a file? ");
    			connectInfo = getPeerData(getUserInput());
    		
    			if (!serverConnected) {
    				System.err.println("Could not send file; please reconnect to server.");
    				return;
    			}
    			
    			System.out.println("Enter the path and file name of the file you'd like to send: ");
    			file = new File(getUserInput());
    			
    			dataSocket = new DatagramSocket();
    			dataSocket.connect(InetAddress.getByName(connectInfo[HOSTNAME]), Integer.parseInt(connectInfo[SOCKET]));
    			sendImage(file, dataSocket);
 
    			dataSocket.close();
    			System.out.println("Image sent! ");
    		}
    		catch (Exception e) {
    			System.err.println("Problem connecting to peer! ");
    			System.err.println("Maybe peer is not available? ");
    		}
    	}
    	
    	//Print Methods
    	private static void printSystemInfo() throws UnknownHostException {
    		System.out.println();
    		System.out.println(" SYSTEM INFO ");
    		System.out.println("=============");
    		System.out.println("Hostname:\t" + serverSocket.getInetAddress().getCanonicalHostName());
    		System.out.println("Socket:\t" + serverSocket.getPort());
    		System.out.println();
    	}
    	
    	private static void printServerInfo() throws UnknownHostException {
    		System.out.println();
    		System.out.println(" SERVER INFO ");
    		System.out.println("=============");
    		System.out.println("Hostname:\t" + InetAddress.getLocalHost().getCanonicalHostName());
    		System.out.println("Socket:\t" + dataSocket.getLocalPort());
    		System.out.println();
    	}
    	
    	private static void printMenu() {
    		System.out.println();
    		System.out.println(" M A I N   M E N U ");
    		System.out.println("===================");
    		System.out.println(" 1) Send file to user ");
    		System.out.println(" 2) Print system connection info ");
    		System.out.println(" 3) Print server connection info ");
    		System.out.println(" 4) Exit program "); 
    		System.out.println("Enter a number to proceed with the corresponding action: ");
    		System.out.println();
    	}
    	
    	public static void main(String[] args) throws IOException {
    		boolean done = false; // Helps track program state
    		serverConnected = false;
    	   
    		// Welcome
    		System.out.println("Welcome to Image Sender Pro!");
    		System.out.println("Please enter a username: ");
    		username = getUserInput();
    		System.out.println("Hello, " + username + "! ");
    		
    		// Establish Server Connection
    		System.out.println("Before you can send files, you must establish connection to a server.");
    		while (!done) {

    			while (!serverConnected)
    				initializeServerConnection();
    	   
    			// Menu
    			printMenu();
    			switch ( Integer.parseInt(getUserInput()) ) {
    			case 1:
    				// Send file to user
    				findAndSendImageToUser();
    				break;
    			case 2:
    				// Print system connection info
    				printSystemInfo();
    				break;
    			case 3:
    				// Print server connection info
    				try {
    					printServerInfo();
    				}
    				catch (IOException e) {
    					serverConnected = false;
    				}
    				break;
    			case 4:
    				// Exit program
    				System.out.println();
    				System.out.println("Goodbye!");
    				done = true;
    				break;
    			}
    		}
    		
			// closing connections
    		dataSocket.close();
			serverInput.close();
			serverOutput.close();
			serverSocket.close();
      }	
   }
