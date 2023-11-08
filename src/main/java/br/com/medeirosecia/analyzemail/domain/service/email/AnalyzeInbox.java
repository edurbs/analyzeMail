package br.com.medeirosecia.analyzemail.domain.service.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;
import br.com.medeirosecia.analyzemail.domain.service.email.attachment.HandleArchive;
import br.com.medeirosecia.analyzemail.domain.service.email.attachment.HandleAttachmentType;
import br.com.medeirosecia.analyzemail.domain.service.email.attachment.HandlePdf;
import br.com.medeirosecia.analyzemail.domain.service.email.attachment.HandleXML;
import br.com.medeirosecia.analyzemail.infra.email.EmailProvider;
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

    @Override
    public Void call() throws Exception {
        Map<String, HandleAttachmentType> extensionsMap = new HashMap<>();
        extensionsMap.put("PDF", new HandlePdf());
        extensionsMap.put("XML", new HandleXML());
        extensionsMap.put("ZIP", new HandleArchive());
        extensionsMap.put("RAR", new HandleArchive());
        // FEAT handle archive with 7z format

        String[] extensions = extensionsMap.keySet().toArray(new String[extensionsMap.size()]);

        if (!checkLabel()) {
            updateMessage("Etiqueta não encontrada!");
            return null;
        }

        List<EmailMessageDAO> listMessages = getListMessages();

        // FEAT get int number of total messages for outlook

        int messageNumberActual = 1;
        String userMsg = "";
        while (emailProvider.hasMoreMessages() || messageNumberActual <= listMessages.size()) {

            getMoreMessages(listMessages, messageNumberActual);

            updateProgress(messageNumberActual, listMessages.size());
            userMsg = "Msg " + messageNumberActual + " de " + listMessages.size() + ". ";

            updateMessage(userMsg + "Baixando anexos...");
            EmailMessageDAO messageActual = listMessages.get(messageNumberActual-1);
            List<EmailAttachmentDAO> attachments = emailProvider.listAttachments(messageActual, extensions);

            updateMessage(userMsg + "Analizando anexos...");
            analyzeAttachments(extensionsMap, userMsg, attachments);

            updateMessage(userMsg + "Marcando mensagem como analisada...");
            emailProvider.setMessageWithThisLabel(messageActual.getId());

            if (Thread.currentThread().isInterrupted()) {
                updateMessage("Finalizado.");
                break;
            }

            messageNumberActual++;
        }

        updateMessage("Finalizado. Analizados " + (messageNumberActual-1) + " de " + listMessages.size() + " e-mails.");


        if (Thread.currentThread().isInterrupted()) {
            Thread.currentThread().interrupt();
        }

        return null;
    }



    private void analyzeAttachments(Map<String, HandleAttachmentType> extensionsMap, String userMsg,
            List<EmailAttachmentDAO> attachments) {
        for (EmailAttachmentDAO attachment : attachments) {
            if (!Thread.currentThread().isInterrupted()) {
                String filename = attachment.getFileName();
                String extension = getExtension(filename);

                updateMessage(userMsg + extension + ": " + filename);

                HandleAttachmentType handleAttachment = extensionsMap.get(extension);
                handleAttachment.analyzeAttachment(attachment);
            } else {
                updateMessage("Finalizando ...");
                break;
            }
        }
    }

    private void getMoreMessages(List<EmailMessageDAO> listMessages, int messageNumberActual) throws InterruptedException {
        while (listMessages.size() < messageNumberActual) {
            updateMessage("Aguardando nova lista de e-mails...");

            updateProgress(-1, -1);
            emailProvider.loadMoreMessages(true);
            Thread.sleep(1000);
        }
        emailProvider.loadMoreMessages(false);
    }

    private List<EmailMessageDAO> getListMessages() {
        List<EmailMessageDAO> listMessages = new ArrayList<>();
        updateMessage("Lendo quantidade de e-emails...");
        if (this.analizeAllMessages) {
            emailProvider.getAllMessages(listMessages);
        } else {
            emailProvider.getMessagesWithoutLabel(listMessages);
        }
        return listMessages;
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
