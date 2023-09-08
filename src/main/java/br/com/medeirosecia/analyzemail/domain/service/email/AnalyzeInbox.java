package br.com.medeirosecia.analyzemail.domain.service.email;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;

import br.com.medeirosecia.analyzemail.console.LocalConsole;
import br.com.medeirosecia.analyzemail.domain.repository.Attachment;
import br.com.medeirosecia.analyzemail.infra.email.Email;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;

public class AnalyzeInbox {

    private Gmail service;
    private String user;
    private List<String> listLabelsAnalyzedMail;
    private LocalFileSystem localFileSystem;
    private LocalConsole console = new LocalConsole();

    public AnalyzeInbox(){
        this.localFileSystem = new LocalFileSystem();
        Email email = new Email(this.localFileSystem.getLocalCredentialsFolder());
        HandleInbox handleInbox = new HandleInbox(email);
        
        this.service = handleInbox.getService();
        this.user = handleInbox.getUser();
        this.listLabelsAnalyzedMail = Collections.singletonList("Label_4928034761963589095");    

        List<Message> messages = handleInbox.getNotAnalyzedMessages();

        if(messages==null) {
            console.msgToUser("No messages found");            
        } else {
            console.msgToUser("Processing " + messages.size() + " messages");
            console.msgToUser("===========================================");

            for(Message m : messages) {
            
                try {
                    // get the message
                    String messageId = m.getId();                
                    Message message = handleInbox.getMessage(messageId);
        
                    // list attachments
                    var parts = handleInbox.listAttachments(message);

                    // get all attachments
                    var attachments = handleInbox.filterByExtension(messageId, parts);

                    // if PDF or XML, analyze the PDF and save it
                    for (Attachment att : attachments) {
                        saveIfInteresting(att);
                    }
        
                    // set message as analyzed
                    console.msgToUser("Analyzed message id "+ messageId);
                    this.setLabel(messageId);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }        
    }

    private void setLabel(String messageId){
        ModifyMessageRequest modify = new ModifyMessageRequest().setAddLabelIds(listLabelsAnalyzedMail);
        try {
            service.users().messages().modify(user, messageId, modify).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveIfInteresting(Attachment attachment) {
        String filename = attachment.getFilename();
        String extension = getExtension(filename);       

        if(!extension.isEmpty()){

            if(extension.equals("PDF")){

                AnalyzePDF analyzePDF = new AnalyzePDF(attachment);

                if(analyzePDF.isNF()){                    
                    localFileSystem.savePdfNF(attachment);
                    console.msgToUser("Saving PDF as NF file: "+filename);

                }else if(analyzePDF.isBoleto()){
                    
                    localFileSystem.savePdfBoleto(attachment, analyzePDF.getBoletoDate());
                    console.msgToUser("Saving PDF as Boleto file: "+filename);
                }else{
                    
                    localFileSystem.savePdfOthers(attachment);
                    console.msgToUser("Saving PDF as other file: "+filename);
                }

                

            }else if(extension.equals("XML")){
                console.msgToUser("Saving XML file: "+filename);
                localFileSystem.saveXml(attachment);

            } else {
                console.msgToUser("NOT saving file: "+filename);
            }
        }
    }

    private String getExtension(String filename) {
        if (filename.length() == 3) {
            return filename.toUpperCase();
          } else if (filename.length() > 3) {
            return filename.substring(filename.length() - 3).toUpperCase();
          }
        return "";
          
    }
}
