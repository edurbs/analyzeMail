package br.com.medeirosecia.analyzemail.domain.service.gmail;

import com.google.api.services.gmail.model.Message;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachment;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;

public class HandleGmailMessage implements Runnable {

    private HandleGmailInbox handleGmailInbox;

    private Message m;

    private LocalFileSystem localFileSystem;

    public HandleGmailMessage(Message message, HandleGmailInbox handleGmailInbox, LocalFileSystem localFileSystem) {
        this.m = message;
        this.localFileSystem = localFileSystem;       
   
        this.handleGmailInbox = handleGmailInbox; 
 
    }

    
    @Override
    public void run() {
         this.processMessage(m);
    }


    private void processMessage(Message m) {

        String messageId = m.getId();                
        try {
            // get the message
            Message message = handleGmailInbox.getMessage(messageId);
      
            // list attachments
            var parts = handleGmailInbox.listAttachments(message);

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
