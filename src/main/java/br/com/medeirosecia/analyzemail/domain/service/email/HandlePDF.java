package br.com.medeirosecia.analyzemail.domain.service.email;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.excel.ExcelFile;
import br.com.medeirosecia.analyzemail.infra.pdf.PdfTools;

public class HandlePdf implements HandleAttachmentType {

    @Override
    public void analyzeAttachment(EmailAttachmentDAO emailAttachment, ExcelFile excel) {

        ExecutorService executor = Executors.newFixedThreadPool(10);

        ByteArrayInputStream stream = new ByteArrayInputStream(emailAttachment.getData());
        PdfTools tools = new PdfTools(stream);
        List<String> pdfPages = tools.getTextFromPdf();
        for (String page : pdfPages) {
            executor.execute(() -> new HandlePdfPage(page, emailAttachment, excel));
        }
        executor.shutdown();
        excel.save();
    }

}
