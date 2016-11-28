package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

/**
 * Created by Ali Ziyaan Momin on 11/25/2016.
 */
public class Server extends Observable {

    public HashMap<String, ClientObserver> onlineUsers = new HashMap<>();

    public HashMap<String, ArrayList<ClientObserver>> currentChats;
    
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
            ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
            BufferedReader tempReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (true){
                if((message = tempReader.readLine())!= null){
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

            try{
                while ((message = reader.readLine())!= null){
                    System.out.println("Server read: " + message);
                    setChanged();
                    notifyObservers(message);
                    clearChanged();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
