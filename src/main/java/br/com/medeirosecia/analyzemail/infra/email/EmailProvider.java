package br.com.medeirosecia.analyzemail.infra.email;

import java.util.List;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;

public interface EmailProvider {    
    
    public void setCredentialsFile(String credentialsFile);
    public EmailLabelDAO getEmailLabel();
    public List<EmailMessageDAO> getMessagesWithoutLabel();
    public List<EmailMessageDAO> getAllMessages();
    public List<EmailAttachmentDAO> listAttachments(EmailMessageDAO emailMessageDAO, String[] extensions);
    public void setMessageWithThisLabel(String messageId);    
    

}
