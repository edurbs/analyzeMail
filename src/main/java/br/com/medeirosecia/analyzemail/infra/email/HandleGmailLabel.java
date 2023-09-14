package br.com.medeirosecia.analyzemail.infra.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ModifyMessageRequest;

import br.com.medeirosecia.analyzemail.domain.repository.EmailLabel;

public class HandleGmailLabel {
   
    Gmail service;
    String user;
    EmailLabel emailLabel;

    public HandleGmailLabel(EmailProvider myGmail){        
        this.service = (Gmail) myGmail.getConnection();
        this.user = myGmail.getUser();        
    }

    private List<EmailLabel> listLabels(){
        List<EmailLabel> emailLabels = new ArrayList<>();

        ListLabelsResponse listResponse;
        try {
            listResponse = service.users().labels().list(user).execute();        

            List<Label> labels = listResponse.getLabels();

            if (!labels.isEmpty()) {                                
                for (Label label : labels) {                    
                    emailLabels.add(new EmailLabel(label.getId(), label.getName()));
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return emailLabels;
    }

    private void searchLabel(String label){
        List<EmailLabel> emailLabels = listLabels();
        for (EmailLabel emailLabel : emailLabels) {
            if(emailLabel.getName().toLowerCase().contains(label.toLowerCase())){
                this.emailLabel = emailLabel;
            }
        }        
    }

    public EmailLabel getEmailLabel(){
        
        this.searchLabel("analyzedmail");
        return this.emailLabel;
    }

    public void setLabel(String messageId){
        var listLabelsAnalyzedMail = Collections.singletonList(this.emailLabel.getId());    
        ModifyMessageRequest modify = new ModifyMessageRequest().setAddLabelIds(listLabelsAnalyzedMail);
        try {
            this.service.users().messages().modify(user, messageId, modify).execute();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
