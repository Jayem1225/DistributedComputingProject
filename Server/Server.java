import java.net.*;
import java.io.*;

public class Server {
    static int socketNumber;
   	static Socket serverSocket;
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

	// Prints methods
	//Print Methods
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
		System.out.println(" 3) Print connect server info ");
		System.out.println(" 4) Start service "); 
		System.out.println(" 5) Exit program "); 
		System.out.println("Enter a number to proceed with the corresponding action: ");
		System.out.println();
    }
    
   	public static void main(String[] args) throws IOException {
   		Socket clientSocket = null; // socket for the thread
   		Object [][] RoutingTable = new Object [100][3]; // routing table
		int SockNum = 5555; // port number for server router 1
		Boolean Running = true;
		final int SR_RECORD = 0;
		int ind; // index in the routing table	
		Socket Socket = null;
		PrintWriter out = null; // for writing to ServerRouter
		BufferedReader in = null; // for reading form ServerRouter

        

		//Accepting connections
		ServerSocket serverSocket = null; // server socket for accepting connections
		try {
			serverSocket = new ServerSocket(5555);
			System.out.println("ServerRouter is Listening on port: 5555.");
		}
        catch (IOException e) {
           	System.err.println("Could not listen on port: 5555.");
           	System.exit(1);
           }
		try{
			// Server Router Data 
			RoutingTable[SR_RECORD][0] = "10.80.22.980"/*Enter Server Router IP here*/;
			RoutingTable[SR_RECORD][1] = new Socket("J263-05", 5550);
			RoutingTable[SR_RECORD][2] = "Server Router 2";
			ind = 1;
		} 
		catch(ConnectException e) {
			System.err.println("Could not connect to other serverRouter.");
			ind = 0;
		}
			
		// Creating threads with accepted connections
		while (Running == true) {
			try {
				clientSocket = serverSocket.accept();
				SThread t = new SThread(RoutingTable, clientSocket, ind); // creates a thread with a random port
				t.start(); // starts the thread
				ind++; // increments the index
				System.out.println("ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress());
			}
			catch (IOException e) {
				System.err.println("Client/Server failed to connect.");
				System.exit(1);
			}
		}//end while
		
		//closing connections
		clientSocket.close();
		serverSocket.close();

    }
}