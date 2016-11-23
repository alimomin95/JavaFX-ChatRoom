package assignment7;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));

        Scene scene = new Scene(grid, 325, 275);

        Text scenetitle = new Text("Please Login: ");
        scenetitle.setFont(Font.font("Tahoma",FontWeight.NORMAL, 20));
        Label userName = new Label("Host Address:");
        TextField address = new TextField();
        Label port = new Label("Host Port: ");
        TextField portBox = new TextField();

        grid.add(scenetitle,0,0,2,1);
        grid.add(userName,0,1);
        grid.add(address, 1,1);
        grid.add(port,0,2);
        grid.add(portBox,1,2);

        grid.setGridLinesVisible(false);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn,1,4);

        final Text actiontarget = new Text();
        grid.add(actiontarget,1,6);

        portBox.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER)  {
                    primaryStage.close();
                    Parent root = null;
                    try {
                        root = FXMLLoader.load(getClass().getResource("client.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    primaryStage.setTitle("RSA Chat");
                    primaryStage.setScene(new Scene(root, 621, 469));
                    primaryStage.setResizable(false);
                    primaryStage.show();
                }
            }
        });

        btn.setOnAction(event ->
        {
            actiontarget.setFill(Color.FIREBRICK);
            actiontarget.setText("Sign in button pressed");
            primaryStage.close();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("client.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            primaryStage.setTitle("AQ Chat");
            primaryStage.setScene(new Scene(root, 621, 469));
            primaryStage.setResizable(false);
            primaryStage.show();

        });

        primaryStage.setTitle("JavaFX Main Form");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /**
     * @param args the command line arguments
     */
     public static void main(String[] args) {
         launch(args);
     }

}