package assignment7;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Ali Ziyaan Momin on 11/25/2016.
 */
public class Client extends Application {
    private String hostIPAddress = "127.0.0.1";
    private int hostPortNumber = 5000;
    private BufferedReader reader;
    private PrintWriter writer;

    // ------------------------------- GUI Components: ----------------------------------------
    @FXML
    private Button logout;
    @FXML
    private TextArea convoBox;
    @FXML
    private TextArea messageBox;
    // ----------------------------------------------------------------------------------------


    public static void main(String[] args){
        try{
            new Client().run();
        }catch (Exception e){
            e.printStackTrace();
        }
        launch(args);

    }

    private void initViewController(){

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logout = new Button();
        convoBox = new TextArea();
        messageBox = new TextArea();
        
        Parent root = FXMLLoader.load(getClass().getResource("mainscene.fxml"));
        primaryStage.setTitle("Chatter");
        primaryStage.setScene(new Scene(root, 637, 488));
        primaryStage.setResizable(false);
        primaryStage.show();

        messageBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){
                    String text = messageBox.getText();
                    convoBox.setText(text);
                    messageBox.setText("");
                }
            }
        });
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
