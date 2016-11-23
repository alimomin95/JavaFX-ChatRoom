
public class Main {
	public static void main(String[] args){
		ChatServer s = new ChatServer();
		try {
			s.setUpNetworking();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
