package assignment7;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextArea messageBox = new TextArea();

    @FXML
    TextField hostAddress = new TextField();

    @FXML
    TextField hostPort = new TextField();

    @FXML
    TextField username = new TextField();

    @FXML
    PasswordField password = new PasswordField();

    @FXML
    TextArea mainBody = new TextArea();

    @FXML
    Button connect = new Button();

    @FXML
    Button login = new Button();

    @FXML
    Button send = new Button();


    public void button(){
        String text = messageBox.getText();
        mainBody.setText(mainBody.getText() + text);
        System.out.println("button pressed!");
        messageBox.clear();
    }


    public void ebutton(String text){
        //Text t1 = new Text("button pressed!button pressed!button pressed!button pressed!button pressed!button pressed!");

        mainBody.setText(mainBody.getText() + text);
        System.out.println("button pressed!");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    String text = messageBox.getText();
                    ebutton(text);
                    messageBox.setText("");
                }
            }
        });

    }
}
