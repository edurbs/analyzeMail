package br.com.medeirosecia.analyzemail.infra.email;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;



public class OutlookProviderTest {

    EmailProvider provider = new OutlookProvider();
    
    @Test
    void testGetNotAnalyzedMessages() {
        
        var messages = provider.getMessagesWithoutLabel();
        
        var totalMessages = messages.size();
        Assertions.assertTrue(totalMessages>0);  
        //Assertions.assertEquals(2, totalMessages);
    
    }

    @Test
    void testGetEmailLabel(){
        EmailLabelDAO label = provider.getEmailLabel();
        Assertions.assertNotNull(label);
        Assertions.assertEquals("analyzedMail", label.getName());
    }

    @Test
    void testGetAttachments(){
        provider.setCredentialsFile("C:\\temp\\outlookCredentials.json");
        String[] extensions = new String[] { "PDF", "XML" };
        var messages = provider.getMessagesWithoutLabel();
        for (EmailMessageDAO emailMessageDAO : messages) {
            var atts = provider.listAttachments(emailMessageDAO, extensions);
            for (EmailAttachmentDAO att : atts) {                
                System.out.println(att.getFileName());
                Assertions.assertNotNull(att);                   
            }
        }
    }
}
