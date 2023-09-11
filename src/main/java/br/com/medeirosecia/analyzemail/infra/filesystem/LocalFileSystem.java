package br.com.medeirosecia.analyzemail.infra.filesystem;

import java.io.File;
import java.io.FileOutputStream;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachment;

public class LocalFileSystem {
    //private String baseFolder=System.getProperty("user.dir");
    private String baseFolder="\\temp\\email";

    private String pdfFolder=baseFolder+"\\pdf";
    private String boletoFolder=pdfFolder+"\\boleto";
    private String nfFolder=pdfFolder+"\\nf";
    private String pdfOthersFolder=pdfFolder+"\\outros";
    private String xmlFolder=baseFolder+"\\xml";
    private String localCredentialsFolder=baseFolder+"\\credentials";

    public String getBaseFolder(){
        return this.baseFolder;
    }

    public String getLocalCredentialsFolder(){
        return this.localCredentialsFolder;
    }

    public void savePdfNF(EmailAttachment attachment){
        this.saveAttachment(attachment, nfFolder);
    }

    public void savePdfBoleto(EmailAttachment attachment, String[] date){
        String folder = this.getBoletoGroupFolder(date[2], date[1]);   
        this.createBoletoFolder(folder);
        this.saveAttachment(attachment, folder);
    }

    public void savePdfOthers(EmailAttachment attachment){
        this.saveAttachment(attachment, pdfOthersFolder);
    }

    public void saveXml(EmailAttachment attachment){
        this.saveAttachment(attachment, xmlFolder);
    }
    
    private void saveAttachment(EmailAttachment attachment, String folder) {
        
        File file = new File(folder+"\\"+attachment.getFileName());

        int counter = 0;
        while (file.exists()) {
            counter++;
            String newName=getNewFileName(folder, counter);
            file = new File(newName);
        }        

        try (FileOutputStream out = new FileOutputStream(file);) {            
            out.write(attachment.getData());            
        } catch (Exception e) {            
            e.printStackTrace();
        } 
    }

    private static String getNewFileName(String originalName, int counter) {
        int indexOfDot = originalName.lastIndexOf(".");
        if (indexOfDot == -1) {
            return originalName + "_" + counter;
        } else {
            String baseName = originalName.substring(0, indexOfDot);
            String extension = originalName.substring(indexOfDot);
            return baseName + "_" + counter + extension;
        }
    }

    public void createBoletoFolder(String folder){
        File file = new File(folder);
        file.mkdirs();
    }

    private String getBoletoGroupFolder(String year, String month){
        return boletoFolder+"\\"+year+"\\"+month;
        
    }
    
}
