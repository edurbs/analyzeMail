package br.com.medeirosecia.analyzemail.domain.repository;

public class Attachment {
    private String filename;
    private byte[] data;
    public Attachment(String filename, byte[] data) {
        this.filename = filename;
        this.data = data;    
    }

    public byte[] getData() {
        return data;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    
}
