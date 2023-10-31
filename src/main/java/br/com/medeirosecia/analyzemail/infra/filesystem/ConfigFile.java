package br.com.medeirosecia.analyzemail.infra.filesystem;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

public class ConfigFile {

    private Properties prop = new Properties();
    private String baseFolder = "";
    private String credentialsFilePath = "";
    private String filePath = "";
    private String emailProvider = "";
    private String pathCnpjPayers = "";

    public ConfigFile(){

        filePath = System.getProperty("user.home")+"\\.analyzeMail";

        File fileProp = new File(filePath);
        if(fileProp.exists()){
            try(var file =new java.io.FileInputStream(filePath)){
                prop.load(file);
                loadProperties();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            saveProperties();
        }
    }

    private void loadProperties() {
        baseFolder = prop.getProperty("baseFolder");
        credentialsFilePath = prop.getProperty("credentialsFilePath");
        emailProvider = prop.getProperty("emailProvider");
        pathCnpjPayers = prop.getProperty("pathCnpjPayers");
    }


    private void saveProperties(){

        try (var file = new FileOutputStream(filePath)) {
            prop.store(file,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
        prop.setProperty("baseFolder", baseFolder);
        saveProperties();
    }

    public void setCredentialsFilePath(String credentialsFilePath) {
        this.credentialsFilePath = credentialsFilePath;
        prop.setProperty("credentialsFilePath", credentialsFilePath);
        saveProperties();
    }

    public void setEmailProvider(String emailProvider) {
        this.emailProvider = emailProvider;
        prop.setProperty("emailProvider", emailProvider);
        saveProperties();
    }

    public void setPathCnpjPayersPath(String pathCnpjPayers) {
        this.pathCnpjPayers = pathCnpjPayers;
        prop.setProperty("pathCnpjPayers", pathCnpjPayers);
        saveProperties();
    }

    public String getPathCnpjPayersPath(){
        return pathCnpjPayers;
    }

    public String getBaseFolder() {
        return baseFolder;
    }

    public String getCredentialsFilePath() {
        return credentialsFilePath;
    }

    public String getEmailProvider() {
        return emailProvider;
    }



}
