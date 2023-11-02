package br.com.medeirosecia.analyzemail.domain.service.email;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.excel.ExcelFile;

public interface HandleAttachmentType {
    public void analyzeAttachment(EmailAttachmentDAO emailAttachmentDAO);
}
