package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.excel.ExcelFile;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class PdfActionOther extends PdfActionAbstract {

    @Override
    public void save(EmailAttachmentDAO attachment, String pdfText, ExcelFile excelFile) {
        new BaseFolders().savePdfOthers(attachment);
    }

}
