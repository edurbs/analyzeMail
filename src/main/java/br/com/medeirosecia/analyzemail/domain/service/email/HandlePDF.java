package br.com.medeirosecia.analyzemail.domain.service.email;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.infra.pdf.PdfTools;

public class HandlePdf implements HandleAttachmentType {

    @Override
    public void analyzeAttachment(EmailAttachmentDAO emailAttachmentDAO) {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(emailAttachmentDAO.getData());

        PdfTools pdfTools = new PdfTools(inputStream);

        List<String> listPdfPagesText = pdfTools.getTextFromPdf();

        //ExecutorService executor = Executors.newFixedThreadPool(10);
        for (String pdfPageText : listPdfPagesText) {

            //executor.submit(() ->
            new HandlePdfPage(pdfPageText, emailAttachmentDAO);
            //);
        }


        //executor.shutdown();

        // try {
        //     executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        //     Thread.currentThread().interrupt();
        // }


    }

}
