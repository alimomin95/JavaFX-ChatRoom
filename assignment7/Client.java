package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;



//client is initialized with an output text field and an input field
//after initialize, you call setUpNetworking with an address and a port
public class Client {
	private Socket sock;
    private BufferedReader reader;
	private PrintWriter writer;
	private TextField incoming;
	private TextArea outgoing;
	
	public Client(TextField reader, TextArea writer){
		this.incoming = reader;
		this.outgoing = writer;
	}
	
	
	public void setUpNetworking(String address, int port) throws Exception {
		sock = new Socket(address, port);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		Thread readerThread = new Thread(new incomingReader());
		readerThread.start();
	}
	
	public void closeNetworking() throws IOException{
		sock.close();
	}

	public void sendMessage(String message){
        incoming.setText(incoming.getText() + message + "\n");
	}
	
	
	
	private class incomingReader implements Runnable {
		@Override
		public void run() {
			String message = null;
			while(true){
				try{
					message = reader.readLine();
			        incoming.setText(incoming.getText() + message + "\n");
				} catch (IOException e) {
				
				}				
			}
		}
	}
}

