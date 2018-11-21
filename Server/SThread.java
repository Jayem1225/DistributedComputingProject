import java.io.*;
import java.net.*;
import java.lang.Exception;

	
public class SThread extends Thread 
{
	// Constants
	final int IP = 0;
   	final int SOCKET = 1;
   	final int USERNAME = 2;
   	final String DNF = "DNF";
   	
	private Object [][] RTable; // routing table
	private PrintWriter out, outServer; // writers (for writing back to the machine and to destination)
	private BufferedReader in, inServer; // reader (for reading from the machine connected to)
	private String inputLine, outputLine, user, addr; // communication strings
	private Socket outSocket; // socket for communicating with a destination
	private int ind; // index in the routing table

	// Constructor
	SThread(Object [][] Table, Socket toClient, int index) throws IOException
	{
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
	
   private void writeClientData(String ip, Socket socket)throws IOException{
	   	out.println(ip);
	   	out.println(socket.getPort());
	   	out.println(socket.getInetAddress().getHostName());
   }
   
   private void writeClientData(String ip_in, String socket_in, String hostname_in)throws IOException{
	   	out.println(ip_in);
	   	out.println(socket_in);
	   	out.println(hostname_in);
   }
   
   private void forwardClientData(String response, BufferedReader inFromServer) throws IOException{
	   	if (response.equals(DNF))
	   		return;
         
	   	String client_ipAddress = response;
	   	String client_socket = inFromServer.readLine();
	   	String client_hostname = inFromServer.readLine();
      
	   	writeClientData(client_ipAddress, client_socket, client_hostname);
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
		try {
    		Thread.currentThread().sleep(10000); 
		}
		catch(InterruptedException ie){
		System.out.println("Thread interrupted");
		}
		
		// loops through the routing table to find the destination
		boolean found = false;
		for ( int i=0; RTable[i][USERNAME]!=null && i<RTable.length && !found; i++) {
			if (RTable[i][USERNAME].equals(user))
				try {
					writeClientData((String) RTable[i][IP], (Socket) RTable[i][SOCKET]);
				}
				catch (IOException e){
					System.err.println("Line 83");
					found = true;
                }
				   
		}
		if (!found) {
			String response = DNF;
            for ( int i=0; RTable[i][USERNAME]!=null && i<RTable.length && !found; i++) {
            	if (((String)RTable[i][USERNAME]).contains("Server Router") && i != ind) {
            		outSocket = (Socket) RTable[i][SOCKET];
                    try {
                    	outServer = new PrintWriter(outSocket.getOutputStream(), true);
                    }
                    catch(IOException e) {
                    }
                    
                    try{
                        inServer = new BufferedReader(new InputStreamReader(outSocket.getInputStream()));
                    }
                    catch (IOException e) {
                    }
                    
                    outServer.println(user);
                    try {
                    	response = inServer.readLine();
                        if (!response.equals(DNF)) {
                        	writeClientData((String) RTable[i][IP], (Socket) RTable[i][SOCKET]);
                            found = true;
                        }
                    }
                    catch (IOException e) {
                    }
            	}
            }
		}
        if (!found)
        	out.println(DNF);
	}
}

