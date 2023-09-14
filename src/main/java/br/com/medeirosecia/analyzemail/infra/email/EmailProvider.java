package br.com.medeirosecia.analyzemail.infra.email;

public interface EmailProvider {
    void connect();
    Object getConnection();
    String getUser();
}
