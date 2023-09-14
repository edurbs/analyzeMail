package br.com.medeirosecia.analyzemail.domain.service.email;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDTO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDTO;
import br.com.medeirosecia.analyzemail.domain.service.pdf.AnalyzePDFText;
import br.com.medeirosecia.analyzemail.infra.email.EmailProvider;
import br.com.medeirosecia.analyzemail.infra.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class HandleAttachment {

    private BaseFolders baseFolders;
    private MyExcel myExcel;   
    private String[] extensions = new String[] { "PDF", "XML" };


    public HandleAttachment( BaseFolders baseFolders, EmailProvider emailProvider,
            MyExcel myExcel, EmailMessageDTO emailMessage)  { 
       
        this.baseFolders = baseFolders;       
        this.myExcel = myExcel;   
     
        int maxThreads = 6;
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);    
        
        List<EmailAttachmentDTO> attachments = emailProvider.listAttachments(emailMessage.getId(), extensions);        
        attachments.stream().forEach(att -> {
            Runnable task = () -> {
                analyzeAttachment(att);
            };
            executor.submit(task);
                            
        });            
        executor.shutdown();                
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
    }
    private void analyzeAttachment(EmailAttachmentDTO attachment) {        

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

        String[] date = analyzePDF.getDataEmissao();
        String dataEmissao = date[0] + "/" + date[1] + "/" + date[2];
        String row[] = new String[] { dataEmissao,
                analyzePDF.getCNPJEmitente(),
                analyzePDF.getChaveDeAcesso(),
                analyzePDF.getFileName()
        };
        this.myExcel.addRow(row);
    }



}
