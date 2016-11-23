import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class ChatServer {
	private ArrayList<PrintWriter> clientOutputStreams;
	
	public void setUpNetworking() throws Exception {
		clientOutputStreams = new ArrayList<PrintWriter>();
		
		ServerSocket serverSock = new ServerSocket(5000);
		while (true){
			Socket clientSocket = serverSock.accept();
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			clientOutputStreams.add(writer);
			System.out.println("Got a connection");
		}
	}
	
	private class ClientHandler implements Runnable {
		private BufferedReader reader;
		
		public ClientHandler(Socket clientSocket) throws IOException {
			Socket sock = clientSocket;
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}
		
		public void run() {
			String message = null;
			while (true){
				try {
					message = reader.readLine();
				} 
				catch (IOException e) {
					message = null;
				}
				if(message != null){
					notifyClients(message);
				}
			}
		}
		
		private void notifyClients(String message){
			for(PrintWriter writer : clientOutputStreams){
				writer.println(message);
				writer.flush();
			}
		}
	}
}
