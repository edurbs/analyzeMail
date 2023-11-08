package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdfInterface;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdfNfServico;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class PdfActionNfServico extends PdfActionAbstract {


    @Override
    public void save(EmailAttachmentDAO attachment, String pdfText) {

        ReadPdfInterface pdfSearch = new ReadPdfNfServico();
        pdfSearch.setText(pdfText);


        String[] date = pdfSearch.date();

        new BaseFolders().savePdfNfServico(attachment, date);
    }

}
