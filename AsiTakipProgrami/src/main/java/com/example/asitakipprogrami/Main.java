package com.example.asitakipprogrami;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loginFXML = new FXMLLoader(Main.class.getResource("login_page.fxml"));
        Scene scene = new Scene(loginFXML.load(), 1345, 720);

        stage.setTitle("Giriş Yap");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}