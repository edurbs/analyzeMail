package br.com.medeirosecia.analyzemail.domain.service.email.attachment;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.infra.pdf.PdfTools;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HandleArchiveTest {

    String getFile(String file){
        try {
            URL resource = getClass().getResource("/pdfTest/"+file);
            return Paths.get(resource.toURI()).toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
    }
    EmailAttachmentDAO getEmailAttachmentDAO(String file){
        try (FileInputStream fis = new FileInputStream(getFile(file))){
            EmailAttachmentDAO emailAttachment = new EmailAttachmentDAO(file, IOUtils.toByteArray(fis));
            return emailAttachment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @ParameterizedTest
    @CsvSource(textBlock = """
               pdfTest.zip
               pdfTest2.zip
               pdfTest.rar
            """)
    void analyzeAttachment(String filename) {
        EmailAttachmentDAO emailAttachmentDAO = getEmailAttachmentDAO(filename);
        HandleArchive handleArchive = new HandleArchive();
        assertDoesNotThrow(() -> handleArchive.analyzeAttachment(emailAttachmentDAO));
    }
}