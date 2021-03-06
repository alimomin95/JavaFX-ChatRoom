package assignment7;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.scene.control.ScrollPane;

import javafx.collections.FXCollections;


// quinn says: trying to make this show up on my github account


/**
 * Created by Ali Ziyaan Momin on 11/25/2016.
 */
public class ClientMain extends Application {
	//private static String hostIPAddress = "127.0.0.1";
	private static String hostIPAddress = "10.147.112.234";
	private static int hostPortNumber = 5000;
	private static String username;
	private static String password;

	private static BufferedReader reader;
	private static PrintWriter writer;
	public static Object getConvoBox;

	public ScrollPane chatListPane;

	public Parent root;
	
	public static Socket socket;
	public static Thread readerThread;

	// login GUI components
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private TextField ipField;
	@FXML
	private TextField portField;

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
	private ListView<String> onlineListView;
	@FXML
	private ListView<String> friendsListView;
	@FXML
	public Button contextButton;
	@FXML
	public TextField chatName;
	@FXML
	public TabPane tabPane;
	// ----------------------------------------------------------------------------------------

	// -------------------------------- GUI Variables:
	// -----------------------------------------
	private int state = 0;
	private boolean enterPressed = false;
	private boolean shiftPressed = false;
	
	private static Stage loginStage;

	// these two variables keep track of the users chats
	private static ArrayList<String> chats = new ArrayList<>();
	private static HashMap<String, String> chatText = new HashMap<>();
	public static String currentChat = "";
	public static String currentPerson = "";

	private ArrayList<String> friendList = new ArrayList<String>();
	private ArrayList<String> selectedPeople = new ArrayList<>();

	// ----------------------------------------------------------------------------------------

