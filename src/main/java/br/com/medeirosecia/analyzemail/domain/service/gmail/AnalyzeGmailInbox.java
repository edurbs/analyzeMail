package br.com.medeirosecia.analyzemail.domain.service.gmail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;

import br.com.medeirosecia.analyzemail.console.LocalConsole;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabel;
import br.com.medeirosecia.analyzemail.infra.email.MyGmail;
import br.com.medeirosecia.analyzemail.infra.email.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;

public class AnalyzeGmailInbox {    
    
    private LocalFileSystem localFileSystem = new LocalFileSystem();
    private LocalConsole console = new LocalConsole();
    private MyExcel myExcel;
    private Gmail service;
    private MyGmail myGmail;
    private String user;
    private HandleGmailInbox handleGmailInbox;

    public AnalyzeGmailInbox(){
        this.myGmail = new MyGmail(this.localFileSystem.getLocalCredentialsFolder());
        this.user = myGmail.getUser();
        this.handleGmailInbox = new HandleGmailInbox(myGmail);
        this.service = myGmail.getConnection();
        
        int maxThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);        
        
        String[] header = new String[]{"Dt.Emissão",
                "CNPJ Emitente",
                "Chave de acesso",
                "Nome do arquivo"
        };
        myExcel = new MyExcel(this.localFileSystem, "PlanilhaNF-AnalyzedMail.xlsx", header);
        this.myExcel.saveWorkbook();

        List<Message> messages = handleGmailInbox.getNotAnalyzedMessages();
        while(messages!=null && !messages.isEmpty()){
               
            console.msgToUser("Processing " + messages.size() + " messages");
            console.msgToUser("===========================================");

            for(Message m : messages) {                
                console.msgToUser("Processing message "+m.getId());
                Runnable task = new HandleGmailMessage(m, handleGmailInbox, myGmail);
                executor.execute(task);
                this.setLabel(m.getId());  
            }
            console.msgToUser("Finished pack of messages.");
            messages = handleGmailInbox.getNotAnalyzedMessages();        
        }

        try {
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }       
    
        console.msgToUser("There is no more messages to process");
    }

    private void setLabel(String messageId){
        // TODO list labels and searsh for "analyzedMail" label
        var emailLabel = new EmailLabel("Label_4928034761963589095", "AnalyzedMail");
        
        var listLabelsAnalyzedMail = Collections.singletonList(emailLabel.getId());    
        ModifyMessageRequest modify = new ModifyMessageRequest().setAddLabelIds(listLabelsAnalyzedMail);
        try {
            service.users().messages().modify(user, messageId, modify).execute();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    








}
