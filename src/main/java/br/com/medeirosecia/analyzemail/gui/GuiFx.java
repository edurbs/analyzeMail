package br.com.medeirosecia.analyzemail.gui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GuiFx {

    public GuiFx(Stage primaryStage){
        FXMLLoader fxmlLoader = new FXMLLoader(GuiFx.class.getResource("/gui/guifx.fxml"));

		Parent root;
        try {
            root = fxmlLoader.load();
            Scene scene = new Scene(root);

            primaryStage.setTitle("Analyze Mail v0.6.0");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event -> onClose());
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onClose() {
        Platform.exit();
        System.exit(0);
    }

}
