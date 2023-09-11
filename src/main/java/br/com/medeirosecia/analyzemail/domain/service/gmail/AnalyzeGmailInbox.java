package br.com.medeirosecia.analyzemail.domain.service.gmail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;

import br.com.medeirosecia.analyzemail.console.LocalConsole;
import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachment;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabel;
import br.com.medeirosecia.analyzemail.domain.service.pdf.AnalyzePDFText;
import br.com.medeirosecia.analyzemail.infra.email.MyGmail;
import br.com.medeirosecia.analyzemail.infra.email.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;

public class AnalyzeGmailInbox {

    private Gmail service;
    private String user;
    private LocalFileSystem localFileSystem = new LocalFileSystem();
    private LocalConsole console = new LocalConsole();
    private MyExcel myExcel;

    public AnalyzeGmailInbox(){
        
        MyGmail myGmail = new MyGmail(this.localFileSystem.getLocalCredentialsFolder());
        HandleGmailInbox handleGmailInbox = new HandleGmailInbox(myGmail);
        
        this.service = myGmail.getConnection();
        this.user = myGmail.getUser();
        myExcel = new MyExcel(this.localFileSystem, "PlanilhaNF-AnalyzedMail.xlsx");

        String[] header = new String[]{"Dt.Emissão",
                "CNPJ Emitente",
                "Chave de acesso",
                "Nome do arquivo"
        };
        myExcel.setHeader(header);

        List<Message> messages = handleGmailInbox.getNotAnalyzedMessages();

        if(messages==null) {
            console.msgToUser("No messages found");            
        } else {
            console.msgToUser("Processing " + messages.size() + " messages");
            console.msgToUser("===========================================");

            for(Message m : messages) {
            
                try {
                    // get the message
                    String messageId = m.getId();                
                    Message message = handleGmailInbox.getMessage(messageId);
        
                    // list attachments
                    var parts = handleGmailInbox.listAttachments(message);

                    // get all attachments
                    var attachments = handleGmailInbox.filterByExtension(messageId, parts);

                    // if PDF or XML, analyze the PDF and save it
                    for (EmailAttachment att : attachments) {
                        saveIfInteresting(att);
                    }
        
                    // set message as analyzed
                    console.msgToUser("Analyzed message id "+ messageId);
                    this.setLabel(messageId);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.myExcel.saveWorkbook();        
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

    private void saveIfInteresting(EmailAttachment attachment) {
        String filename = attachment.getFileName();
        String extension = getExtension(filename);       

        if(!extension.isEmpty()){

            if(extension.equals("PDF")){
                
                AnalyzePDFText analyzePDF = new AnalyzePDFText(attachment);

                if(analyzePDF.isNF()){                    
                    console.msgToUser("Saving PDF as NF file: "+filename);
                    localFileSystem.savePdfNF(attachment);
                    this.writeItAsExcel(analyzePDF);

                }else if(analyzePDF.isBoleto()){
                    
                    localFileSystem.savePdfBoleto(attachment, analyzePDF.getBoletoDate());
                    console.msgToUser("Saving PDF as Boleto file: "+filename);
                }else{
                    
                    localFileSystem.savePdfOthers(attachment);
                    console.msgToUser("Saving PDF as other file: "+filename);
                }

                

            }else if(extension.equals("XML")){
                console.msgToUser("Saving XML file: "+filename);
                localFileSystem.saveXml(attachment);

            } else {
                console.msgToUser("NOT saving file: "+filename);
            }
        }
    }

    private void writeItAsExcel(AnalyzePDFText analyzePDF) {       
        
        String[] date = analyzePDF.getDataEmissao();
        String dataEmissao = date[0]+"/"+date[1]+"/"+date[2];
        String row[] = new String[]{ dataEmissao,
                    analyzePDF.getCNPJEmitente(), 
                    analyzePDF.getChaveDeAcesso(),
                    analyzePDF.getFileName()
                };
        this.myExcel.addRow(row);

    }


    private String getExtension(String filename) {
        if (filename.length() == 3) {
            return filename.toUpperCase();
          } else if (filename.length() > 3) {
            return filename.substring(filename.length() - 3).toUpperCase();
          }
        return "";
          
    }
}
