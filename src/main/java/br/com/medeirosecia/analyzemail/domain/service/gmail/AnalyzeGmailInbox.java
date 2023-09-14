package br.com.medeirosecia.analyzemail.domain.service.gmail;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import br.com.medeirosecia.analyzemail.domain.repository.EmailMessage;
import br.com.medeirosecia.analyzemail.infra.email.EmailProvider;
import br.com.medeirosecia.analyzemail.infra.email.GmailProvider;
import br.com.medeirosecia.analyzemail.infra.email.HandleGmailInbox;
import br.com.medeirosecia.analyzemail.infra.email.HandleGmailLabel;
import br.com.medeirosecia.analyzemail.infra.email.HandleGmailMessage;
import br.com.medeirosecia.analyzemail.infra.email.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;

public class AnalyzeGmailInbox extends AnalyzeInboxBase implements AnalyzeInbox {    
    
    
    private LocalFileSystem localFileSystem;
    
    private EmailProvider myGmail;
    
    // não fazer dessa uma classe genérica!!! 
    
    public AnalyzeGmailInbox(LocalFileSystem localFileSystem) {
        super(localFileSystem);
        this.localFileSystem = localFileSystem;
        //TODO Auto-generated constructor stub
    }

    @Override
    public Void call() throws Exception {
        
        //this.myGmail = new MyGmail(this.localFileSystem.getPathCredentials());
        this.myGmail = new GmailProvider(this.localFileSystem.getPathCredentials());

        var handleGmailInbox = new HandleGmailInbox(myGmail);        
        var handleGmailLabel = new HandleGmailLabel(myGmail);
                
        if(handleGmailLabel.getEmailLabel()==null){
            updateMessage("Etiqueta não encontrada!");
            return null;
        }
        
        
        String[] header = new String[]{"Dt.Emissão",
        "CNPJ Emitente",
        "Chave de acesso",
        "Nome do arquivo"
        };
        var myExcel = new MyExcel(this.localFileSystem, "PlanilhaNF-AnalyzedMail.xlsx", header);
        myExcel.saveWorkbook();
        
        int maxThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);        

        List<EmailMessage> messages = handleGmailInbox.getNotAnalyzedMessages();
        while(messages!=null && !messages.isEmpty()){
                        
            int i=0;
            for(EmailMessage message : messages) {                
                if(Thread.currentThread().isInterrupted()){
                    return null;
                }
                i++;
                updateProgress(i, messages.size());
                updateMessage("Processando mensagem "+i+" de um pacote de "+messages.size()+".");
                
                Runnable task = new HandleGmailMessage(message.getId(), handleGmailInbox, this.localFileSystem);
      
                executor.execute(task);
                handleGmailLabel.setLabel(message.getId());  
                
            }            
            messages = handleGmailInbox.getNotAnalyzedMessages();        
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        
        updateMessage("Não há mais mensagens para processar.");
       
        return null;

    }


    
    
}
