package br.com.medeirosecia.analyzemail.domain.service.email.attachment;

import java.io.ByteArrayInputStream;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.email.attachment.pdf.HandlePdfPage;
import br.com.medeirosecia.analyzemail.infra.pdf.PdfTools;

public class HandlePdf implements HandleAttachmentType {

    @Override
    public void analyzeAttachment(EmailAttachmentDAO emailAttachment) {
        Thread thread = new Thread(() -> {
            PdfTools tools = new PdfTools(
                new ByteArrayInputStream(emailAttachment.getData())
            );
            var hashMapPages = tools.getTextFromPdf();
            hashMapPages.forEach((string, byteArray) -> {
                EmailAttachmentDAO emailAttachmentDAOClone = (EmailAttachmentDAO) emailAttachment.clone();
                emailAttachmentDAOClone.setData(byteArray.readAllBytes());
                emailAttachmentDAOClone.setFileName(emailAttachment.getFileName());
                new HandlePdfPage(string, emailAttachmentDAOClone);
            });
        });
        thread.start();
    }

}
