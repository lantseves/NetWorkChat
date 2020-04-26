package ru.gb.jtwo.chat.client.giu.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientGUIFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
