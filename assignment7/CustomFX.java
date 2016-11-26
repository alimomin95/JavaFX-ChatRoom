package assignment7;


import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;


//custom widgets for GUI
/**
 * Created by Quinten Zambeck on 11/25/2016.
 */
public class CustomFX {
	
	//represents a list item in the userlist
	
	public class Chat extends TextField{
			private ArrayList<Person> people;
			private boolean newMessage;
			
			public Chat(){
				super();
			}
	}
}


class Person{
	private String username;
	private boolean newMessage;
	private boolean online;
	
	
	public Person(){};
	
	public Person(String username, boolean online){
		this.username = username;
		this.online = online;
	}
			
	public String toString(){
		return this.username;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public boolean getNewMessage(){
		return this.newMessage;
	}
	
	public boolean getOnline(){
		return this.online;
	}
}

class PersonCell extends HBox{
	Label label = new Label();
	Label onlineLabel = new Label();
	
	private String username;
	private boolean newMessage = false;
	private boolean online;
	
	public PersonCell(String username, boolean online){
		super();
		this.username = username;
		this.online = online;
		

		
		label.setText(username);
		label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

		//onlineLabel.setText((online?"G":"R"));
        onlineLabel.setText("Hi");

		this.setSpacing(10);
		this.getChildren().addAll(label, onlineLabel);
        

	}
			
	public String toString(){
		return this.username;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public boolean getNewMessage(){
		return this.newMessage;
	}
	
	public boolean getOnline(){
		return this.online;
	}

}

