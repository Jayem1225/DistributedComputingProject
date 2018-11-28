/**
 * Created by Joshua Sanyika on 11/25/2018.
 */

/*
The Metrics class consists of singletons that are used for multiple instances to collect data from all of them instead
of throwing it away at the end.
It does this by finding the times and data sizes and averaging these values and printing it into it's own text file when
it is ready. Multiple calls to the same text file should overwrite it so that at the end the text file that is left
will be the finalized entry.
 */

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;
import java.io.UnsupportedEncodingException;

public class Metrics {
    private static Metrics SThreadMetrics = null;           //Singleton for SThread to use
    private static Metrics SendDataMetrics = null;          //Singleton to gather data when sending files
    private static Metrics RecieveDataMetrics = null;       //Singleton to gather data when receiving files
    private long start;                                     //The starting time
    private long end;                                       //The ending time
    private long[] dataSizes = null;                        //Array to collect size of data sent or recieved. Should not be initialized for SThread
    private long[] times = new long[100];                   //Array to collect times
    private int timeIndex = 0;                              //Index of the time array the singleton is currently on
    private int dataIndex = 0;                              //Index of data array

    private Metrics(){

    }
    //Creates a new Metrics object for SThread to use if there isn't one already
    public static Metrics SThreadMetrics(){
        if (SThreadMetrics == null){
            SThreadMetrics = new Metrics();
        }
        return SThreadMetrics;
    }

    //Creates a new Metric object if there isn't already one to use to gather data for sent files
    public static Metrics SendDataMetrics(){
        if(SendDataMetrics == null){
            SendDataMetrics = new Metrics();
        }
        return SendDataMetrics;
    }

    //Creates a new Metric object if there isn't already one to use for received data
    public static Metrics RecieveDataMetrics(){
        if(RecieveDataMetrics == null){
            RecieveDataMetrics = new Metrics();
        }
        return RecieveDataMetrics;
    }

    //Sets the starting time
    public void start(){
        start = System.currentTimeMillis();
    }

    //Sets the ending time. The time is then converted to seconds and that time is added to the array. The working index
    //is then increased.
    public void end(){
        end = System.currentTimeMillis();

        long elapsedTime = (end - start) / 1000;
        times[timeIndex] = elapsedTime;
        timeIndex++;

    }

    //Adds the data to a specified text file
    public void gatherData(String fileName){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
        } catch (FileNotFoundException e) {
            System.out.println(fileName + " could not be created.");
        } catch (UnsupportedEncodingException e) {
            System.out.println("UTF not supported");
        }

        //Prints all the times individually and simultaneously computes the sum of the array
        long sum = 0;
        writer.println("All the times are :");
        try {
            for (int i = 0; i < timeIndex; i++) {
                writer.print(times[i] + ", ");
                sum += times[i];
            }
            writer.println("The average of those times is " + sum / timeIndex);
        } catch (NullPointerException e){
            System.out.println("Index does not exist");
        }
        //Does the same for the data array if it has been initialized. It should not be initialzed for SThreadMetrics
        sum = 0;
        if (dataSizes != null) {
            writer.println("All the data sizes are: ");
            try {
                for (int i = 0; i < dataIndex; i++){
                    writer.print(dataSizes[i] + ", ");
                    sum += dataSizes[i];
                }
            }catch (NullPointerException e){
                System.out.println("Index does not exist");
            }
        }

    }

    //Gathers the size of the data sent and adds it to the array
    public void fileSize(File file) {
        if (dataSizes == null){
            dataSizes = new long[100];
        }

        dataSizes[dataIndex] = file.length();
        dataIndex++;
    }
}
