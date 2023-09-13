package br.com.medeirosecia.analyzemail.domain.service.gmail;

import com.google.api.services.gmail.model.Message;

import br.com.medeirosecia.analyzemail.console.LocalConsole;
import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachment;
import br.com.medeirosecia.analyzemail.infra.email.MyGmail;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;
import javafx.scene.control.TextArea;

public class HandleGmailMessage  {

    private HandleGmailInbox handleGmailInbox;
    private LocalConsole console;
    private Message m;
    private TextArea debugTextArea;
    private LocalFileSystem localFileSystem;

    public HandleGmailMessage(Message m, HandleGmailInbox handleGmailInbox, MyGmail myGmail, LocalConsole console,
            LocalFileSystem localFileSystem, TextArea debugTextArea) {
        this.m = m;
        this.localFileSystem = localFileSystem;        
        this. debugTextArea = debugTextArea;
        this.handleGmailInbox = handleGmailInbox;
        this.console = console;
        this.processMessage(m);
    }

    
    // @Override
    // public void run() {
    //      this.processMessage(m);
    // }


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
                new HandleAttachment(att, this.console, this.localFileSystem, this.debugTextArea);
                
            }
      
            // set message as analyzed
            
            this.debugTextArea.appendText("\nAnalizado mensagem id: "+ messageId);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    
}
