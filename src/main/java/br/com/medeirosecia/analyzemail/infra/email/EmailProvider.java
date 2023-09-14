package br.com.medeirosecia.analyzemail.infra.email;

import java.util.List;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDTO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDTO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDTO;

public interface EmailProvider {    
    String getUser();
    void setCredentialsFile(String credentialsFile);
    public EmailLabelDTO getEmailLabel();
    public List<EmailMessageDTO> getNotAnalyzedMessages();
    public List<EmailAttachmentDTO> listAttachments(String messageId, String[] extensions);
    public void setMessageWithLabel(String messageId);    
    public void setEmailLabel(EmailLabelDTO emailLabel);

}
