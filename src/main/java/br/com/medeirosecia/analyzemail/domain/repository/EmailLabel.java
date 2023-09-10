package br.com.medeirosecia.analyzemail.domain.repository;

public class EmailLabel {
    private String id;
    private String name;

    public EmailLabel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    
}
