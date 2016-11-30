package assignment7;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.scene.control.ScrollPane;

import javafx.collections.FXCollections;

/**
 * Created by Ali Ziyaan Momin on 11/25/2016.
 */
public class Client extends Application {
	private String hostIPAddress = "127.0.0.1";
	private int hostPortNumber = 5000;
	private String username = "quinn";
	private String password = "pswd";

	private static BufferedReader reader;
	private static PrintWriter writer;
	public static Object getConvoBox;

	public ScrollPane chatListPane;

	public Parent root;

	// ------------------------------- GUI Components:
	// ----------------------------------------
	@FXML
	private Button logout;
	@FXML
	public TextArea convoBox;
	@FXML
	private TextArea messageBox;
	@FXML
	private ListView<String> chatListView;
	@FXML
	private ListView<String> personListView;
	@FXML
	private ListView<String> friendsListView;
	@FXML
	private Button makeChat;
	@FXML
	private TextField chatName;
	// ----------------------------------------------------------------------------------------

	// -------------------------------- GUI Variables:
	// -----------------------------------------
	private boolean enterPressed = false;
	private boolean shiftPressed = false;

	// these two variables keep track of the users chats
	private static ArrayList<String> chats = new ArrayList<>();
	private static HashMap<String, String> chatText = new HashMap<>();
	public static String currentChat;

	private ArrayList<String> friendList = new ArrayList<String>();
	private ArrayList<String> selectedPeople = new ArrayList<>();

	// ----------------------------------------------------------------------------------------

	public static void main(String[] args) {
		try {
			new Client().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		launch(args);

	}

	@FXML
	public void initialize() {
		//chatListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		chatListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov, final String oldvalue, final String newvalue) {
				System.out.println(newvalue);
				currentChat = new String(newvalue);
				convoBox.setText(chatText.get(newvalue));
			}
		});
		
		ListChangeListener<String> multiSelection = new ListChangeListener<String>(){
			
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> changed) {
				selectedPeople.clear();
				for(String user : changed.getList()){
					selectedPeople.add(user);
				}
			}
		};
		friendsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		friendsListView.getSelectionModel().getSelectedItems().addListener(multiSelection);
		
	}

	private void initViewController() {
		// Not sure if we need this
	}
	
	public String getCurrentChat(){
		return currentChat;
	}

	// ---------------------------- This is for GUI functionality
	// ------------------------------
	@Override
	public void start(Stage primaryStage) throws Exception {
		root = FXMLLoader.load(getClass().getResource("mainscene.fxml"));
		primaryStage.setTitle("Chatter");
		primaryStage.setScene(new Scene(root, 637, 488));
		primaryStage.setResizable(false);
		primaryStage.show();

		setUpNetworking();
	}

	private void setUpNetworking() throws Exception {
		Socket socket = new Socket(hostIPAddress, hostPortNumber);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream());
		// Thread.sleep(20);
		//writer.println("@LOGIN;" + username);
		writer.println(username);
		// writer.println("@REGISTER;" + username);
		writer.flush();
		System.out.println("Networking established with " + hostIPAddress);
		Thread readerThread = new Thread(new IncomingReader(root));
		readerThread.start();
	}

	class IncomingReader implements Runnable {
		Parent root;

		public IncomingReader(Parent root) {
			this.root = root;
		}

		@Override
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					synchronized (this) {
						// TextArea n = (TextArea) root.lookup("#convoBox");
						// n.appendText(message + "\n");
						System.out.println(getCurrentChat());
						String[] m = message.split(";");
						if (chatText.containsKey(m[1])) {
							String oldMessage = chatText.get(m[1]);
							chatText.replace(m[1], oldMessage + m[0] + ": " + m[2] + "\n");
						}
						if (currentChat.equals(m[1])) {
							TextArea n = (TextArea) root.lookup("#convoBox");
							n.appendText(m[0] + ": " + m[2] + "\n");
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() throws Exception {
		// setUpNetworking();
		initViewController();
	}

	// private ArrayList<String> chats = new ArrayList<>();
	// private HashMap<String, String> chatText = new HashMap<>();
	// the logout button is for debugging right now
	private int f = 0;

	@FXML
	public void logoutOnClick() {
		/*f++;
		chatListView.getItems().add("test" + f);
		writer.println("@CHATS;" + "test" + f + ";" + username);
		writer.flush();
		chats.add("test" + f);
		chatText.put("test" + f, new String(""));
		*/
		f++;
		friendsListView.getItems().add("test" + f);
		
		
	}
	
	@FXML
	public void messageBoxOnKeyPress(KeyEvent event) {
		if (event.getCode() == KeyCode.SHIFT) {
			shiftPressed = true;
		}

		else if (event.getCode() == KeyCode.ENTER && shiftPressed == true) {
			messageBox.appendText("\n");
		}

		else if (event.getCode() == KeyCode.ENTER && shiftPressed == false) {
			if (enterPressed == false) {
				if (!messageBox.getText().equals("")) {
					// ex = "USR;CHAT;MSG"
					// server: "USR: MSG"
					enterPressed = true;
					String text = messageBox.getText();
					writer.println(username + ";" + currentChat + ";" + text);
					writer.flush();

					messageBox.setText("");
				}
			} else {
				messageBox.setText("");
			}
		}
	}

	@FXML
	public void messageBoxOnKeyRelease(KeyEvent event) {
		if (event.getCode() == KeyCode.SHIFT) {
			shiftPressed = false;
		}

		if (event.getCode() == KeyCode.ENTER && shiftPressed == false) {
			enterPressed = false;
			messageBox.setText("");
		}
	}
	
	@FXML
	public void makeChatOnClick(){
		String message = "";
		for(String s : selectedPeople){
			if(!message.equals("")){
				message = message + ";" + s;
			}
			else{
				message = s;
			}
		}
		if(!chatName.getText().isEmpty()){
			String c = chatName.getText();
			//writer.println("@CHATS;new;" + c + ";" + username + ";" + message);
			writer.println("@CHATS;" + c + ";" + message);
			writer.flush();
		}
	}
}
