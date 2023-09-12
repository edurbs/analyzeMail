package br.com.medeirosecia.analyzemail.domain.service.gmail;

import com.google.api.services.gmail.model.Message;

import br.com.medeirosecia.analyzemail.console.LocalConsole;
import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachment;
import br.com.medeirosecia.analyzemail.infra.email.MyGmail;

public class HandleGmailMessage implements Runnable {

    private HandleGmailInbox handleGmailInbox;
    private LocalConsole console = new LocalConsole();      
    private Message m;

    public HandleGmailMessage(Message m, HandleGmailInbox handleGmailInbox, MyGmail myGmail){
        this.m = m;
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
                //new HandleAttachment(att, this.myExcel);
                new HandleAttachment(att);
                
            }
      
            // set message as analyzed
            console.msgToUser("Analyzed message id "+ messageId);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    
}
