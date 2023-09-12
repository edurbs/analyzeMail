package br.com.medeirosecia.analyzemail.infra.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;

import br.com.medeirosecia.analyzemail.console.LocalConsole;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabel;

public class HandleGmailLabel {

    LocalConsole localConsole = new LocalConsole();
    Gmail service;
    String user;

    public HandleGmailLabel(MyGmail myGmail){

        
        this.service = myGmail.getConnection();

        // Print the labels in the user's account.
        this.user = myGmail.getUser();

        
    }

    public List<EmailLabel> listLabels(){
        List<EmailLabel> emailLabels = new ArrayList<>();

        ListLabelsResponse listResponse;
        try {
            listResponse = service.users().labels().list(user).execute();        

            List<Label> labels = listResponse.getLabels();

            if (labels.isEmpty()) {
                localConsole.msgToUser("No labels found.");
            } else {                
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
}