	public static void main(String[] args) {
		//username = args[0];
		try {
			new ClientMain().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		launch(args);

	}

	@FXML
	public void initialize() {
		try {
			// chatListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			
			onlineListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> ov, final String oldvalue,
						final String newvalue) {
					currentPerson = new String(newvalue);
				}
			});

			
			chatListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> ov, final String oldvalue,
						final String newvalue) {
					
					currentChat = new String(newvalue);
					//convoBox.clear();
					//for(String l : (chatText.get(newvalue)).split("\n")){
						//convoBox.appendText(l + "\n");
					//}
					convoBox.setText(chatText.get(newvalue));
				}
			});

			ListChangeListener<String> multiSelection = new ListChangeListener<String>() {
				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> changed) {
					selectedPeople.clear();
					for (String user : changed.getList()) {
						selectedPeople.add(user);
					}
				}
			};
			friendsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			friendsListView.getSelectionModel().getSelectedItems().addListener(multiSelection);
			
			
	        tabPane.getSelectionModel().selectedItemProperty().addListener((obs,ov,nv)->{
	        	String m = "";
	        	if(nv.getText().equals("Online")){
	        		m = "Add friend";
	        	}
	        	else if(nv.getText().equals("Friends")){
	        		m = "Make chat";
	        	}
	        	else if(nv.getText().equals("Chats")){
	        		m = "Delete chat";
	        	}
	            contextButton.setText(m);
	        });

			
		} catch (Exception e) {
		}

	}

	private void initViewController() {
		// Not sure if we need this
	}

	public String getCurrentChat() {
		return currentChat;
	}

	// ---------------------------- This is for GUI functionality
	// ------------------------------
	public void loggedIn(){
		
		//hostIPAddress = ((ipField.getText().isEmpty())?("127.0.0.1"):(ipField.getText()));
		//hostPortNumber = ((portField.getText().isEmpty())?(5000):(Integer.parseInt(portField.getText())));

		/*
		root = FXMLLoader.load(getClass().getResource("mainscene.fxml"));
		// Parent root2 =
		// FXMLLoader.load(getClass().getResource("loginscreen.fxml"));
		primaryStage.setTitle("Chatter");
		primaryStage.setScene(new Scene(root, 637, 488));
		primaryStage.setResizable(false);
		
		*/
		Stage primaryStage = new Stage();
		primaryStage.setTitle("Chatter");
		primaryStage.setScene(new Scene(root, 637, 488));
		primaryStage.setResizable(false);
		
		loginStage.close();
	
		
		primaryStage.show();
		}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		root = FXMLLoader.load(getClass().getResource("mainscene.fxml"));
		// Parent root2 =
		// FXMLLoader.load(getClass().getResource("loginscreen.fxml"));
		//////////////////
		
		loginStage = primaryStage;
		Parent root1 = FXMLLoader.load(getClass().getResource("loginscreen.fxml"));
		primaryStage.setTitle("Chatter");
		primaryStage.setScene(new Scene(root1, 637, 488));
		primaryStage.setResizable(false);
		primaryStage.show();
		setUpNetworking();
	}

	private void setUpNetworking() throws Exception {
		socket = new Socket(hostIPAddress, hostPortNumber);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream());
		// Thread.sleep(20);
		// writer.println("@LOGIN;" + username);
		//writer.println(username);
		// writer.println("@REGISTER;" + username);
		//writer.flush();
		System.out.println("Networking established with " + hostIPAddress);
		readerThread = new Thread(new IncomingReader(root));
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
						//System.out.println(message);
						if (message.charAt(0) == '@') {
							String[] split = message.split(";");
							String command = split[0];
							if (command.equals("@CHATS")) {
								String action = split[1];
								String users = split[3];
								if (action.equals("new")) {
									@SuppressWarnings("unchecked")
									ListView<String> n = (ListView<String>) root.lookup("#chatListViewID");
									String chat = split[2];
									javafx.application.Platform.runLater(() -> n.getItems().add(chat));
									chats.add(chat);
									chatText.put(chat, new String(""));
								}
							} else if (command.equals("@MESSAGE")) {
								String chat = split[1];
								String user = split[2];
								String servedMessage = message.split(";", 4)[3];
								String oldMessage = chatText.get(chat);
								chatText.replace(chat, oldMessage + user + ": " + servedMessage + "\n");
								if (currentChat.equals(chat)) {
									TextArea n = (TextArea) root.lookup("#convoBox");
									//n.setText(oldMessage + user + ": " + servedMessage + "\n");
									javafx.application.Platform.runLater(() -> n.appendText(user + ": " + servedMessage + "\n"));
									
								}
							} else if(command.equals("@USER")){
								String action = split[1];
								if(action.equals("online")){
									@SuppressWarnings("unchecked")
									ListView<String> n = (ListView<String>) root.lookup("#onlineUserList");
									javafx.application.Platform.runLater(() -> n.getItems().clear());
									for(String u : message.split(";", 3)[2].split(";")){
										if(!u.equals(username)){
											javafx.application.Platform.runLater(() -> n.getItems().add(u));
										}
									}
								}
								else if(action.equals("nowOnline")){
									String u = split[2];
									//System.out.println(u);
									@SuppressWarnings("unchecked")
									ListView<String> n = (ListView<String>) root.lookup("#onlineUserList");
									javafx.application.Platform.runLater(() -> n.getItems().add(u));
								}
								else if(action.equals("addfriend")){
									//System.out.println("adding: " + split[2]);
									@SuppressWarnings("unchecked")
									ListView<String> n = (ListView<String>) root.lookup("#friendListID");
									n.getItems().add(split[2]);
								}
								
							} else if (command.equals("@ERROR")) {
								String errormessage = split[1];
								//System.out.println(errormessage);
								//this adds the command to the javafx command queue ((I think??))
								Platform.runLater(() -> AlertBox.display("Error", errormessage));
								
							} else if (command.equals("@LOGIN")){
								String state = split[1];
								if (state.equals("successful")) {
									Platform.runLater(() -> loggedIn());
									writer.println("@USER;getOnlineUsers;" + username);
									writer.flush();
								} else if (state.equals("incorrectPassword")){
									//AlertBox reason -- incorrect password
									Platform.runLater(() -> AlertBox.display("Error", "Incorrect Password"));
								} else if (state.equals("failed")){
									//AlertBox reason -- need to register
									Platform.runLater(() -> AlertBox.display("Error", "Need to register"));
								}
							} else if (command.equals("@REGISTER")){
								String state = split[1];
								if (state.equals("successful")){
									//AlertBox reason -- registration successful
									Platform.runLater(() -> AlertBox.display("Success", "Registration Successful!"));
								} else if (state.equals("failed")){
									//AlertBox reason -- username already exists
									Platform.runLater(() -> AlertBox.display("Error", "Username already exists"));
								}
							}
							else if (command.equals("@SERVER")){
								if(split[1].equals("friends")){
									if(split.length > 2){
										String[] friendslist = message.split(";", 3)[2].split(";");
										@SuppressWarnings("unchecked")
										ListView<String> n = (ListView<String>) root.lookup("#friendListID");
										for(String u : friendslist){
											n.getItems().add(u);
										}
									}
								}
								else if(split[1].equals("history")){
									String c = split[2];
									String m = message.split(";", 4)[3];
		                            m = m.replace("%55", "\n");
									//System.out.println(Arrays.toString(message.split(";", 4)));
									//System.out.println("Message: " + m);
									chats.add(c);
									
									
									chatText.put(c, new String(m.trim()) + "\n");
									
									@SuppressWarnings("unchecked")
									ListView<String> chatlistview = (ListView<String>) root.lookup("#chatListViewID");
									javafx.application.Platform.runLater(() -> chatlistview.getItems().add(c));

									
									if(currentChat.equals(c)){
										TextArea o = (TextArea) root.lookup("#convoBox");
										//n.setText(oldMessage + user + ": " + servedMessage + "\n");
										javafx.application.Platform.runLater(() -> o.setText(chatText.get(c)));
										
									}
								}
							}
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

	@SuppressWarnings("deprecation")
	@FXML
	public void logoutOnClick() throws IOException {
		writer.println("@LOGOUT;" + username);
		writer.flush();
		readerThread.stop();
		socket.close();
		System.exit(0);
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
					writer.println("@MESSAGE;" + currentChat + ";" + username + ";" + text);
					// writer.println(username + ";" + currentChat + ";" +
					// text);
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
	public void loginOnClick(Event event) throws Exception {
		username = usernameField.getText();
		password = passwordField.getText();
		System.out.println(username + " " + password);
		writer.println("@LOGIN;" + username + ";" + password);
		writer.flush();
		//((Node) (event.getSource())).getScene().getWindow().hide();
		//loggedIn(new Stage());
		
	}

	@FXML
	public void registerOnClick() {
		username = usernameField.getText();
		password = passwordField.getText();
		writer.println("@REGISTER;" + username + ";" + password);
		writer.flush();
	}
	
	@FXML
	public void contextOnClick() {
		if(contextButton.getText().equals("Add friend")){
			friendsListView.getItems().add(currentPerson);
			writer.println("@USER;addfriend;" + username + ";" + currentPerson);
			writer.flush();
		}
		else if(contextButton.getText().equals("Make chat")){

			String message = "";
			for (String s : selectedPeople) {
				message = message + ";" + s;
			}
			if (!chatName.getText().isEmpty()) {
				String c = chatName.getText();
				writer.println("@CHATS;new;" + c + ";" + username + message);
				// writer.println("@CHATS;" + c + ";" + message);
				//writer.println(c);
				writer.flush();
			}
		}
	}


	
}
