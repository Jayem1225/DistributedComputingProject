import java.io.*;
import java.net.*;
import java.util.Random;

public class ServerThread extends Thread {
    Metrics receiveMetrics;

    ServerSocket ss;
    public ServerThread(int _port) throws Exception {
        receiveMetrics = Client.metrics.RecieveDataMetrics();
        ss = new ServerSocket(_port);
    }
    public void run() {
        try {
            while(!Client.done)
            {
                Socket s = ss.accept();
                File folder = new File("C:\\Users\\ncurtin\\Desktop\\sourcefolder");
                File[] files = folder.listFiles();
                File chosen = null;
                Random r = new Random();
                while(chosen == null) {
                    chosen = files[r.nextInt(files.length)];
                    if(!chosen.isFile())
                        chosen = null;
                }
                String fileData = chosen.getName() + "," + chosen.getName().substring(chosen.getName().lastIndexOf(".")) + "," + chosen.length() + ",";//name, extension, size (bytes)
                byte[] fileBytes = fileData.getBytes();
                byte[] push_bytes = new byte[256];
                for(int i = 0; i < fileBytes.length; i++) {
                    push_bytes[i] = fileBytes[i];
                }
                OutputStream os = s.getOutputStream();
                os.write(push_bytes, 0, 256);
                FileInputStream fr = new FileInputStream(chosen);
                byte[] dataBuffer = new byte[1024];
                int status = fr.read(dataBuffer);
		receiveMetrics.start(); // Start capturing metrics
                while(status != -1) {
                    os.write(dataBuffer);
                    dataBuffer = new byte[1024];
                    status = fr.read(dataBuffer);
                }
		receiveMetrics.end();
                fr.close();
                os.close();
                s.close();
            }
   	    receiveMetrics.gatherData("clientReceiveData.txt");
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
