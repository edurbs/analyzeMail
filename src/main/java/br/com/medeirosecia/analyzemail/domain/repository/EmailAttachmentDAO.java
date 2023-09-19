package br.com.medeirosecia.analyzemail.domain.repository;

public class EmailAttachmentDAO {
    private String filename;
    private byte[] data;
    public EmailAttachmentDAO(String filename, byte[] data) {
        this.filename = filename;
        this.data = data;    
    }

    public byte[] getData() {
        return data;
    }
    public String getFileName() {
        return filename;
    }
    public void setFileName(String filename) {
        this.filename = filename;
    }


    
}
