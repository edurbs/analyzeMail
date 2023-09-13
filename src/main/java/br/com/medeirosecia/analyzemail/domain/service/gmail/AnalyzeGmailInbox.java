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

import br.com.medeirosecia.analyzemail.domain.repository.EmailLabel;
import br.com.medeirosecia.analyzemail.infra.email.HandleGmailLabel;
import br.com.medeirosecia.analyzemail.infra.email.MyGmail;
import br.com.medeirosecia.analyzemail.infra.email.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;
import javafx.concurrent.Task;

public class AnalyzeGmailInbox extends Task<Void> {    
    
    private LocalFileSystem localFileSystem;

    private Gmail service;
    private MyGmail myGmail;
    private String user;
    private EmailLabel emailLabel;

    
  

    public AnalyzeGmailInbox(LocalFileSystem localFileSystem) {       
        this.localFileSystem = localFileSystem;
         
    }

    @Override
    protected Void call() throws Exception {
        
        this.myGmail = new MyGmail(this.localFileSystem.getPathCredentials());
        this.user = myGmail.getUser();
        var handleGmailInbox = new HandleGmailInbox(myGmail);
        this.service = myGmail.getConnection();
        if(!this.setLabelId()){
            updateMessage("Etiqueta não encontrada!");
            return null;
        }
        
        int maxThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);        
        
        String[] header = new String[]{"Dt.Emissão",
                "CNPJ Emitente",
                "Chave de acesso",
                "Nome do arquivo"
        };
        var myExcel = new MyExcel(this.localFileSystem, "PlanilhaNF-AnalyzedMail.xlsx", header);
        myExcel.saveWorkbook();

        List<Message> messages = handleGmailInbox.getNotAnalyzedMessages();
        while(messages!=null && !messages.isEmpty()){
            
            
            
            int i=0;
            for(Message message : messages) {                
                if(Thread.currentThread().isInterrupted()){
                    return null;
                }
                i++;
                updateProgress(i, messages.size());
                updateMessage("Processando mensagem "+i+" de um pacote de "+messages.size()+".");
            
                Runnable task = new HandleGmailMessage(message, handleGmailInbox, this.localFileSystem);
      
                executor.execute(task);
                this.setLabel(message.getId());  
                
            }
            
            messages = handleGmailInbox.getNotAnalyzedMessages();        
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        
        updateMessage("Não há mais mensagens para processar.");
       
        return null;

    }

    private boolean setLabelId(){

        HandleGmailLabel handleGmailLabel = new HandleGmailLabel(this.myGmail);
        EmailLabel label = handleGmailLabel.getLabel("analyzedmail");
        if(label==null){
            return false;
        }   
        this.emailLabel = label;
        return true;
    }

    private void setLabel(String messageId){
       
        var listLabelsAnalyzedMail = Collections.singletonList(emailLabel.getId());    
        ModifyMessageRequest modify = new ModifyMessageRequest().setAddLabelIds(listLabelsAnalyzedMail);
        try {
            service.users().messages().modify(user, messageId, modify).execute();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    





    
}
