package br.com.medeirosecia.analyzemail.infra.email;

import com.google.api.services.gmail.model.Message;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachment;
import br.com.medeirosecia.analyzemail.domain.service.gmail.HandleAttachment;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;

public class HandleGmailMessage implements Runnable {

    private HandleGmailInbox handleGmailInbox;

    private Message fullMessage;

    private LocalFileSystem localFileSystem;

    public HandleGmailMessage(String messageId, HandleGmailInbox handleGmailInbox, LocalFileSystem localFileSystem) {
        this.localFileSystem = localFileSystem;               
        this.handleGmailInbox = handleGmailInbox; 
        this.fullMessage = handleGmailInbox.getMessage(messageId);            
 
    }

    
    @Override
    public void run() {
         this.processMessage();
    }


    private void processMessage() {

        String messageId = this.fullMessage.getId();                
        try {
            // get the message
            //Message message = handleGmailInbox.getMessage(messageId);

            // list attachments
            //var parts = handleGmailInbox.listAttachments(message);
            var parts = handleGmailInbox.listAttachments(fullMessage);

            // get all attachments
            var attachments = handleGmailInbox.filterByExtension(messageId, parts);

            // if PDF or XML, analyze the PDF and save it
            for (EmailAttachment att : attachments) {
                new HandleAttachment(att, this.localFileSystem);                
            }
      
           
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


}
