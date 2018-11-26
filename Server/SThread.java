import java.io.*;
import java.net.*;
import java.lang.Exception;

	
public class SThread extends Thread 
{
	// Constants
	final int IP = Server.IP;
   	final int SOCKET = Server.SOCKET;
   	final int USERNAME = Server.USERNAME;
   	final String SERVER_ID = Server.SERVER_ID;
   	final String REMOVED_ENTRY = "__DELETED__";
   	final String DNF = "DNF";
   	
	private Object [][] RTable; // routing table
	private PrintWriter out, outServer; // writers (for writing back to the machine and to destination)
	private BufferedReader in, inServer; // reader (for reading from the machine connected to)
	private String inputLine, outputLine, user, addr; // communication strings
	private Socket outSocket; // socket for communicating with a destination
	private int ind; // index in the routing table

	// Constructor
	SThread(Object [][] Table, Socket toClient, int index) throws IOException {
		out = new PrintWriter(toClient.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));
		RTable = Table;
		addr = toClient.getInetAddress().getHostAddress();
		RTable[index][IP] = addr; // IP addresses 
		RTable[index][SOCKET] = toClient; // sockets for communication
        RTable[index][USERNAME] = in.readLine();
        System.out.println(RTable[index][USERNAME]);
        ind = index;
	}
	
	// Returns the index of the record client is searching for.
	// Returns -1 if a record could not be found.
	private int searchForUserLocally(String username) {
		for ( int i=0; RTable[i][USERNAME]!=null && i<RTable.length; i++)
			if (RTable[i][USERNAME].equals(username))
				return i;

		// Could not find index
	    return -1;
	}
	
	// Returns record of client data from remote server
	// Returns DNF if a record could not be found remotely
	private String[] searchForUserRemotely(String username) {
		String[] record = { DNF, null, null };

		for (int i=0; i<RTable.length && record[0].equals(DNF); ++i)
			if ( ((String)RTable[i][USERNAME]).equals(SERVER_ID) ) {
				try { record = requestDataFromExternalServer((Socket)RTable[i][SOCKET], username); }
				catch (IOException e) {
					System.err.println("Lost connection to an external server... Closing connection. ");
					RTable[i][USERNAME] = REMOVED_ENTRY;
				}
			}
		
		return record;
	}
	
	// Calls methods necessary to find and deliver requested
	// information back to the client.
	private void searchForUserAndSendData(String username) throws IOException {
		String[] record = new String[3];
		int searchIndex = searchForUserLocally(username);
		
		if (searchIndex > -1)
			writeClientData(searchIndex);
		else if (remoteServerAvailable()) {
			record = searchForUserRemotely(username);
			if (!record[0].equals(DNF))
				writeClientData(record);
		}
		else
			writeDNF();
	}
	
	// Searches for a remote server in the lookup table and returns
	// true if one exists, else returns false.
	private boolean remoteServerAvailable() {
		for (int i=0; i<RTable.length; ++i)
			if ( ((String)RTable[i][USERNAME]).equals(SERVER_ID) )
				return true;
		
		return false;
	}
	
	private String[] requestDataFromExternalServer(Socket serverSocket, String username) throws IOException {
		String[] record = new String[3];
		BufferedReader serverInput = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
		PrintWriter serverOutput = new PrintWriter(serverSocket.getOutputStream());
		
		serverOutput.println(username);
		record[0] = serverInput.readLine();
		if (record[0].equals(DNF))
			return record;
		record[1] = serverInput.readLine();
		record[2] = serverInput.readLine();
		
		return record;
	}
	
	private void writeDNF() throws IOException {
		out.println(DNF);
	}
	
	private void writeClientData(int localTableIndex) {
		int i = localTableIndex;
		out.println(RTable[i][IP]);
		out.println( ((Socket)RTable[i][SOCKET]).getPort());
		out.println( ((Socket)RTable[i][SOCKET]).getInetAddress().getHostName());
	}
	
	private void writeClientData(String[] userRecord) {
		out.println(userRecord[0]);
		out.println(userRecord[1]);
		out.println(userRecord[2]);
	}
   
	// Run method (will run for each machine that connects to the ServerRouter)
	public void run() {
		try {
			// Initial sends/receives
			user = in.readLine(); // initial read (the destination for writing)
			System.out.println("Searching for " + user);
		}
		catch(IOException e) {
			System.out.println("No input");
		}
		
		// waits 10 seconds to let the routing table fill with all machines' information
		try { Thread.currentThread().sleep(10000); }
		catch(InterruptedException ie){	System.out.println("Thread interrupted"); }
		
		// loops through the routing table to find the destination
		try { searchForUserAndSendData(user); }
		catch (IOException e) { System.out.println("FATAL ERROR: Could not write to client! "); }
	}
}

