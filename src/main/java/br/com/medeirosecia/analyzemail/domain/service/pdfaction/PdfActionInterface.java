package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import java.util.List;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;

public interface PdfActionInterface {
    public void save(EmailAttachmentDAO attachment, String pdfText);
    public void setCnpjPayers(List<String> cnpjListPayers);
}
