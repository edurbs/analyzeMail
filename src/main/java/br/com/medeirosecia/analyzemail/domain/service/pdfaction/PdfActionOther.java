package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class PdfActionOther extends PdfActionAbstract {

    @Override
    public void save(EmailAttachmentDAO attachment, BaseFolders baseFolders, String pdfText) {
        baseFolders.savePdfOthers(attachment);
    }

}
