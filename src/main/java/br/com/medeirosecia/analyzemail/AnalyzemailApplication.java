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

// TODO planilha com notas de serviço
// TODO pasta padrão: C:\Senior\Sapiens\medeiros\email\pdf
// TODO ??? renomeando os arquivos PDF  de boletos para o padrão "boleto_email_1", "boleto_email_2", "boleto_email_3" ou renomear com a linha digitavel
// TODO ??? E renomeando os arquivos PDF  de nfe para o padrão "nfe_email_1", "nfe_email_2", "nfe_email_3"
// TODO ??? E renomeando os arquivos PDF  de nfs para o padrão "nfs_email_1", "nfs_email_2", "nfs_email_3"
// TODO ??? renomeando os arquivos PDF  de outros para o padrão "outros_email_1", "outros_email_2", "outros_email_3"

