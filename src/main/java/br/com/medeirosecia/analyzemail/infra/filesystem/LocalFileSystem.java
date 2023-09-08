package br.com.medeirosecia.analyzemail.infra.filesystem;

import java.io.File;
import java.io.FileOutputStream;

import br.com.medeirosecia.analyzemail.domain.repository.Attachment;

public class LocalFileSystem {
    //private String baseFolder=System.getProperty("user.dir");
    private String baseFolder="\\temp\\email";

    private String pdfFolder=baseFolder+"\\pdf";
    private String boletoFolder=pdfFolder+"\\boleto";
    private String nfFolder=pdfFolder+"\\nf";
    private String pdfOthersFolder=pdfFolder+"\\outros";
    private String xmlFolder=baseFolder+"\\xml";
    private String localCredentialsFolder=baseFolder+"\\credentials";

    public String getLocalCredentialsFolder(){
        return this.localCredentialsFolder;
    }

    public void savePdfNF(Attachment attachment){
        this.saveAttachment(attachment, nfFolder);
    }

    public void savePdfBoleto(Attachment attachment, String[] date){
        String folder = this.getBoletoGroupFolder(date[2], date[1], date[0]);   
        this.createBoletoFolder(folder);
        this.saveAttachment(attachment, folder);
    }

    public void savePdfOthers(Attachment attachment){
        this.saveAttachment(attachment, pdfOthersFolder);
    }

    public void saveXml(Attachment attachment){
        this.saveAttachment(attachment, xmlFolder);
    }
    
    private void saveAttachment(Attachment attachment, String folder) {
        
        File file = new File(folder+"\\"+attachment.getFilename());
        try (FileOutputStream out = new FileOutputStream(file);) {            
            out.write(attachment.getData());
        } catch (Exception e) {            
            e.printStackTrace();
        } 
    }

    public void createBoletoFolder(String folder){
        File file = new File(folder);
        file.mkdirs();
    }

    private String getBoletoGroupFolder(String year, String month, String day){
        return boletoFolder+"\\"+year+"\\"+month+"\\"+day;
        
    }
    
}
