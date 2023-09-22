package br.com.medeirosecia.analyzemail.domain.service.email;

import java.util.List;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;
import br.com.medeirosecia.analyzemail.domain.service.pdf.AnalyzePDFText;
import br.com.medeirosecia.analyzemail.infra.email.EmailProvider;
import br.com.medeirosecia.analyzemail.infra.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;
import javafx.concurrent.Task;

public class AnalyzeInbox extends Task<Void> {    
    
    
    private BaseFolders baseFolders;

    private EmailProvider emailProvider;
    private String[] extensions = new String[] { "PDF", "XML" };
    private String[] header = new String[]{"Dt.Emissão",
                "CNPJ Emitente",
                "Chave de acesso",
                "Nome do arquivo"
        };

    
    public AnalyzeInbox(BaseFolders baseFolders, EmailProvider emailProvider) {
        this.baseFolders = baseFolders;     
        this.emailProvider = emailProvider;
    }
      

    @Override
    public Void call() throws Exception {
        EmailLabelDAO analyzedLabel = emailProvider.getEmailLabel();
        if(analyzedLabel==null){            
            updateMessage("Etiqueta não encontrada!");
            return null;
        }
        

        List<EmailMessageDAO> messages = emailProvider.getNotAnalyzedMessages();
        while(messages!=null && !messages.isEmpty()){
                        
            int i=0;
            
            for(EmailMessageDAO message : messages) {                
                if(Thread.currentThread().isInterrupted()){

                    Thread.currentThread().interrupt();
                    break;
                }
                
                i++;
                updateProgress(i, messages.size());
                updateMessage("Mensagem "+i+" de um pacote de "+messages.size());
                
                
                
                //new HandleAttachment(baseFolders, emailProvider, myExcel, message);             
                handleAttachment(message);             
                emailProvider.setMessageWithThisLabel(message.getId());
            }


            if(Thread.currentThread().isInterrupted()){
                messages = null;
            }else{
                messages = emailProvider.getNotAnalyzedMessages();        
            }
        }

        
        updateMessage("Não há mais mensagens para processar.");

       
        return null;

    }    

    private void handleAttachment(EmailMessageDAO emailMessage)  { 
       
        
        List<EmailAttachmentDAO> attachments = emailProvider.listAttachments(emailMessage, extensions);        
        
        attachments.stream().forEach(this::analyzeAttachment);
        
        
    }
    private void analyzeAttachment(EmailAttachmentDAO attachment) {        

        String filename = attachment.getFileName();
        String extension = getExtension(filename);       
        
        
        if(!extension.isEmpty()){

            if(extension.equals("PDF")){
                
                AnalyzePDFText analyzePDF = new AnalyzePDFText(attachment);

                if(analyzePDF.isNF()){                    
                    baseFolders.savePdfNF(attachment, analyzePDF.getDataEmissao());
                    
                    writeItAsExcel(analyzePDF);        

                }else if(analyzePDF.isBoleto()){
                    
                    baseFolders.savePdfBoleto(attachment, analyzePDF.getBoletoDate());
                    
                } 
                else{
                    
                    baseFolders.savePdfOthers(attachment);
                    
                }

            }else if(extension.equals("XML")){
                baseFolders.saveXml(attachment);                
                
            }
        }
        
    }          
    
    

    private String getExtension(String filename) {
        if (filename.length() == 3) {
            return filename.toUpperCase();
        } else if (filename.length() > 3) {
            return filename.substring(filename.length() - 3).toUpperCase();
        }
        return "";

    }

    private void writeItAsExcel(AnalyzePDFText analyzePDF) {
        
        var myExcel = new MyExcel(this.baseFolders, "PlanilhaNF-AnalyzedMail.xlsx", header);
        String[] date = analyzePDF.getDataEmissao();
        String dataEmissao = date[0] + "/" + date[1] + "/" + date[2];
        String row[] = new String[] { dataEmissao,
                analyzePDF.getCNPJEmitente(),
                analyzePDF.getChaveDeAcesso(),
                analyzePDF.getFileName()
        };
        myExcel.addRow(row);
        myExcel.saveAndCloseWorkbook();
        
    }
    
}
