package br.com.medeirosecia.analyzemail.infra.filesystem;

import java.io.File;
import java.io.FileOutputStream;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;

public class BaseFolders {

    private String baseFolder;

    private String pdfFolder;

    private String boletoFolder;
    private String nfFolder;
    private String nfsFolder;
    private String pdfOthersFolder;

    private String xmlFolder;

    private String pathCredentials;

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;

        this.pdfFolder=baseFolder+"\\pdf";

        this.boletoFolder=pdfFolder+"\\boleto";
        this.nfFolder=pdfFolder+"\\nf";
        this.nfsFolder=pdfFolder+"\\nfs";
        this.pdfOthersFolder=pdfFolder+"\\outros";

        this.xmlFolder=baseFolder+"\\xml";
    }

    public String getPathCredentials() {
        return pathCredentials;
    }

    public void setPathCredentials(String pathCredentials) {
        this.pathCredentials = pathCredentials;
    }

    public String getBaseFolder(){
        return this.baseFolder;
    }

    public String savePdfNF(EmailAttachmentDAO attachment, String[] date){
        String groupFolder = this.getGroupFolder(date[2], date[1], this.nfFolder);
        this.createFolder(groupFolder);
        return this.saveAttachment(attachment, groupFolder);
    }

    public String savePdfNfs(EmailAttachmentDAO attachment, String[] date){
        String groupFolder = this.getGroupFolder(date[2], date[1], this.nfsFolder);
        this.createFolder(groupFolder);
        return this.saveAttachment(attachment, groupFolder);
    }

    public String savePdfBoleto(EmailAttachmentDAO attachment, String[] date){
        String groupFolder = this.getGroupFolder(date[2], date[1], this.boletoFolder);   
        this.createFolder(groupFolder);
        return this.saveAttachment(attachment, groupFolder);
    }

    public String savePdfOthers(EmailAttachmentDAO attachment){
        return this.saveAttachment(attachment, pdfOthersFolder);
    }

    public String saveXml(EmailAttachmentDAO attachment){
        return this.saveAttachment(attachment, xmlFolder);
    }
    
    private String saveAttachment(EmailAttachmentDAO attachment, String folder) {
        createFolder(folder);
 
        String extension="";
        String fileName = attachment.getFileName();        

        int indexOfDot = fileName.lastIndexOf(".");
        int counter = 0;
        if (indexOfDot != -1) {
            extension = fileName.substring(indexOfDot);
        }
    
        File file = new File(folder+"\\"+fileName);
        fileName = file.getName();
        
        file = new File(folder+"\\"+fileName); // without the folder from a zip archive
        
        while (file.exists()) {
            counter++;            
        
            fileName=fileName + "_" + counter + extension;            

            file = new File(folder+"\\"+fileName);
        }
        
        attachment.setFileName(folder+"\\"+fileName);

        try (FileOutputStream out = new FileOutputStream(file);) {            
            out.write(attachment.getData());            
        } catch (Exception e) {            
            e.printStackTrace();
        } 
        return attachment.getFileName();
    }

    public void createFolder(String folder){
        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private String getGroupFolder(String year, String month, String folder){
        return folder+"\\"+year+"\\"+month;        
    }
}

