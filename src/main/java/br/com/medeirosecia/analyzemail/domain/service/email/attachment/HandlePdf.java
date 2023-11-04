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
            tools.getTextFromPdf().forEach(page ->
                new HandlePdfPage(page, emailAttachment)
            );
        });
        thread.start();
    }

}
