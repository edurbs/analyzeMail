package br.com.medeirosecia.analyzemail.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import br.com.medeirosecia.analyzemail.domain.service.gmail.AnalyzeGmailInbox;
import br.com.medeirosecia.analyzemail.infra.filesystem.ConfigFile;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class GuiFxController implements Initializable {

  
    @FXML
    private ToggleGroup emailProvider;

    @FXML
    private RadioButton gmailProvider;

    @FXML
    private Button buttonSearchFolder;

    @FXML
    private TextField pathCredentials;

    @FXML
    private RadioButton outlookProvider;

    @FXML
    private TextField pathFolder;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button buttonStop;
    
    @FXML
    private Button buttonStart;

    @FXML
    private Label labelProgress;

    private LocalFileSystem localFileSystem = new LocalFileSystem();
    private Thread thread;
    private ConfigFile configFile;

    @FXML
    void buttonSearchFolderClicked(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecione a pasta raiz para armazenar os arquivos");
        File folderSelected = directoryChooser.showDialog(null);
        this.pathFolder.setText(folderSelected.getAbsolutePath());

        this.localFileSystem.setBaseFolder(this.pathFolder.getText());
        this.configFile.setBaseFolder(this.pathFolder.getText());
        
    }
   
    @FXML
    void buttonCredentialClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();        
        fileChooser.setTitle("Selecione o arquivo de credenciais");
        File fileSelected = fileChooser.showOpenDialog(null);
        this.pathCredentials.setText(fileSelected.getAbsolutePath());
        
        this.localFileSystem.setPathCredentials(this.pathCredentials.getText());
        this.configFile.setCredentialsFilePath(this.pathCredentials.getText());
                
    }

    @FXML
    void buttonStartClicked(ActionEvent event) {
        

        this.buttonStart.setDisable(true);
        this.buttonStop.setDisable(false);

        
       Task<Void> task = new AnalyzeGmailInbox(this.localFileSystem);
       task.setOnFailed(w -> {

        this.restartButtons();
        this.labelProgress.setText("Finalizado com erros");
        w.getSource().getException().printStackTrace();
       });

       task.setOnSucceeded(w->{

        this.restartButtons();
        this.labelProgress.setText("Finalizado");
       });

       progressBar.progressProperty().bind(task.progressProperty());
       labelProgress.textProperty().bind(task.messageProperty());
       
       

       this.thread = new Thread(task);
       this.thread.start();

        
   
    }



    public void restartButtons(){
        this.buttonStart.setDisable(false);
        this.buttonStop.setDisable(true);
        progressBar.progressProperty().unbind();
        labelProgress.textProperty().unbind();
        this.progressBar.setProgress(0);
    }

    
    @FXML
    void buttonStopClicked(ActionEvent event) {        
       
        restartButtons();

        if(this.thread != null) {
            try {
                this.thread.interrupt();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        this.configFile = new ConfigFile();
       
        this.pathCredentials.setText(this.configFile.getCredentialsFilePath());        
        this.pathFolder.setText(this.configFile.getBaseFolder());

        this.localFileSystem.setBaseFolder(this.pathFolder.getText());
        this.localFileSystem.setPathCredentials(this.pathCredentials.getText());

        restartButtons();

    }




}