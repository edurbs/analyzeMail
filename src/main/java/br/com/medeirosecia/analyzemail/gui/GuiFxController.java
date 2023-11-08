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

    @FXML
    private Button buttonSearchCnpj;
    @FXML
    private TextField textFieldPathCnpj;

    private BaseFolders baseFolders = new BaseFolders();
    private Thread thread;
    private ConfigFile configFile;
    private Map<Toggle, EmailProvider> emailProvidersMap;

    @FXML
    void buttonSearchCnpjClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione o arquivo de CNPJ");

        setInitialDirectory(configFile.getPathCnpjPayersPath(), fileChooser);

        File fileSelected = fileChooser.showOpenDialog(null);

        if (fileSelected != null) {
            String path = fileSelected.getAbsolutePath();
            textFieldPathCnpj.setText(path);
            configFile.setPathCnpjPayersPath(path);
        }

    }

    @FXML
    void buttonSearchFolderClicked(ActionEvent event) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecione a pasta raiz para armazenar os arquivos");

        String startFolder = configFile.getBaseFolder();
        if (!startFolder.isBlank()) {

            File folder = new File(startFolder);
            if (folder.isDirectory()) {
                directoryChooser.setInitialDirectory(folder);
            }
        }

        File folderSelected = directoryChooser.showDialog(null);

        if (folderSelected != null) {
            String path = folderSelected.getAbsolutePath();
            textFieldPathFolder.setText(path);
            configFile.setBaseFolder(path);
            baseFolders.setBaseFolder(path);
        }

    }

    @FXML
    void buttonCredentialClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione o arquivo de credenciais");

        String pathCredential = configFile.getCredentialsFilePath();
        setInitialDirectory(pathCredential, fileChooser);


        File fileSelected = fileChooser.showOpenDialog(null);

        if (fileSelected != null) {
            String path = fileSelected.getAbsolutePath();
            textFieldPathCredentials.setText(path);
            baseFolders.setPathCredentials(path);
            configFile.setCredentialsFilePath(path);
        }

    }

    private void setInitialDirectory(String fullFilePath, FileChooser fileChooser) {
        if(fullFilePath == null){
            return;
        }
        String folderPath = fullFilePath.substring(0, fullFilePath.lastIndexOf(File.separator));
        if (!folderPath.isBlank()) {
            File folder = new File(folderPath);
            if(folder.isDirectory()){
                fileChooser.setInitialDirectory(folder);
            }
        }
    }

    @FXML
    void buttonStartClicked(ActionEvent event) {

        buttonStart.setDisable(true);
        buttonStop.setDisable(false);

        Toggle toggleEmailProviderSelected = emailProviderGroup.getSelectedToggle();
        EmailProvider emailProviderSelected = emailProvidersMap.get(toggleEmailProviderSelected);
        emailProviderSelected.setCredentialsFile(baseFolders.getPathCredentials());

        Toggle toggleSelectedWhichMessages = whichMessages.getSelectedToggle();
        boolean getAllMessages = toggleSelectedWhichMessages == toggleAllMessages;

        configFile.setEmailProvider(emailProviderSelected.getClass().getSimpleName().toLowerCase());

        Task<Void> task = new AnalyzeInbox(emailProviderSelected, getAllMessages);

        task.setOnFailed(w -> {

            restartButtons();
            w.getSource().getException().printStackTrace();
        });

        task.setOnSucceeded(w -> {

            restartButtons();
        });

        progressBar.progressProperty().bind(task.progressProperty());
        labelProgress.textProperty().bind(task.messageProperty());

        thread = new Thread(task);
        thread.start();
    }

    public void restartButtons() {
        buttonStart.setDisable(false);
        buttonStop.setDisable(true);
        progressBar.progressProperty().unbind();
        labelProgress.textProperty().unbind();
        progressBar.setProgress(0);
    }

    @FXML
    void buttonStopClicked(ActionEvent event) {

        if (thread != null && thread.isAlive()) {
            try {
                thread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        configFile = new ConfigFile();

        textFieldPathCredentials.setText(configFile.getCredentialsFilePath());
        textFieldPathFolder.setText(configFile.getBaseFolder());
        textFieldPathCnpj.setText(configFile.getPathCnpjPayersPath());

        baseFolders.setBaseFolder(textFieldPathFolder.getText());
        baseFolders.setPathCredentials(textFieldPathCredentials.getText());

        emailProvidersMap = new HashMap<>();

        EmailProvider gmailProvider = new GmailProvider();
        EmailProvider outlookProvider = new OutlookProvider();
        emailProvidersMap.put(toggleGmailProvider, gmailProvider);
        emailProvidersMap.put(toggleOutlookProvider, outlookProvider);

        String emailProviderFromConfig = configFile.getEmailProvider();

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