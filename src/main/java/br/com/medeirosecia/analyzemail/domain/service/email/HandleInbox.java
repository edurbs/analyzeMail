package br.com.medeirosecia.analyzemail.domain.service.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;

import br.com.medeirosecia.analyzemail.domain.repository.Attachment;
import br.com.medeirosecia.analyzemail.infra.email.Email;

public class HandleInbox {

    
    private String user = "me"; 
    private Email email;        
    private Gmail service;


    public HandleInbox(Email email){
        this.email = email;        
        this.service = email.connect();
    
    }

    public Gmail getService(){
        return this.service;
    }

    public String getUser(){
        return this.user;
    }

    public Message getMessage(String messageId) {
        Message msg = null;
        try {
            msg = this.service.users().messages().get(user, messageId).execute();
        } catch (IOException e) {            
            e.printStackTrace();
        }                
        return msg;
    }

    public List<Message> getNotAnalyzedMessages() {
        service = email.connect();

        try {
            ListMessagesResponse listMessageResponse = service.users().messages().list(user)
                    .setQ("!label:analyzedmail")
                .execute();
            return listMessageResponse.getMessages();
                  
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }


    public List<MessagePart> listAttachments(Message message) {
         return message.getPayload().getParts();
        
    }

    public List<Attachment> filterByExtension(String messageId, List<MessagePart> parts) {
        List<Attachment> attachments = new ArrayList<>();

        if(parts!=null){
            for(MessagePart part: parts) {
                if(part.getFilename()!=null && part.getFilename().length()>0) {

                    var fileByteArray = downloadAttachment(part, messageId); 
                    
                    String filename = part.getFilename();                    

                    Attachment attachment = new Attachment(filename, fileByteArray);
                    attachments.add(attachment);
                }
            }
        }
        return attachments;
    }


    private byte[] downloadAttachment(MessagePart part, String messageId) {
        String attId = part.getBody().getAttachmentId();
        MessagePartBody attachPart;
        try {
            attachPart = service.users().messages().attachments().get(user, messageId, attId).execute();
            //return Base64.getDecoder().decode(attachPart.getData());
            return Base64.decodeBase64(attachPart.getData());
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new byte[0];
    }

}
