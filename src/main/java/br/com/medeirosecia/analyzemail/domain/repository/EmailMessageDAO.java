package br.com.medeirosecia.analyzemail.domain.repository;

public class EmailMessageDAO {
    private String id;
    private String folderId;
    public EmailMessageDAO(String id){    
        this.id = id;    
    }
    public EmailMessageDAO(String id, String folderId){    
        this.id = id;    
        this.folderId = folderId;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }
    public String getFolderId() {
        return folderId;
    }
}
