package assignment7;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javafx.scene.control.ScrollPane;
import javafx.collections.FXCollections;
//testing
import javafx.collections.ObservableList;


/**
 * Created by Ali Ziyaan Momin on 11/25/2016.
 */
public class Client extends Application {
    private String hostIPAddress = "127.0.0.1";
    private int hostPortNumber = 5000;
    private String username = "quinn";
    private String password = "pswd";
    
    private BufferedReader reader;
    private PrintWriter writer;
    
    public ScrollPane chatListPane;
    

    // ------------------------------- GUI Components: ----------------------------------------
    @FXML
    private Button logout;
    @FXML
    private TextArea convoBox;
    @FXML
    private TextArea messageBox;
    private boolean enterPressed = false;
    private boolean shiftPressed = false;
    @FXML
    private ListView<String> chatListView;
    @FXML
    private ListView<String> personListView;
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
        //Not sure if we need this
    }

    // ---------------------------- This is for GUI functionality ------------------------------
    @Override
    public void start(Stage primaryStage) throws Exception {
    	// I don't think you need these with scenebuilder
        //logout = new Button();
        //convoBox = new TextArea();
       // messageBox = new TextArea();
       // chatListView = new ListView<String>();
      //  personListView = new ListView<String>();

        Parent root = FXMLLoader.load(getClass().getResource("mainscene.fxml"));
        primaryStage.setTitle("Chatter");
        primaryStage.setScene(new Scene(root, 637, 488));
        primaryStage.setResizable(false);
        primaryStage.show();

        //marked for deletion
        //moved to a scenebuilder method onEnterPress
        /*
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
        */
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
    
    //the logout button is for debugging right now
    @FXML
    public void logoutOnClick(){
    	ObservableList<String> test = FXCollections.
    			<String>observableArrayList("Test1", "Test2");
    	chatListView.getItems().setAll(test);
    }
    
    @FXML
    public void messageBoxOnKeyPress(KeyEvent event) {
    	if(event.getCode() == KeyCode.SHIFT){
    		shiftPressed = true;
    	}
    	
    	else if(event.getCode() == KeyCode.ENTER && shiftPressed == true){
    		messageBox.appendText("\n");
    	}
    	
    	else if(event.getCode() == KeyCode.ENTER && shiftPressed == false){
        	if (enterPressed == false){
        		enterPressed = true;
        		String text = messageBox.getText();
        		convoBox.appendText(username + ": " + text +"\n");
        		messageBox.setText("");
        	}
        	else{
        		messageBox.setText("");
        	}
        }
    }
    
    @FXML
    public void messageBoxOnKeyRelease(KeyEvent event){
    	if(event.getCode() == KeyCode.SHIFT){
    		shiftPressed = false;
    	}
    	
    	if(event.getCode() == KeyCode.ENTER && shiftPressed == false){
        	enterPressed = false;
    		messageBox.setText("");
    	}
    }
}
