package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.searchpdf.SearchPdf;
import br.com.medeirosecia.analyzemail.domain.service.searchpdf.SearchPdfNfServico;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class PdfActionNfServico extends PdfActionAbstract {


    @Override
    public void save(EmailAttachmentDAO attachment, BaseFolders baseFolders, String pdfText) {

        SearchPdf pdfSearch = new SearchPdfNfServico(pdfText);


        String[] date = pdfSearch.date();

        baseFolders.savePdfNfServico(attachment, date);
    }

}
