package br.com.medeirosecia.analyzemail.infra.filesystem;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

public class ConfigFile {

    private Properties prop;
    private String baseFolder;
    private String credentialsFilePath;
    private String filePath;    

    public ConfigFile(){
        this.prop = new Properties();
        this.filePath = System.getProperty("user.home")+"\\.analyzeMail";        
        
        File fileProp = new File(filePath);
        if(fileProp.exists()){            
            try(var f =new java.io.FileInputStream(filePath)){                
                prop.load(f);
                this.baseFolder = prop.getProperty("baseFolder");
                this.credentialsFilePath = prop.getProperty("credentialsFilePath");
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            //fileProp.mkdirs();
            saveProperties();
        }    
    }

    private void saveProperties(){
        if(this.baseFolder!=null){
            this.prop.setProperty("baseFolder", this.baseFolder);
        }
        if(this.credentialsFilePath!=null){

            this.prop.setProperty("credentialsFilePath", this.credentialsFilePath);
        }
        try (var f = new FileOutputStream(this.filePath)) {
            
            prop.store(f,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
        this.saveProperties();
    }

    public void setCredentialsFilePath(String credentialsFilePath) {
        this.credentialsFilePath = credentialsFilePath;
        this.saveProperties();
    }

    public String getBaseFolder() {
        return baseFolder;
    }

    public String getCredentialsFilePath() {
        return credentialsFilePath;
    }

    

}