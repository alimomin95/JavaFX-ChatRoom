package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.scene.control.TextArea;


//client is initialized with an output text field and an input field
//after initialize, you call setUpNetworking with an address and a port
public class Client {
	private Socket sock;
    private BufferedReader reader;
	private PrintWriter writer;
	private TextArea incoming;
	private TextArea outgoing;
	
	public Client(TextArea reader, TextArea writer){
		this.incoming = reader;
		this.outgoing = writer;
	}
	
	
	public void setUpNetworking(String address, int port){
		try {
			sock = new Socket(address, port);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			Thread readerThread = new Thread(new incomingReader());
			readerThread.start();
		}
		catch (IOException e) {
			System.out.println("cannot connect to server");
			return;
		}
	}
	
	public void closeNetworking(){
		try{
			sock.close();
		}
		catch(IOException e){
			System.out.println("Cannot disconnect");
		}
	}

	public void sendMessage(String message){
		writer.println(message);
		//for testing use this:
        //incoming.setText(incoming.getText() + message + "\n");
	}
	
	
	
	private class incomingReader implements Runnable {
		@Override
		public void run() {
			String message;
			try{
				while ((message = reader.readLine()) != null){
					incoming.appendText(message + "\n");
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}

