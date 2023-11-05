package br.com.medeirosecia.analyzemail.domain.service.email.attachment.pdf;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionBoleto;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionInterface;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionNfProduto;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionNfServico;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionOther;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.DefinePdfType;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.PdfType;
import br.com.medeirosecia.analyzemail.infra.filesystem.ReadCnpjFile;

public class HandlePdfPage {
    public HandlePdfPage(String pdfPageText, EmailAttachmentDAO emailAttachmentDAO){
        List<String> cnpjListPayers = new ReadCnpjFile().getCnpjListPayers();

        pdfPageText = pdfPageText.toLowerCase();

        DefinePdfType definePdfType = new DefinePdfType(pdfPageText);
        PdfType pdfType = definePdfType.getPdfType();

        // create a class to store this relationship of the map

        final Map<PdfType, PdfActionInterface> mapPdfActionByType = new EnumMap<>(PdfType.class);
        mapPdfActionByType.put(PdfType.NF_PRODUTO, new PdfActionNfProduto());
        mapPdfActionByType.put(PdfType.NF_SERVICO, new PdfActionNfServico());
        mapPdfActionByType.put(PdfType.BOLETO, new PdfActionBoleto());
        mapPdfActionByType.put(PdfType.OUTRO, new PdfActionOther());

        PdfActionInterface pdfAction = mapPdfActionByType.get(pdfType);

        pdfAction.setCnpjPayers(cnpjListPayers);
        pdfAction.save(emailAttachmentDAO, pdfPageText);

    }
}
