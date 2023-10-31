package br.com.medeirosecia.analyzemail.domain.service.email;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionBoleto;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionInterface;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionNfProduto;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionNfServico;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionOther;
import br.com.medeirosecia.analyzemail.domain.service.searchpdf.CountPdfKeywords;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;
import br.com.medeirosecia.analyzemail.infra.filesystem.ReadCnpjFile;
import br.com.medeirosecia.analyzemail.infra.pdf.PdfTools;

public class HandlePDF implements HandleAttachmentType {



    @Override
    public void analyzeAttachment(EmailAttachmentDAO emailAttachmentDAO, BaseFolders baseFolders) {

        List<String> cnpjListPayers = new ReadCnpjFile().getCnpjListPayers();


        ByteArrayInputStream inputStream = new ByteArrayInputStream(emailAttachmentDAO.getData());
        String pdfText = new PdfTools().getTextFromPdf(inputStream);

        pdfText = pdfText.toLowerCase();

        var countKeywords = new CountPdfKeywords(pdfText);

        final Map<Boolean, PdfActionInterface> map = new HashMap<>();
        map.put(countKeywords.isPdfNfProduto(), new PdfActionNfProduto());
        map.put(countKeywords.isPdfNfServico(), new PdfActionNfServico());
        map.put(countKeywords.isPdfBoleto(), new PdfActionBoleto());
        map.put(countKeywords.isPdfOther(), new PdfActionOther());

        PdfActionInterface pdfAction = map.get(true);
        pdfAction.setCnpjPayers(cnpjListPayers);
        pdfAction.save(emailAttachmentDAO, baseFolders, pdfText);

    }



}
