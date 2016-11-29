package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

/**
 * Created by Ali Ziyaan Momin on 11/25/2016.
 */
public class Server extends Observable {

    public HashMap<String, PrintWriter> individualPrinters = new HashMap<>();

    public HashMap<String, ClientObserver> onlineUsers = new HashMap<>();

    public HashMap<String, ChatObserver> currentChats = new HashMap<>();

    public HashMap<String, String> historyOfChats = new HashMap<>();
    
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
        while(true){
            Socket clientSocket = serverSocket.accept();
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
            ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
            BufferedReader tempReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (true){
                if((message = tempReader.readLine())!= null){
                    individualPrinters.put(message, printWriter);
                    onlineUsers.put(message, writer);
                    System.out.println(message);
                    break;
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
                        	//should notify the creator that the name already exists
                        } else {
                            c = new ChatObserver();
                            currentChats.put(chat, c);
                            historyOfChats.put(chat, null);
                            String[] userlist = message.split(";", 3)[3].split(";"); //lol
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
					}
					else if(split[1].equals("rename")){ //rename existing chat
						if(currentChats.containsKey(chat)){
							String newname = split[3];
							c = currentChats.get(chat);
							currentChats.remove(chat);
							currentChats.put(newname, c);
						}
			
					}
					else if(split[1].equals("add")){ //add n users to chat
						if(currentChats.containsKey(chat)){
							c = currentChats.get(chat);	
                            String[] userlist = message.split(";", 3)[3].split(";"); //lol
							for(String user : userlist){
								if(c.usersInChat.contains(user)){
									//complain somehow
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
                        String[] userlist = message.split(";", 3)[3].split(";"); //lol
						for(String user : userlist){
							if(c.usersInChat.contains(user)){
								c.usersInChat.remove(user);
                                c.deleteObserver((onlineUsers.get(user)));
							}
							else{
								//complain somehow
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
				else if(split[0].equals("@MESSAGE")){

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
        	
        	@USER;{login, logout, register, rename, add, remove};etc
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
                    m = message.split(";");
                    
                    
                    if (m[0].equals("@CHATS")) {
                        if (currentChats.containsKey(m[1])) {

                        } else {
                            ChatObserver c = new ChatObserver();
                            currentChats.put(m[1], c);
                            historyOfChats.put(m[1], null);
                            int numOfUsers = m.length - 2;
                            for (int i = 0; i < numOfUsers; i++) {                            	
                                c.usersInChat.add(m[i + 2]);
                                c.addObserver(onlineUsers.get(m[i + 2]));
                            }
                            //c.changed();
                            //c.notifyObservers("@CHATS");
                        }
                    }
                    else if(onlineUsers.containsKey(m[0])) {
                        if (currentChats.containsKey(m[1])) {
                            if (m[2].equals("/leave")) {
                                ChatObserver a = currentChats.get(m[1]);
                                a.usersInChat.remove(m[0]);
                                a.deleteObserver(onlineUsers.get(m[0]));
                            } else if (m[2].equals("/history")) {
                                PrintWriter p = individualPrinters.get(m[0]);
                                p.println(historyOfChats.get(m[1]));
                                p.flush();
                            } else {
                                ChatObserver a = currentChats.get(m[1]);
                                a.changed();
                                String outgoing = m[0] + ": " + m[2];
                                a.notifyObservers(message);
                                historyOfChats.replace(m[1], historyOfChats.get(m[1]) + "\n" + outgoing);
                                a.unChanged();
                            }
                        }                         }
                    }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
