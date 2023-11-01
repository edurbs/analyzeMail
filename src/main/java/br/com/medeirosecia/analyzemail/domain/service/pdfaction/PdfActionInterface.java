package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import java.util.List;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.excel.ExcelFile;

public interface PdfActionInterface {
    public void save(EmailAttachmentDAO attachment, String pdfText, ExcelFile excelFile);
    public void setCnpjPayers(List<String> cnpjListPayers);
}
