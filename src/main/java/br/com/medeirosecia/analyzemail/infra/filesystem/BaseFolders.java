package br.com.medeirosecia.analyzemail.infra.filesystem;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;

public class BaseFolders {

    private String baseFolder;

    private String boletoFolder;
    private String nfFolder;
    private String nfsFolder;
    private String pdfOthersFolder;

    private String xmlFolder;

    private String pathCredentials;

    public BaseFolders() {
        String folder = new ConfigFile().getBaseFolder();
        this.setBaseFolder(folder);
    }

    public String getBoletoFolder(){
        return this.boletoFolder;
    }

    public String getNfFolder(){
        return this.nfFolder;
    }

    public String getNfsFolder(){
        return this.nfsFolder;
    }

    public String getPdfOthersFolder(){
        return this.pdfOthersFolder;
    }

    public String getXmlFolder(){
        return this.xmlFolder;
    }

    public void setBaseFolder(String baseFolder) {

        this.baseFolder = baseFolder;

        String pdfFolder = baseFolder+"\\pdf";

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

    public String savePdfNfProduto(EmailAttachmentDAO attachment, String[] date){
        String groupFolder = this.getGroupFolder(date[2], date[1], this.nfFolder);
        this.createFolder(groupFolder);
        return this.saveAttachment(attachment, groupFolder);
    }

    public String savePdfNfServico(EmailAttachmentDAO attachment, String[] date){
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

        if(attachment.isSaved()){
            return attachment.getFileName();
        }

        createFolder(folder);

        String fileName = attachment.getFileName();
        fileName = fileName.substring(fileName.lastIndexOf("\\")+1);


        String extension="";
        int indexOfDot = fileName.lastIndexOf(".");
        int counter = 0;
        if (indexOfDot != -1) {
            extension = fileName.substring(indexOfDot);
        }
        String fileNameWithoutExtention = fileName.substring(0, indexOfDot);

        File file = new File(folder+"\\"+fileName);

        while (file.exists()) {
            counter++;

            fileName=fileNameWithoutExtention + "_" + counter + extension;

            file = new File(folder+"\\"+fileName);
        }

        attachment.setFileName(Paths.get(folder, fileName).toString());

        try (FileOutputStream out = new FileOutputStream(file);) {
            out.write(attachment.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
        attachment.setSaved(true);
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
