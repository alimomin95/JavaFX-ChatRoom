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
	private static String hostIPAddress = "127.0.0.1";
	private static int hostPortNumber = 5000;
	private static String username;
	private static String password;

	private static BufferedReader reader;
	private static PrintWriter writer;
	public static Object getConvoBox;

	public ScrollPane chatListPane;

	public Parent root;

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
	public Tab onlineTab;
	@FXML
	public Tab friendsTab;
	@FXML
	public Tab chatsTab;
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
			new Client().run();
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
		Socket socket = new Socket(hostIPAddress, hostPortNumber);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream());
		// Thread.sleep(20);
		// writer.println("@LOGIN;" + username);
		//writer.println(username);
		// writer.println("@REGISTER;" + username);
		//writer.flush();
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
						System.out.println(message);
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
									n.appendText(user + ": " + servedMessage + "\n");
								}
							} else if(command.equals("@USER")){
								String action = split[1];
								if(action.equals("online")){
									@SuppressWarnings("unchecked")
									ListView<String> n = (ListView<String>) root.lookup("#onlineUserList");
									for(String u : message.split(";", 3)[2].split(";")){
										if(!u.equals(username)){
											n.getItems().add(u);
										}
									}
								}
								else if(action.equals("nowOnline")){
									String u = split[2];
									System.out.println(u);
									@SuppressWarnings("unchecked")
									ListView<String> n = (ListView<String>) root.lookup("#onlineUserList");
									n.getItems().add(u);
								}
								
							} else if (command.equals("@ERROR")) {
								String errormessage = split[1];
								System.out.println(errormessage);
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
		/*
		 * f++; chatListView.getItems().add("test" + f);
		 * writer.println("@CHATS;" + "test" + f + ";" + username);
		 * writer.flush(); chats.add("test" + f); chatText.put("test" + f, new
		 * String(""));
		 */
		f++;
		friendsListView.getItems().add(chatName.getText());

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
		if(contextButton.getText().equals("Add Friend")){
			friendsListView.getItems().add(currentPerson);
			writer.println("@USER;addfriend;" + currentPerson);
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
