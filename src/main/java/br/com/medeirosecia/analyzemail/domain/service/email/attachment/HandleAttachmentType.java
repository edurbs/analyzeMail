package br.com.medeirosecia.analyzemail.domain.service.email.attachment;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;

public interface HandleAttachmentType {
    public void analyzeAttachment(EmailAttachmentDAO emailAttachmentDAO);
}
