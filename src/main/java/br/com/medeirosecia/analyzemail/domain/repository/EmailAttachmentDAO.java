package br.com.medeirosecia.analyzemail.domain.repository;

public class EmailAttachmentDAO implements Cloneable{
    private String filename;
    private byte[] data;
    private boolean saved;
    public EmailAttachmentDAO(String filename, byte[] data) {
        this.filename = filename;
        this.data = data;
    }


    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public String getFileName() {
        return filename;
    }
    public void setFileName(String filename) {
        this.filename = filename;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }


    @Override
    public Object clone()  {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


}
