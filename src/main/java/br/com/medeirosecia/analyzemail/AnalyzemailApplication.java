package br.com.medeirosecia.analyzemail;

import br.com.medeirosecia.analyzemail.gui.GuiFx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


//@SpringBootApplication
public class AnalyzemailApplication extends Application {

	public static void main(String[] args) {
		//SpringApplication.run(AnalyzemailApplication.class, args);
		// new AnalyzeGmailInbox();
		//System.setProperty("java.awt.headless", "false");
		//new Gui();
		launch(args);
		// var guifx = new GuiFx(args);
		// guifx.launch();
		//GuiFx.launch();


	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		// FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("guifx.fxml"));
		// Parent root = fxmlLoader.load();
		// Scene scene = new Scene(root);

		// primaryStage.setTitle("Analyze Mail");
		// primaryStage.setScene(scene);
		// primaryStage.show();

		new GuiFx(primaryStage);
		

	}

}
