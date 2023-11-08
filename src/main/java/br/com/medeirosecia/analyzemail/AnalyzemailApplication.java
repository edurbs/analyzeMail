package br.com.medeirosecia.analyzemail;

import br.com.medeirosecia.analyzemail.gui.GuiFx;
import javafx.application.Application;
import javafx.stage.Stage;



public class AnalyzemailApplication extends Application {

	public static void main(String[] args) {

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		new GuiFx(primaryStage);


	}

}
