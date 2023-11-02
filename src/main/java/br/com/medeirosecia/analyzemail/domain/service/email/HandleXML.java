package br.com.medeirosecia.analyzemail.domain.service.email;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class HandleXML implements HandleAttachmentType{

    @Override
    public void analyzeAttachment(EmailAttachmentDAO emailAttachmentDAO) {
        new BaseFolders().saveXml(emailAttachmentDAO);
    }

}
