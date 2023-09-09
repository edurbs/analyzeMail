package br.com.medeirosecia.analyzemail.domain.service.gmail;

import java.io.IOException;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;

import br.com.medeirosecia.analyzemail.infra.email.MyGmail;

public class ListGmailLabels {

    public ListGmailLabels(){

        MyGmail email = new MyGmail("/credentials/credentials.json");
        Gmail gmail = email.connect();

        // Print the labels in the user's account.
        String user = "me";

        ListLabelsResponse listResponse;
        try {
            listResponse = gmail.users().labels().list(user).execute();        

            List<Label> labels = listResponse.getLabels();

            if (labels.isEmpty()) {
            System.out.println("No labels found.");
            } else {
                System.out.println("Labels:");
                for (Label label : labels) {
                    System.out.printf(" ID "+label.getId()+"- %s\n", label.getName());
                    
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
