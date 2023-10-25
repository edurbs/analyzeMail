package br.com.medeirosecia.analyzemail.gui;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import br.com.medeirosecia.analyzemail.domain.service.email.AnalyzeInbox;
import br.com.medeirosecia.analyzemail.infra.email.EmailProvider;
import br.com.medeirosecia.analyzemail.infra.email.GmailProvider;
import br.com.medeirosecia.analyzemail.infra.email.OutlookProvider;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;
import br.com.medeirosecia.analyzemail.infra.filesystem.ConfigFile;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class GuiFxController implements Initializable {

 

    @FXML
    private ToggleGroup emailProviderGroup;
    @FXML
    private RadioButton toggleGmailProvider;
    @FXML
    private RadioButton toggleOutlookProvider;

    @FXML
    private Button buttonSearchFolder;
    @FXML
    private Button buttonSearchCredential;

    @FXML
    private TextField textFieldPathCredentials;
    @FXML
    private TextField textFieldPathFolder;

    @FXML
    private Button buttonStop;
    @FXML
    private Button buttonStart;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label labelProgress;

    @FXML
    private ToggleGroup whichMessages;
    @FXML
    private RadioButton toggleAllMessages;
    @FXML
    private RadioButton toggleOnlyNotAnalized;

    private BaseFolders baseFolders = new BaseFolders();
    private Thread thread;
    private ConfigFile configFile;
    private Map<Toggle, EmailProvider> emailProvidersMap;

    @FXML
    void buttonSearchFolderClicked(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecione a pasta raiz para armazenar os arquivos");
        File folderSelected = directoryChooser.showDialog(null);
        this.textFieldPathFolder.setText(folderSelected.getAbsolutePath());

        this.baseFolders.setBaseFolder(this.textFieldPathFolder.getText());
        this.configFile.setBaseFolder(this.textFieldPathFolder.getText());

    }

    @FXML
    void buttonCredentialClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione o arquivo de credenciais");
        File fileSelected = fileChooser.showOpenDialog(null);
        this.textFieldPathCredentials.setText(fileSelected.getAbsolutePath());

        this.baseFolders.setPathCredentials(this.textFieldPathCredentials.getText());
        this.configFile.setCredentialsFilePath(this.textFieldPathCredentials.getText());

    }

    @FXML
    void buttonStartClicked(ActionEvent event) {

        this.buttonStart.setDisable(true);
        this.buttonStop.setDisable(false);

        Toggle toggleEmailProviderSelected = emailProviderGroup.getSelectedToggle();
        EmailProvider emailProviderSelected = this.emailProvidersMap.get(toggleEmailProviderSelected);
        emailProviderSelected.setCredentialsFile(this.baseFolders.getPathCredentials());

        Toggle toggleSelectedWhichMessages = whichMessages.getSelectedToggle();
        boolean getAllMessages = toggleSelectedWhichMessages == toggleAllMessages;

        this.configFile.setEmailProvider(emailProviderSelected.getClass().getSimpleName().toLowerCase());
    

        Task<Void> task = new AnalyzeInbox(baseFolders, emailProviderSelected, getAllMessages);

        task.setOnFailed(w -> {

            this.restartButtons();            
            w.getSource().getException().printStackTrace();
        });

        task.setOnSucceeded(w -> {

            this.restartButtons();            
        });

        progressBar.progressProperty().bind(task.progressProperty());
        labelProgress.textProperty().bind(task.messageProperty());

        this.thread = new Thread(task);
        this.thread.start();
    }

    public void restartButtons() {
        this.buttonStart.setDisable(false);
        this.buttonStop.setDisable(true);
        progressBar.progressProperty().unbind();
        labelProgress.textProperty().unbind();
        this.progressBar.setProgress(0);
    }

    @FXML
    void buttonStopClicked(ActionEvent event) {

        if (this.thread != null && this.thread.isAlive()) {

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
        

        this.configFile = new ConfigFile();

        this.textFieldPathCredentials.setText(this.configFile.getCredentialsFilePath());
        this.textFieldPathFolder.setText(this.configFile.getBaseFolder());

        this.baseFolders.setBaseFolder(this.textFieldPathFolder.getText());
        this.baseFolders.setPathCredentials(this.textFieldPathCredentials.getText());

        this.emailProvidersMap = new HashMap<>();

        EmailProvider gmailProvider = new GmailProvider();
        EmailProvider outlookProvider = new OutlookProvider();
        emailProvidersMap.put(this.toggleGmailProvider, gmailProvider);
        emailProvidersMap.put(this.toggleOutlookProvider, outlookProvider);

        String emailProviderFromConfig = this.configFile.getEmailProvider();

        if (emailProviderFromConfig != null) {
            String config = emailProviderFromConfig.toLowerCase();
            emailProvidersMap.forEach((key, value) -> {

                if (value.getClass().getSimpleName() != null
                        && (value.getClass().getSimpleName().toLowerCase().contains(config))) {
                    key.setSelected(true);
                }

            });

        }

        restartButtons();

    }

}