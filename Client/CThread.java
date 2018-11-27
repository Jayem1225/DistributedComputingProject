
import java.net.DatagramSocket;

public class CThread extends Thread {
	private DatagramSocket socket;
	
	CThread( DatagramSocket dataSocket ) {
		socket = dataSocket;
	}
	
	public void run() {
		
	}
	
}
