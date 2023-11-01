package br.com.medeirosecia.analyzemail.domain.service.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;
import br.com.medeirosecia.analyzemail.domain.service.excel.ExcelFile;
import br.com.medeirosecia.analyzemail.infra.email.EmailProvider;
import br.com.medeirosecia.analyzemail.infra.excel.MyExcel;
import javafx.concurrent.Task;

public class AnalyzeInbox extends Task<Void> {


    private EmailProvider emailProvider;
    private boolean analizeAllMessages;

    public AnalyzeInbox(EmailProvider emailProvider, boolean analizeAllMessages) {

        this.emailProvider = emailProvider;
        this.analizeAllMessages = analizeAllMessages;
    }



    private boolean checkLabel() {
        EmailLabelDAO analyzedLabel = emailProvider.getEmailLabel();
        if (analyzedLabel == null) {
            updateMessage("Etiqueta não encontrada!");
            return false;
        }
        return true;

    }

    private boolean checkExcelFiles(ExcelFile excelFile) {
        try {
            excelFile.checkExcelFiles();
        } catch (IOException e) {
            updateMessage("Planilhas de excel com erro. Feche ou exclua as planilhas!");
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }

    @Override
    public Void call() throws Exception {

        ExcelFile excelFile = new ExcelFile();

        if (!checkExcelFiles(excelFile) || !checkLabel()) {
            return null;
        }

        Map<String, HandleAttachmentType> extensionsMap = new HashMap<>();
        extensionsMap.put("PDF", new HandlePdf());
        extensionsMap.put("XML", new HandleXML());
        extensionsMap.put("ZIP", new HandleArchive());
        extensionsMap.put("RAR", new HandleArchive());
        extensionsMap.put("7Z", new HandleArchive());

        String[] extensions = extensionsMap.keySet().toArray(new String[extensionsMap.size()]);

        List<EmailMessageDAO> listMessages = new ArrayList<>();
        updateMessage("Lendo lista quantidade de e-emails...");
        if (this.analizeAllMessages) {
            emailProvider.getAllMessages(listMessages);
        } else {
            emailProvider.getMessagesWithoutLabel(listMessages);
        }

        excelFile.openFiles();

        int messageNumberActual = 0;
        String userMsg = "";
        while(emailProvider.hasMoreMessages()) {

            while( listMessages.size() <= messageNumberActual){
                updateMessage("Aguardando nova lista de e-mails...");

                updateProgress(-1, -1);
                emailProvider.loadMoreMessages(true);
                Thread.sleep(1000);
            }
            emailProvider.loadMoreMessages(false);


            updateProgress(messageNumberActual, listMessages.size());
            userMsg = "Msg " + messageNumberActual + " de " + listMessages.size() + ". ";

            updateMessage(userMsg + "Baixando anexos...");
            var messageActual = listMessages.get(messageNumberActual);
            List<EmailAttachmentDAO> attachments = emailProvider.listAttachments(messageActual, extensions);

            updateMessage(userMsg + "Analizando anexos...");
            final String tempUserMg = userMsg;
            for (EmailAttachmentDAO attachment : attachments) {
                if (!Thread.currentThread().isInterrupted()) {
                    String filename = attachment.getFileName();
                    String extension = getExtension(filename);

                    updateMessage(tempUserMg + extension + ": " + filename);

                    HandleAttachmentType handleAttachment = extensionsMap.get(extension);
                    handleAttachment.analyzeAttachment(attachment, excelFile);
                }else{
                    updateMessage("Finalizando ...");
                    break;
                }
            }


            updateMessage(userMsg + "Marcando mensagem como analisada...");
            emailProvider.setMessageWithThisLabel(messageActual.getId());

            if (Thread.currentThread().isInterrupted()) {
                updateMessage("Finalizando ...");
                break;
            }

            messageNumberActual++;

        }

        updateMessage("Salvando Excel...");
        excelFile.saveAllAndClose();
        updateMessage("Finalizado.");

        if(Thread.currentThread().isInterrupted()){
            Thread.currentThread().interrupt();
        }
        return null;

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
