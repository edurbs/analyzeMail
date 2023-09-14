package br.com.medeirosecia.analyzemail.domain.repository;

public class EmailAttachmentDTO {
    private String filename;
    private byte[] data;
    public EmailAttachmentDTO(String filename, byte[] data) {
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
