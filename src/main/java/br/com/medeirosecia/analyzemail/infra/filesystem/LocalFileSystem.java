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

    public String savePdfNF(EmailAttachment attachment, String[] date){
        String groupFolder = this.getNfGroupFolder(date[2], date[1]);
        this.createBoletoFolder(groupFolder);
        return this.saveAttachment(attachment, groupFolder);
    }

    public String savePdfBoleto(EmailAttachment attachment, String[] date){
        String groupFolder = this.getBoletoGroupFolder(date[2], date[1]);   
        this.createBoletoFolder(groupFolder);
        return this.saveAttachment(attachment, groupFolder);
    }

    public String savePdfOthers(EmailAttachment attachment){
        return this.saveAttachment(attachment, pdfOthersFolder);
    }

    public String saveXml(EmailAttachment attachment){
        return this.saveAttachment(attachment, xmlFolder);
    }
    
    private String saveAttachment(EmailAttachment attachment, String folder) {
        String baseName ="";
        String extension="";
        String fileName = attachment.getFileName();

        int indexOfDot = fileName.lastIndexOf(".");
        int counter = 0;
        if (indexOfDot == -1) {
            baseName = fileName;
        } else {
            baseName = fileName.substring(0, indexOfDot);
            extension = fileName.substring(indexOfDot);
        }
    
        File file = new File(folder+"\\"+fileName);
        while (file.exists()) {
            counter++;            
        
            fileName=baseName + "_" + counter + extension;            

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

    public void createBoletoFolder(String folder){
        File file = new File(folder);
        file.mkdirs();
    }

    private String getBoletoGroupFolder(String year, String month){
        return getGroupGolder(year,month,this.boletoFolder);
    }

    private String getNfGroupFolder(String year, String month){
        return getGroupGolder(year,month,this.nfFolder);
    }

    private String getGroupGolder(String year, String month, String folder){
        return folder+"\\"+year+"\\"+month;        
    }
}

