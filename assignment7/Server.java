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
                                a.notifyObservers(m[0] + ": " + m[2]);
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
