   import java.awt.image.BufferedImage;
   import java.io.*;
   import java.net.*;

import javax.imageio.ImageIO;

    public class Client {
		// Constants
		final static int IP = 0;
	   	final static int SOCKET = 1;
	   	final static int HOSTNAME = 2;

	   	static Socket serverSocket;
	   	static BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
		static BufferedReader serverInput;
		static PrintWriter serverOutput;
    	
    	private static Socket connectToServer() throws IOException {
    		Socket socket;
			System.out.println("Please enter the hostname of the server: ");
			String hostName = userInput.readLine();
			System.out.println("Please enter the socket for connection to " + hostName + ": ");
		    int socketNum = Integer.parseInt(userInput.readLine());
    		System.out.println("Attempting to connect to server " + hostName + " on port number " + socketNum + "... ");
    		
    		// Tries to connect to the ServerRouter
            try {
            	socket = new Socket(hostName, socketNum);
            } 
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
    	
    	private static String[] getPeerData(String peerUsername) throws IOException{
    		String[] connectData = new String[3];
    		
    		serverOutput.println(peerUsername);// initial send (IP of the destination Server)
    		connectData[IP] = serverInput.readLine();//receives the IP from the ServerRouter
    		System.out.println("IP: " + connectData[IP]);
    		connectData[SOCKET] = serverInput.readLine(); //receives the Socket from the ServerRouter
			System.out.println("Socket: " + connectData[SOCKET]);
			connectData[HOSTNAME] = serverInput.readLine(); //receives the HostName from the ServerRouter
			System.out.println("Host Name: " + connectData[HOSTNAME]);
    		
			return connectData;
    	}
    	
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
    			System.err.println("Couldn't find file at path " + file.getAbsolutePath());
    			return;
    		}
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
    		File file;
    		String[] peerConnectionData = new String[3];
    		String username;
    		boolean done = false; // Helps track program state
    	   
    		// Welcome
    		System.out.println("Welcome to Image sender pro!");
    		System.out.println("Please enter a username: ");
    		username = userInput.readLine();
    		System.out.println("Hello, " + username + "! ");
    		
    		// Establish Server Connection
    		System.out.println("Before you can send files, you must establish connection to a server.");
    		while (!done) {
    		    serverSocket = connectToServer();
    		    
    		    if (serverSocket == null) {
    		    	System.out.println("Connection Failed!");
    		    	System.out.println("Ensure server crudentials are correct and try again.");
    		    }
    		    else {
    		    	try {
    		    		serverInput = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    		    		serverOutput = new PrintWriter(serverSocket.getOutputStream());

    		    		// Give server username for server record
        		    	serverOutput.println(username);
        		    	done = true;
    		    	}
    		    	catch (IOException e) {
    		    		System.err.println("Fatal Error: Lost connection to Server!");
    		    		System.err.println("Please try to re-establish connection. ");
    		    	}
    		    }
    		}
    		done = false;
    	   
    		// Menu
    		while (!done) {
    			printMenu();
    			switch ( Integer.parseInt(userInput.readLine()) ) {
    			case 1:
    				// Send file to user
    				break;
    			case 2:
    				// Print system connection info
    				break;
    			case 3:
    				// Print server connection info
    				break;
    			case 4:
    				// Exit program
    				done = true;
    				break;
    			}
    		}

    		/*
			// Variables for message passing	
			Reader reader = new FileReader("file.txt"); 
			BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file
			String fromServer, fromServer2, fromServer3; // messages received from ServerRouter
			String fromUser; // messages sent to ServerRouter
			String userName ="Bob"; // destination IP (Server)
			long t0, t1, t;
			
			// Communication process (initial sends/receives
			out.println("Fred");
			out.println(userName);// initial send (IP of the destination Server)
			fromServer = in.readLine();//receives the IP from the ServerRouter
			System.out.println("IP: " + fromServer);
			fromServer2 = in.readLine(); //receives the Socket from the ServerRouter
			System.out.println("Socket: " + fromServer2);
			fromServer3 = in.readLine(); //receives the HostName from the ServerRouter
			System.out.println("Host Name: " + fromServer3);
			*/
    		
			// closing connections
			serverOutput.close();
			serverInput.close();
			serverSocket.close();
      }	
   }
