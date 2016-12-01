//test
package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

/**
 * Created by Ali Ziyaan Momin on 11/25/2016.
 */
public class Server extends Observable {

    public HashMap<String, String> loginMap = new HashMap<>();

    public HashMap<String, PrintWriter> individualPrinters = new HashMap<>();

    public HashMap<String, ClientObserver> onlineUsers = new HashMap<>();

    public HashMap<String, ChatObserver> currentChats = new HashMap<>();

    public HashMap<String, String> historyOfChats = new HashMap<>();

    public HashMap<String, ArrayList<String>> usersChats = new HashMap<>();
    
    public static void main(String[] args){
        try{
            new Server().setUpNetworking();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpNetworking() throws Exception{
        ServerSocket serverSocket = new ServerSocket(5000);
        String message;
        loginMap.put("quinn", "password");
        while(true){
            Socket clientSocket = serverSocket.accept();
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
            ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
            BufferedReader tempReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //User sends commands to login or register
            //login command: @LOGIN;{username};{password}
            //register command: @REGISTER;{username};{password}

            while((message = tempReader.readLine())!= null){
            	System.out.println(message);
                String[] split;
                if(message.charAt(0) == '@'){
                    split = message.split(";");
                    String command = split[0];
                    String username = split[1];
                    String password = split[2];
                    if(command.equals("@LOGIN")){
                        if(loginMap.containsKey(username)){
                            if(loginMap.get(username).equals(password)){
                                printWriter.println("@LOGIN;successful");
                                printWriter.flush();
                                System.out.println(username);
                                individualPrinters.put(username, printWriter);
                                onlineUsers.put(username, writer);
                                //Below notifies the current logged in users that a new user came online
                                String userlist = "@USER;nowOnline;" + username;
                                this.setChanged();
                                this.notifyObservers(userlist);
                                this.clearChanged();
                                break;
                            }
                            else{
                                printWriter.println("@LOGIN;incorrectPassword");
                                printWriter.flush();
                            }
                        }
                        else{
                            printWriter.println("@LOGIN;failed");
                            printWriter.flush();
                        }
                    }
                    else if(command.equals("@REGISTER")){
                        if(loginMap.containsKey(username)){
                            printWriter.println("@REGISTER;failed");
                            printWriter.flush();
                        }
                        else{
                            loginMap.put(username, password);
                            printWriter.println("@REGISTER;successful");
                            printWriter.flush();
                        }
                    }
                }
            }

            Thread t = new Thread(new ClientHandler(clientSocket));
            t.start();
            this.addObserver(writer);
            System.out.println("got a connection");
        }
    }

    class ClientHandler implements Runnable{

        private BufferedReader reader;

        public ClientHandler(Socket clientSocket){
            Socket socket = clientSocket;
            try{
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        /*
        	commands:
        	@CHATS;{new, delete, rename, add, remove};{chat};[username];etc
        	@CHATS;{new};[chat];[username];[usr1];[usr2]...
        	@CHATS;{delete};[chat];[username]
        	@CHATS;{rename};{chat};[username];{newname}
        	@CHATS;{add};{chat};[username];[usr1];[usr2]...
        	@CHATS;{remove};{chat};[username];[usr1];[usr2]...

        	@USER;{login, logout, register, rename, add, remove};etc
        	@USER;{login};username;password
        	@USER;{logout};usernmae;password
        	@USER;{register};username;password
        	@USER;{rename};username;password;newname
        	@USER;{addfriend};username;friend
        	@USER;{removefriend};username;friend
            @USER;{OnlineUsers};username;usr1;usr2...
        	@USER;{getOnlineUsers};username

        	@MESSAGE;{chat};{user};{message}

        */
        
        private void processMessage(String message){
			String[] split;
			if(message.charAt(0) == '@'){
				split = message.split(";");
	        	//@CHATS;{new, delete, rename, add, remove};{chat};etc
				if(split[0].equals("@CHATS")){
					String chat = split[2];
					ChatObserver c = null;
					if(split[1].equals("new")){ //make new chat
                        if (currentChats.containsKey(chat)){
                        	//notify the creator that the name already exists
                            String user = split[3];
                            PrintWriter w = individualPrinters.get(user);
                            w.println("@ERROR;Chat already exists");
                            w.flush();
                            c = null;
                        } else {
                        	System.out.println("new chat created");
                            c = new ChatObserver();
                            currentChats.put(chat, c);
                            historyOfChats.put(chat, null);
                            System.out.println(Arrays.toString(message.split(";", 4)));
                            String[] userlist = message.split(";", 4)[3].split(";"); //lol
                            for(String user : userlist){
                                c.usersInChat.add(user);
                                c.addObserver(onlineUsers.get(user));
                            }
                        }
					}
					else if(split[1].equals("delete")){ //delete existing chat
						if(currentChats.containsKey(chat)){
							c = currentChats.get(chat);
							currentChats.remove(chat);
						}
						else{
						    //notify the user that chat doesn't exist
                            String user = split[3];
                            PrintWriter w = individualPrinters.get(user);
                            w.println("@ERROR;Chat does not exist");
                            w.flush();
                            c = null;
                        }
					}
					else if(split[1].equals("rename")){ //rename existing chat
						if(currentChats.containsKey(chat)){
							String newname = split[3];
							c = currentChats.get(chat);
							currentChats.remove(chat);
							currentChats.put(newname, c);
						}
						else{
						    //notify the user that the chat doesn't exist
                            String user = split[3];
                            PrintWriter w = individualPrinters.get(user);
                            w.println("@ERROR;Chat does not exist");
                            w.flush();
                            c = null;
                        }
			
					}
					else if(split[1].equals("add")){ //add n users to chat
						if(currentChats.containsKey(chat)){
							c = currentChats.get(chat);	
                            String[] userlist = message.split(";", 4)[3].split(";"); //lol
							for(String user : userlist){
								if(c.usersInChat.contains(user)){
									//notify user that user is already in chat
                                    String userName = split[3];
                                    PrintWriter w = individualPrinters.get(userName);
                                    w.println("@ERROR;User is already in chat");
                                    w.flush();
                                    c = null;
								}
								else{
									c.usersInChat.add(user);
	                                c.addObserver(onlineUsers.get(user));
								}
							}
						}

					}
					else if(split[1].equals("remove")){ //remove n users from chat
						c = currentChats.get(chat);	
                        String[] userlist = message.split(";", 4)[3].split(";"); //lol
						for(String user : userlist){
							if(c.usersInChat.contains(user)){
								c.usersInChat.remove(user);
                                c.deleteObserver((onlineUsers.get(user)));
							}
							else{
								/*
								//complain somehow
                                String userName = split[3];
                                PrintWriter w = individualPrinters.get(userName);
                                w.println("@ERROR; ");
                                w.flush();
                                c = null;
                                */
							}
                        }
                    }
					if(c != null){
						c.changed();
						c.notifyObservers(message); //tell included users that a new chat was made
						c.unChanged();
					}
				}
				else if(split[0].equals("@USER")){
					String username = split[2];
					if(split[1].equals("logout")){
			
					}
					else if(split[1].equals("rename")){
			
					}
					else if(split[1].equals("add")){
			
					}
					else if(split[1].equals("remove")){
			
					}
					else if(split[1].equals("getOnlineUsers")){
					    String userlist = "@USER;online";
					    List<String> l = new ArrayList<>(onlineUsers.keySet());
					    int length = l.size();
					    for(int i = 0; i < length; i ++){
					        String temp = userlist + ";" + l.get(i);
					        userlist = temp;
                        }
                        System.out.println("Username: " + username);
                        PrintWriter w = individualPrinters.get(username);
                        w.println(userlist);
                        w.flush();
                    }
				}
				else if(split[0].equals("@MESSAGE")){
                    if(currentChats.containsKey(split[1])){
                        ChatObserver c = currentChats.get(split[1]);
                        c.changed();
                        c.notifyObservers(message);
                        c.unChanged();
                    }
				}
			}

        }

        /*
        	commands:
        	@CHATS;{new, delete, rename, add, remove};{chat};etc
        	@CHATS;{new};[chat];[usr1];[usr2]...
        	@CHATS;{delete};[chat]
        	@CHATS;{rename};{chat};{newname}
        	@CHATS;{add};{chat};[usr1];[usr2]...
        	@CHATS;{remove};{chat};[usr1];[usr2]...
        	
        	@USER;{login, logout, register, rename, add, remove};{username};etc
        	@USER;{login};username;password
        	@USER;{logout};usernmae;password
        	@USER;{register};username;password
        	@USER;{rename};username;password;newname
        	@USER;{addfriend};username;friend
        	@USER;{removefriend};username;friend
        	
        	
        	@MESSAGE;{chat};{user};{message}
        
        */
        
        /* Skeleton of command processor
         * 	message = reader.readLine()
			String split;
			if(message.getAt(0) == '@'){
				split = message.split(";")
				if(split[0].equals("@CHATS")){
					if(split[1].equals("new")){
						
					}
					else if(split[1].equals("delete")){
			
					}
					else if(split[1].equals("rename")){
			
					}
					else if(split[1].equals("add")){
			
					}
					else if(split[1].equals("remove")){
			
					}
				}
				else if(split[0].eqauls("@USER")){
					if(split[1].equals("login")){
			
					}
					else if(split[1].equals("logout")){
			
					}
					else if(split[1].equals("register")){
			
					}
					else if(split[1].equals("rename")){
			
					}
					else if(split[1].equals("add")){
			
					}
					else if(split[1].equals("remove")){
			
					}
				}
			}
         * 
         * 
         */
        @Override
        public void run() {

            String message;

            String[] m;

            try{
                while ((message = reader.readLine())!= null){
                    System.out.println("Server read: " + message);

                    processMessage(message);

//                    m = message.split(";");
//
//
//                    if (m[0].equals("@CHATS")) {
//                        if (currentChats.containsKey(m[1])) {
//
//                        } else {
//                            ChatObserver c = new ChatObserver();
//                            currentChats.put(m[1], c);
//                            historyOfChats.put(m[1], null);
//                            int numOfUsers = m.length - 2;
//                            for (int i = 0; i < numOfUsers; i++) {
//                                c.usersInChat.add(m[i + 2]);
//                                c.addObserver(onlineUsers.get(m[i + 2]));
//                            }
//                            //c.changed();
//                            //c.notifyObservers("@CHATS");
//                        }
//                    }
//                    else if(onlineUsers.containsKey(m[0])) {
//                        if (currentChats.containsKey(m[1])) {
//                            if (m[2].equals("/leave")) {
//                                ChatObserver a = currentChats.get(m[1]);
//                                a.usersInChat.remove(m[0]);
//                                a.deleteObserver(onlineUsers.get(m[0]));
//                            } else if (m[2].equals("/history")) {
//                                PrintWriter p = individualPrinters.get(m[0]);
//                                p.println(historyOfChats.get(m[1]));
//                                p.flush();
//                            } else {
//                                ChatObserver a = currentChats.get(m[1]);
//                                a.changed();
//                                String outgoing = m[0] + ": " + m[2];
//                                a.notifyObservers(message);
//                                historyOfChats.replace(m[1], historyOfChats.get(m[1]) + "\n" + outgoing);
//                                a.unChanged();
//                            }
//                        }                         }
                    }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
