package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import java.util.List;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public interface PdfActionInterface {
    public void save(EmailAttachmentDAO attachment, BaseFolders baseFolders, String pdfText);
    public void setCnpjPayers(List<String> cnpjListPayers);
}
