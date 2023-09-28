package br.com.medeirosecia.analyzemail.domain.service.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;
import br.com.medeirosecia.analyzemail.infra.email.EmailProvider;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;
import javafx.concurrent.Task;

public class AnalyzeInbox extends Task<Void> {    
    
    
    private BaseFolders baseFolders;
    private EmailProvider emailProvider;
  
    
    public AnalyzeInbox(BaseFolders baseFolders, EmailProvider emailProvider) {
        this.baseFolders = baseFolders;     
        this.emailProvider = emailProvider;    
    }
      

    @Override
    public Void call() throws Exception {

        Map<String, HandleAttachmentType> extensionsMap = new HashMap<>();        
            extensionsMap.put("PDF", new HandlePDF());
            extensionsMap.put("XML", new HandleXML());
            extensionsMap.put("ZIP", new HandleArchive());
            extensionsMap.put("RAR", new HandleArchive());
            extensionsMap.put("7Z", new HandleArchive());
        
        String[] extensions = extensionsMap.keySet().toArray(new String[extensionsMap.size()]);

        EmailLabelDAO analyzedLabel = emailProvider.getEmailLabel();
        if(analyzedLabel==null){            
            updateMessage("Etiqueta não encontrada!");
            return null;
        }
        

        List<EmailMessageDAO> messages = emailProvider.getMessages();
        
        while(messages!=null && !messages.isEmpty()){
                        
            int i=0;
            
            for(EmailMessageDAO message : messages) {                
                if(Thread.currentThread().isInterrupted()){

                    Thread.currentThread().interrupt();
                    break;
                }
                
                i++;                

                updateProgress(i, messages.size());
                final String userMsg = "Msg "+i+" de "+messages.size()+". ";
                updateMessage(userMsg);

                List<EmailAttachmentDAO> attachments = emailProvider.listAttachments(message, extensions);                
                
                attachments.stream().forEach(attachment -> {
                    String filename = attachment.getFileName();
                    String extension = getExtension(filename);    
                    
                    updateMessage(userMsg + extension+": "+filename);               

                    HandleAttachmentType handleAttachment = extensionsMap.get(extension);
                    handleAttachment.analyzeAttachment(attachment, baseFolders);

                });  

                emailProvider.setMessageWithThisLabel(message.getId());
            }


            if(Thread.currentThread().isInterrupted()){
                messages = null;
            }else{
                messages = emailProvider.getMessages();        
            }
        }

        
        updateMessage("Não há mais mensagens para processar.");

       
        return null;

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
