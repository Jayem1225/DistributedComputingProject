import java.io.*;
import java.net.*;
import java.nio.file.Files;

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
                static boolean done;
		static boolean serverConnected;
		static String username;
                static Metrics metrics;
		static Metrics sendMetrics;		

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
		    	System.out.println("Ensure server credentials are correct and try again.");
		    	return;
	    	}
	    	try {
	    		serverInput = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
	    		serverOutput = new PrintWriter(serverSocket.getOutputStream(), true);

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
    	
    	// Sends file to user connected to socket.
    	private static void sendFile(String[] aHost ) throws Exception {

    		int port = Integer.parseInt(aHost[SOCKET]);
    		ServerThread st = new ServerThread(port);
			st.start();
			InetAddress ip = InetAddress.getByName(aHost[IP]);
			Socket s = new Socket(ip, port);
			s.setSoTimeout(15000);
			InputStream is = s.getInputStream();
			sendMetrics.start(); //Start capturing send time
			byte[] fileDataBuffer = new byte[256];
			is.read(fileDataBuffer, 0, fileDataBuffer.length);
			String metadata = new String(fileDataBuffer, 0, fileDataBuffer.length);
			String[] fileData = metadata.split(",");
			String nameOfFile = fileData[0];
			String TypeOfFile = fileData[1];
			String SizeOfFile = fileData[2];

			byte[] dataBuffer = new byte[1024];
			FileOutputStream os = new FileOutputStream("C:\\Users\\ncurtin\\Desktop\\outputfolder\\"+ nameOfFile);
			int status = is.read(dataBuffer);
			while(status != -1){
				os.write(dataBuffer);
				dataBuffer = new byte[1024];
				status = is.read(dataBuffer);
			}
			sendMetrics.end(); // Stop capturing send time
         os.close();
         System.out.println("It took exactly " + overallTime + " nano seconds to send " + nameOfFile);
		}

    	// Handles capturing data from the user and calls methods to send file to a user.
    	private static void findAndSendFileToUser() {
    		String[] connectInfo = new String[3];
    		
    		try {
    			System.out.println("To whom would you like to send a file? ");
    			connectInfo = getPeerData(getUserInput());
    		
    			if (!serverConnected) {
    				System.err.println("Could not send file; please reconnect to server.");
    				return;
    			}
				sendFile(connectInfo);
    			System.out.println("File sent! ");
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
    		done = false; // Helps track program state
                serverConnect = false;
                metrics = new Metrics();
                sendMetrics = metrics.SendDataMetrics();
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
    				findAndSendFileToUser();
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
 	                sendMetrics.gatherData("clientSendData.txt");
  
			// closing connections
    			dataSocket.close();
			serverInput.close();
			serverOutput.close();
			serverSocket.close();
      }	
   }
