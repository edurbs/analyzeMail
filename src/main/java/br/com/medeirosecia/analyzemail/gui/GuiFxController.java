package br.com.medeirosecia.analyzemail.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import br.com.medeirosecia.analyzemail.console.LocalConsole;
import br.com.medeirosecia.analyzemail.domain.service.gmail.AnalyzeGmailInbox;
import br.com.medeirosecia.analyzemail.infra.filesystem.ConfigFile;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class GuiFxController implements Initializable{

    @FXML
    private CheckBox debug;

    @FXML
    private TextArea debugTextArea;

    @FXML
    private ToggleGroup emailProvider;

    @FXML
    private RadioButton gmailProvider;

    @FXML
    private Button buttonSearchFolder;

    @FXML
    private TextField pathCredentials;

    @FXML
    private Spinner<Integer> minutes;

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

    private LocalConsole console = new LocalConsole();
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
        if(this.pathFolder.getText().isBlank() || this.pathCredentials.getText().isBlank()) {
            this.debugTextArea.appendText("\nPor favor, selecione as credenciais e a pasta raiz");
            return;
        }       
        

        this.buttonStart.setDisable(true);
        this.buttonStop.setDisable(false);
        this.progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        this.debugTextArea.appendText("\nIniciando...");

        

        Runnable analyzeGmailInbox = new AnalyzeGmailInbox(this.console, this.localFileSystem, this.debugTextArea);

        this.thread = new Thread(()->{

            analyzeGmailInbox.run();

            Platform.runLater(()->{
                restartButtons();
            });
            
        });


        this.thread.start();
        
   
    }

    public void restartButtons(){
        this.buttonStart.setDisable(false);
        this.buttonStop.setDisable(true);
        this.progressBar.setProgress(0);
    }

    
    @FXML
    void buttonStopClicked(ActionEvent event) {        
        this.debugTextArea.appendText("\nFinalizando...");
        restartButtons();

        if(this.thread != null) {
            try {
                //this.thread.join();
                //this.thread.interrupt();
                this.thread.stop();
                this.thread=null;
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

        this.minutes.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 30));
        
    }




}