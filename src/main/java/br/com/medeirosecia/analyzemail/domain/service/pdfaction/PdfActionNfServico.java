package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.excel.ExcelFile;
import br.com.medeirosecia.analyzemail.domain.service.searchpdf.SearchPdf;
import br.com.medeirosecia.analyzemail.domain.service.searchpdf.SearchPdfNfServico;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class PdfActionNfServico extends PdfActionAbstract {


    @Override
    public void save(EmailAttachmentDAO attachment, String pdfText, ExcelFile excelFile) {

        SearchPdf pdfSearch = new SearchPdfNfServico(pdfText);


        String[] date = pdfSearch.date();

        new BaseFolders().savePdfNfServico(attachment, date);
    }

}
