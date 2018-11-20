   import java.awt.image.BufferedImage;
   import java.io.*;
   import java.net.*;

import javax.imageio.ImageIO;

    public class Client {
    	
    	private static Socket connectToServer(String hostName, int socketNum){
    		Socket socket;
    		
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
            
            return socket;
    	}
    	
    	private static void sendImage(String filePath, DatagramSocket socket) {
    		try {
    			BufferedImage img = ImageIO.read(new File(filePath));
        		ByteArrayOutputStream out = new ByteArrayOutputStream();
        		ImageIO.write(img, filePath.substring(filePath.lastIndexOf('.') + 1), out);
    		}
    		catch (IOException e) {
    			System.err.println("Couldn't find file at path " + filePath);
    			return;
    		}

    		
    	}
    	
    	private static void printMenu() {
    		System.out.println(" M A I N   M E N U ");
    		System.out.println("===================");
    		System.out.println("1) ");

    		
    	}
    	
    	public static void main(String[] args) throws IOException {
    	   boolean done = false;
    	   
    	   System.out.println("");
    	   
    	   while (!done) {
    		   
    		   
    	   }
    	   
			// Variables for setting up connection and communication
    	    Socket Socket = null; // socket to connect with ServerRouter
         	PrintWriter out = null; // for writing to ServerRouter
         	BufferedReader in = null; // for reading form ServerRouter
			InetAddress addr = InetAddress.getLocalHost();
				
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
		/*
			t0 = System.currentTimeMillis();
      	
         
			// Communication while loop
	        while ((fromServer = in.readLine()) != null) {
	            System.out.println("Server: " + fromServer);
					t1 = System.currentTimeMillis();
	            if (fromServer.equals("Bye.")) // exit statement
	               break;
					t = t1 - t0;
					System.out.println("Cycle time: " + t);
	          
	            fromUser = fromFile.readLine(); // reading strings from a file
	            if (fromUser != null) {
	               System.out.println("Client: " + fromUser);
	               out.println(fromUser); // sending the strings to the Server via ServerRouter
						t0 = System.currentTimeMillis();
            	}	
         	}
		*/
			// closing connections
         out.close();
         in.close();
         Socket.close();
      }
   }
