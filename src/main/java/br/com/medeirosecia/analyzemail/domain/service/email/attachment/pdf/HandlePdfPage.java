package br.com.medeirosecia.analyzemail.domain.service.email.attachment.pdf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionBoleto;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionInterface;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionNfProduto;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionNfServico;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionOther;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.CountPdfKeywords;
import br.com.medeirosecia.analyzemail.infra.filesystem.ReadCnpjFile;

public class HandlePdfPage {
    public HandlePdfPage(String pdfPageText, EmailAttachmentDAO emailAttachmentDAO){
        List<String> cnpjListPayers = new ReadCnpjFile().getCnpjListPayers();

        pdfPageText = pdfPageText.toLowerCase();

        var countKeywords = new CountPdfKeywords(pdfPageText);

        final Map<Boolean, PdfActionInterface> map = new HashMap<>();
        //map.put(countKeywords.isPdfEnergisa(), new PdfActionBoleto());
        map.put(countKeywords.isPdfNfProduto(), new PdfActionNfProduto());
        map.put(countKeywords.isPdfNfServico(), new PdfActionNfServico());
        map.put(countKeywords.isPdfBoleto(), new PdfActionBoleto());
        map.put(countKeywords.isPdfOther(), new PdfActionOther());



        PdfActionInterface pdfAction = map.get(true);
        pdfAction.setCnpjPayers(cnpjListPayers);
        pdfAction.save(emailAttachmentDAO, pdfPageText);
    }
}
