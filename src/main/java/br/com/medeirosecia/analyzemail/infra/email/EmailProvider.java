package br.com.medeirosecia.analyzemail.infra.email;

import java.util.List;

import com.microsoft.graph.requests.AttachmentCollectionPage;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;

public interface EmailProvider {    
    //public String getUser();
    public void setCredentialsFile(String credentialsFile);
    public EmailLabelDAO getEmailLabel();
    public List<EmailMessageDAO> getNotAnalyzedMessages();
    public List<EmailAttachmentDAO> listAttachments(String messageId, String[] extensions);
    public void setMessageWithThisLabel(String messageId);    
    //public void setEmailLabel(EmailLabelDAO emailLabel);

}
