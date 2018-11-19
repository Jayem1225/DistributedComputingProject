import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Test {
    public static void main(String args[]) {

        final int BUFFERSIZE = 4 * 1024;
        String sourceFilePath = "C:\\Users\\ncurtin\\Downloads\\videoplayback.3gp";
        String outputFilePath = "C:\\Users\\ncurtin\\Downloads\\videoplayback2.3pg";

        try(
                FileInputStream fin = new FileInputStream(new File(sourceFilePath));
                FileOutputStream fout = new FileOutputStream(new File(outputFilePath));
        ){

            byte[] buffer = new byte[BUFFERSIZE];

            while(fin.available() != 0) {
                fin.read(buffer);
                fout.write(buffer);
            }

        }
        catch(Exception e) {
            System.out.println("Something went wrong! Reason: " + e.getMessage());
        }

    }
}