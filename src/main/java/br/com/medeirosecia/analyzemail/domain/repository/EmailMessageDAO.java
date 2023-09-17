package br.com.medeirosecia.analyzemail.domain.repository;

public class EmailMessageDAO {
    private String id;
    public EmailMessageDAO(String id){    
        this.id = id;    
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
}
