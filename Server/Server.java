	import java.net.*;
   import java.io.*;

    public class Server {
       public static void main(String[] args) throws IOException {
         Socket clientSocket = null; // socket for the thread
         Object [][] RoutingTable = new Object [100][3]; // routing table
			int SockNum = 5555; // port number for server router 1
         Boolean Running = true;
         final int SR_RECORD = 0;
			int ind; // indext in the routing table	
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
         catch(ConnectException e){
            System.err.println("Could not connect to other serverRouter.");
            ind = 0;
         }
			
			// Creating threads with accepted connections
			while (Running == true)
			{
			try {
				clientSocket = serverSocket.accept();
				SThread t = new SThread(RoutingTable, clientSocket, ind); // creates a thread with a random port
				t.start(); // starts the thread
				ind++; // increments the index
            System.out.println("ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress());
         } catch (IOException e){
               System.err.println("Client/Server failed to connect.");
               System.exit(1);
             }
               /*try {
               Socket = new Socket(routerName2, SockNum2);
               out = new PrintWriter(Socket.getOutputStream(), true);
               in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
               }
               catch (UnknownHostException e) {
               System.err.println("Don't know about router: " + routerName2);
               System.exit(1);
               } 
               catch (IOException e){
               System.err.println("Client/Server failed to connect.");
               System.exit(1);
               }
            }*/
			}//end while
			
			//closing connections
		   clientSocket.close();
         serverSocket.close();

      }
   }