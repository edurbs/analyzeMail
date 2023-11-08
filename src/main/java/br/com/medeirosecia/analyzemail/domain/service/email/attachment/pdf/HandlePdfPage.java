package br.com.medeirosecia.analyzemail.domain.service.email.attachment.pdf;

import java.util.List;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionInterface;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.DefinePdfType;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.PdfType;
import br.com.medeirosecia.analyzemail.infra.filesystem.ReadCnpjFile;

public class HandlePdfPage {
    public HandlePdfPage(String pdfPageText, EmailAttachmentDAO emailAttachmentDAO){
        List<String> cnpjListPayers = new ReadCnpjFile().getCnpjListPayers();

        pdfPageText = pdfPageText.toLowerCase();

        DefinePdfType definePdfType = new DefinePdfType(pdfPageText);
        PdfType pdfType = definePdfType.getPdfType();

        PdfActionInterface pdfAction = pdfType.getPdfAction();


        pdfAction.setCnpjPayers(cnpjListPayers);
        pdfAction.save(emailAttachmentDAO, pdfPageText);

    }
}
