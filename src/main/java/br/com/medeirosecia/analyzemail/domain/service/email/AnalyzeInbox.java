package br.com.medeirosecia.analyzemail.domain.service.email;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;
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

    private boolean checkExcelFiles() {
        var myExcelNf = new MyExcel("PlanilhaNF-AnalyzedMail.xlsx");
        try {
            myExcelNf.justOpen();
            myExcelNf.saveAndCloseWorkbook();
        } catch (IOException e) {
            updateMessage("Planilha NF está aberta ou com erro. Feche ou exclua a planilha.");
            Thread.currentThread().interrupt();
            return false;
        }

        var myExcelBoleto = new MyExcel("PlanilhaBoleto-AnalyzedMail.xlsx");
        try {
            myExcelBoleto.justOpen();
            myExcelBoleto.saveAndCloseWorkbook();
        } catch (IOException e) {
            updateMessage("Planilha Boletos está aberta ou com erro. Feche ou exclua a planilha.");
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
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

        // TODO check
        if (!checkExcelFiles() || !checkLabel()) {
            return null;
        }

        Map<String, HandleAttachmentType> extensionsMap = new HashMap<>();
        extensionsMap.put("PDF", new HandlePdf());
        extensionsMap.put("XML", new HandleXML());
        extensionsMap.put("ZIP", new HandleArchive());
        extensionsMap.put("RAR", new HandleArchive());
        extensionsMap.put("7Z", new HandleArchive());

        String[] extensions = extensionsMap.keySet().toArray(new String[extensionsMap.size()]);

        List<EmailMessageDAO> messages;
        updateMessage("Calculando quantidade de e-emails...");
        if (this.analizeAllMessages) {
            messages = emailProvider.getAllMessages();
        } else {
            messages = emailProvider.getMessagesWithoutLabel();
        }

        while (messages != null && !messages.isEmpty()) {

            int i = 0;

            for (EmailMessageDAO message : messages) {
                if (Thread.currentThread().isInterrupted()) {

                    Thread.currentThread().interrupt();
                    break;
                }

                i++;

                updateProgress(i, messages.size());
                final String userMsg = "Msg " + i + " de " + messages.size() + ". ";
                updateMessage(userMsg);

                updateMessage(userMsg + "Baixando anexos...");
                List<EmailAttachmentDAO> attachments = emailProvider.listAttachments(message, extensions);

                updateMessage(userMsg + "Analizando anexos...");
                attachments.stream().forEach(attachment -> {

                    String filename = attachment.getFileName();
                    String extension = getExtension(filename);

                    updateMessage(userMsg + extension + ": " + filename);

                    HandleAttachmentType handleAttachment = extensionsMap.get(extension);
                    handleAttachment.analyzeAttachment(attachment);

                });

                emailProvider.setMessageWithThisLabel(message.getId());
            }

            if (Thread.currentThread().isInterrupted()) {
                messages = null;
            } else {
                messages = emailProvider.getMessagesWithoutLabel();
            }
        }

        updateMessage("Finalizado.");
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
