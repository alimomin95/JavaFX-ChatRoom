package assignment7;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 621, 469));
        primaryStage.setResizable(false);
        primaryStage.show();

//        GridPane chat = new GridPane();
//        chat.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//
//        ColumnConstraints c1 = new ColumnConstraints();
//        c1.setPercentWidth(100);
//        chat.getColumnConstraints().add(c1);
//
//        for (int i = 0; i < 20; i++) {
//            Label chatMessage = new Label("Hi " + i);
//            chatMessage.getStyleClass().add("chat-bubble");
//            GridPane.setHalignment(chatMessage, i % 2 == 0 ? HPos.LEFT
//                    : HPos.RIGHT);
//            chat.addRow(i, chatMessage);
//        }
//
//        ScrollPane scroll = new ScrollPane(chat);
//        scroll.setFitToWidth(true);
//
//        Scene scene = new Scene(scroll, 500, 500);
//        scene.getStylesheets().add(getClass().getResource("Test.css").toExternalForm());
//        primaryStage.setScene(scene);
//        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
