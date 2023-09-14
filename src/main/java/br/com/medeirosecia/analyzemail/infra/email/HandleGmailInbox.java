package br.com.medeirosecia.analyzemail.infra.email;

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

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachment;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessage;

public class HandleGmailInbox {    
    
    private EmailProvider myGmail;        
    private Gmail service;
    private String user;


    public HandleGmailInbox(EmailProvider myGmail){
        this.myGmail = myGmail;        
        this.service = (Gmail) myGmail.getConnection();
        this.user = this.myGmail.getUser();
    
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



    public List<EmailMessage> getNotAnalyzedMessages() {        
        List<Message> messages = new ArrayList<>();
        if(this.service!=null){
            try {
                ListMessagesResponse listMessageResponse = this.service.users().messages().list(user)
                        .setQ("!label:analyzedmail")
                    .execute();
                messages = listMessageResponse.getMessages();
                      
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<EmailMessage> emailMessages = new ArrayList<>();
        if(messages!=null && !messages.isEmpty()){
            for(Message message: messages) {
                var emailMessage = new EmailMessage();
                emailMessage.setId(message.getId());
                emailMessages.add(emailMessage);
            }
        }
        return emailMessages;
    }


    public List<MessagePart> listAttachments(Message message) {
         return message.getPayload().getParts();
        
    }

    public List<EmailAttachment> filterByExtension(String messageId, List<MessagePart> parts) {
        List<EmailAttachment> attachments = new ArrayList<>();

        if(parts!=null){
            for(MessagePart part: parts) {
                if(part.getFilename()!=null && part.getFilename().length()>0) {

                    var fileByteArray = downloadAttachment(part, messageId); 
                    
                    String filename = part.getFilename();                    

                    EmailAttachment attachment = new EmailAttachment(filename, fileByteArray);
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
            return Base64.decodeBase64(attachPart.getData());
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new byte[0];
    }

}
