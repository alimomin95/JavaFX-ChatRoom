package assignment7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {
	BufferedReader reader;
	PrintWriter writer;
	
	
	
	private void setUpNetworking(String address, int port) throws Exception {
		Socket sock = new Socket(address, port);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}
}

class incomingReader implements Runnable {
	@Override
	public void run() {
		String message;
		while((message = reader.readLine()) != null) {
			incoming.append(message + "\n");
		}
	}
