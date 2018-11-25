import java.net.*;
import java.io.*;

public class Server {
	// Constants
	final static int IP = 0;
   	final static int SOCKET = 1;
   	final static int USERNAME = 2;
   	final static String SERVER_ID = "__SERVER__";
   	
   	static boolean serverConnected;
    static int socketNumber;
    static int index;
    static Object [][] RoutingTable;
   	static ServerSocket serverSocket;
   	static Socket clientSocket;
   	static BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

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

	private static void connectToServer() {
		String serverHostname;
		int serverSocketNumber;
		
		try{
			// Server Router Data 
			System.out.println("Please enter the hostname of the server: ");
			serverHostname = getUserInput();
			System.out.println("Please enter the socket for connection to " + serverHostname + ": ");
			serverSocketNumber = Integer.parseInt(getUserInput());
			
			RoutingTable[index][SOCKET] = new Socket(serverHostname, serverSocketNumber);
			RoutingTable[index][IP] = ((Socket) RoutingTable[index][SOCKET]).getInetAddress().getHostName();
			RoutingTable[index][USERNAME] = SERVER_ID;
			++index;
			serverConnected = true;
			System.out.println("Connection Successful!");
		} 
		catch(IOException e) {
			System.err.println("Could not connect to other serverRouter.");
		}
	}
	
	// Listens for incoming server connections and spawns a thread
	// for each new connection.
	private static void listenForConnections() {
   		clientSocket = null; // socket for the thread
		serverSocket = null; // server socket for accepting connections
		
		try {
			serverSocket = new ServerSocket(5555);
			System.out.println("ServerRouter is Listening on port: 5555.");
		}
        catch (IOException e) {
           	System.err.println("Could not listen on port: 5555.");
           	System.exit(1);
           }
		try {
			clientSocket = serverSocket.accept();
			SThread t = new SThread(RoutingTable, clientSocket, index); // creates a thread with a random port
			t.start(); // starts the thread
			index++; // increments the index
			System.out.println("ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress());
		}
		catch (IOException e) {
			System.err.println("Client/Server failed to connect.");
			System.exit(1);
		}
	}
	
	// Prints methods
	private static void printServerInfo() {
		int count = 0;
		
		for (int i = 0; i<RoutingTable.length; ++i) {
			if (RoutingTable[i][USERNAME].equals(SERVER_ID)) {
				++count;
	    		System.out.println();
	    		System.out.println(" SERVER INFO ");
	    		System.out.println("=============");
	    		System.out.println("IP Address:\t" + RoutingTable[i][IP]);
	    		System.out.println("Socket:\t" + RoutingTable[i][SOCKET]);
	    		System.out.println();
			}
		}
		
		if (count < 1) {
			serverConnected = false;
			System.out.println("No external servers currently connected. ");
		}
	}
	
	private static void printSystemInfo() throws UnknownHostException {
		System.out.println();
		System.out.println(" SYSTEM INFO ");
		System.out.println("=============");
		System.out.println("Hostname:\t" + serverSocket.getInetAddress().getCanonicalHostName());
		System.out.println("Socket:\t" + socketNumber);
		System.out.println();
	}
	
    private static void printMenu() {
		System.out.println();
		System.out.println(" M A I N   M E N U ");
		System.out.println("===================");
		System.out.println(" 1) Connect external server ");
		System.out.println(" 2) Print system connection info ");
		System.out.println(" 3) Print external server info ");
		System.out.println(" 4) Start service "); 
		System.out.println(" 5) Exit program "); 
		System.out.println("Enter a number to proceed with the corresponding action: ");
		System.out.println();
    }
    
   	public static void main(String[] args) throws IOException {
   		serverConnected = false;
   		index = 0;
   		RoutingTable = new Object [100][3];

		System.out.println("Welcome to Image Sender Pro Server Edition!");
		System.out.println("Before the server can begin listening for connections, ");
		System.out.println("Please fill out the following information for you server. ");
		System.out.println("What socket number would you like to use for new connections? ");
		socketNumber = Integer.parseInt(getUserInput());
		
		// Menu
		printMenu();
		switch ( Integer.parseInt(getUserInput()) ) {
		case 1:
			// Connect to external server
			connectToServer();
			break;
		case 2:
			// Print system connection info
			printSystemInfo();
			break;
		case 3:
			// Print external server connection info
			printServerInfo();
			break;
		case 4:
			// Start service
			listenForConnections();
			break;
		case 5:
			// Exit program
			System.out.println();
			System.out.println("Goodbye!");
			break;
		}

		//closing connections
		clientSocket.close();
		serverSocket.close();
    }
}