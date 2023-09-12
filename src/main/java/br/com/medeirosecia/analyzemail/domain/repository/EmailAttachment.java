package br.com.medeirosecia.analyzemail.domain.repository;

public class EmailAttachment {
    private String filename;
    private byte[] data;
    public EmailAttachment(String filename, byte[] data) {
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
