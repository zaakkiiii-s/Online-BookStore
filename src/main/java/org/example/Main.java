package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        primaryStage.setTitle("BookStore Login");
        primaryStage.setScene(new Scene(root, 350, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);  // This launches the GUI
    }

}
