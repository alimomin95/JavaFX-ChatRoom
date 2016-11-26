package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javafx.scene.control.ScrollPane;


/**
 * Created by Ali Ziyaan Momin on 11/25/2016.
 */
public class Client {
    private String hostIPAddress = "127.0.0.1";
    private int hostPortNumber = 5000;
    private BufferedReader reader;
    private PrintWriter writer;
    
    public ScrollPane chatListPane;
    

    public static void main(String[] args){
        try{
            new Client().run();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initViewController(){
    	
    }

    private void setUpNetworking() throws Exception {
        Socket socket = new Socket(hostIPAddress, hostPortNumber);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());
        System.out.println("Networking established with " + hostIPAddress);
        Thread readerThread = new Thread(new IncomingReader());
        Thread writerThread = new Thread(new MessageWriter());
        readerThread.start();
        writerThread.start();
    }


    class IncomingReader implements Runnable{

        @Override
        public void run() {
            String message;
            try{
                while((message = reader.readLine()) != null){
                    System.out.println(message);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    class MessageWriter implements Runnable{

        @Override
        public void run() {
            //System.out.print("User: ");
            Scanner sc = new Scanner(System.in);
            while(sc.hasNextLine()) {
                writer.println(sc.nextLine());
                writer.flush();
                //System.out.print("User: ");
            }
        }
    }

    public void run() throws Exception{
        setUpNetworking();
        initViewController();
    }
}
