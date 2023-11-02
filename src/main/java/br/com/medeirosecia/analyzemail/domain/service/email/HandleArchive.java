package br.com.medeirosecia.analyzemail.domain.service.email;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.infra.filesystem.ZipFileHandler;

public class HandleArchive implements HandleAttachmentType {

    @Override
    public void analyzeAttachment(EmailAttachmentDAO emailAttachmentDAO) {
        Map<String, HandleAttachmentType> extensionsMap = new HashMap<>();
        extensionsMap.put("PDF", new HandlePdf());
        extensionsMap.put("XML", new HandleXML());

        ByteArrayInputStream archive = new ByteArrayInputStream(emailAttachmentDAO.getData());
        ZipFileHandler zipFileHandler = new ZipFileHandler(archive);

        List<String> extensions = new ArrayList<>(extensionsMap.keySet());

        Map<String, byte[]> extractedFilesMap = zipFileHandler.extractTheseFileExtentions(extensions);

        extractedFilesMap.forEach((fileName, data) -> {

            String fileNameExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
            EmailAttachmentDAO extractedEmailAttachmentDAO = new EmailAttachmentDAO(fileName, data);

            extensionsMap.get(fileNameExtension).analyzeAttachment(extractedEmailAttachmentDAO);
        });

    }

}
