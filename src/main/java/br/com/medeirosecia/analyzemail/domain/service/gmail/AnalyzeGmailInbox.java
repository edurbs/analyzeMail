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
import br.com.medeirosecia.analyzemail.infra.email.HandleGmailLabel;
import br.com.medeirosecia.analyzemail.infra.email.MyGmail;
import br.com.medeirosecia.analyzemail.infra.email.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

public class AnalyzeGmailInbox implements Runnable {    
    
    private LocalFileSystem localFileSystem;
    private LocalConsole console;
    private MyExcel myExcel;
    private Gmail service;
    private MyGmail myGmail;
    private String user;
    private HandleGmailInbox handleGmailInbox;
    private TextArea debugTextArea;
    private EmailLabel emailLabel;
    
    public AnalyzeGmailInbox(LocalConsole console, LocalFileSystem localFileSystem, TextArea debugTextArea) {
        this.console = console;  
        this.localFileSystem = localFileSystem;
        this.debugTextArea = debugTextArea;        
    }

    @Override
    public void run() {  
        this.myGmail = new MyGmail(this.localFileSystem.getPathCredentials());
        this.user = myGmail.getUser();
        this.handleGmailInbox = new HandleGmailInbox(myGmail);
        this.service = myGmail.getConnection();
        if(!this.setLabelId()){
            this.debugTextArea.appendText("Etiqueta não encontrada!");
            return;
        }
        
        // int maxThreads = 5;
        // ExecutorService executor = Executors.newFixedThreadPool(maxThreads);        
        
        String[] header = new String[]{"Dt.Emissão",
                "CNPJ Emitente",
                "Chave de acesso",
                "Nome do arquivo"
        };
        myExcel = new MyExcel(this.localFileSystem, "PlanilhaNF-AnalyzedMail.xlsx", header);
        this.myExcel.saveWorkbook();

        List<Message> messages = handleGmailInbox.getNotAnalyzedMessages();
        while(messages!=null && !messages.isEmpty()){
            
            this.debugTextArea.appendText("\nProcessando " + messages.size() + " messagens");
            
            
            console.msgToUser("===========================================");
            int i=0;
            for(Message m : messages) {                
            
                this.debugTextArea.appendText("\nProcessando messagem "+m.getId());

                //Runnable task = new HandleGmailMessage(m, handleGmailInbox, myGmail, console);
                new HandleGmailMessage(m, handleGmailInbox, myGmail, console, this.localFileSystem, this.debugTextArea);
                //executor.execute(task);
                this.setLabel(m.getId());  
                this.console.setLastProgress(i);
            }
            
            this.debugTextArea.appendText("\nFinalizado pacote de mensagens.");
            messages = handleGmailInbox.getNotAnalyzedMessages();        
        }

        // try {
        //     executor.shutdown();
        //     executor.awaitTermination(60, TimeUnit.SECONDS);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }       
    
        
        this.debugTextArea.appendText("\nNão há mais mensagens para processar.");

    }

    private boolean setLabelId(){
        // analyzedmail
        HandleGmailLabel handleGmailLabel = new HandleGmailLabel(this.myGmail);
        EmailLabel label = handleGmailLabel.getLabel("analyzedmail");
        if(label==null){
            return false;
        }   
        this.emailLabel = label;
        return true;
    }

    private void setLabel(String messageId){
        // TODO list labels and searsh for "analyzedMail" label
        //var emailLabel = new EmailLabel("Label_4928034761963589095", "AnalyzedMail");
        
        var listLabelsAnalyzedMail = Collections.singletonList(emailLabel.getId());    
        ModifyMessageRequest modify = new ModifyMessageRequest().setAddLabelIds(listLabelsAnalyzedMail);
        try {
            service.users().messages().modify(user, messageId, modify).execute();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    
}
