package br.com.medeirosecia.analyzemail.domain.service.email;

import java.util.List;

import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDTO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDTO;
import br.com.medeirosecia.analyzemail.infra.email.EmailProvider;
import br.com.medeirosecia.analyzemail.infra.excel.MyExcel;
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
        EmailLabelDTO analizedLabel = emailProvider.getEmailLabel();
        if(analizedLabel==null){
            updateMessage("Etiqueta não encontrada!");
            return null;
        }
        emailProvider.setEmailLabel(analizedLabel);
        
        String[] header = new String[]{"Dt.Emissão",
                "CNPJ Emitente",
                "Chave de acesso",
                "Nome do arquivo"
        };
        var myExcel = new MyExcel(this.baseFolders, "PlanilhaNF-AnalyzedMail.xlsx", header);
     

        List<EmailMessageDTO> messages = emailProvider.getNotAnalyzedMessages();
        while(messages!=null && !messages.isEmpty()){
                        
            int i=0;
            
            for(EmailMessageDTO message : messages) {                
                if(Thread.currentThread().isInterrupted()){
                    break;
                }
                
                i++;
                updateProgress(i, messages.size());
                updateMessage("Mensagem "+i+" de um pacote de "+messages.size());
                
                new HandleAttachment(baseFolders, emailProvider, myExcel, message);             
                emailProvider.setMessageWithLabel(message.getId());
            }


            if(Thread.currentThread().isInterrupted()){
                messages = null;
            }else{
                messages = emailProvider.getNotAnalyzedMessages();        
            }
        }

        
        updateMessage("Não há mais mensagens para processar.");
        myExcel.saveAndCloseWorkbook();
       
        return null;

    }    
    
}
