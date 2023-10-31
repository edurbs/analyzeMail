package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.email.AnalyzePDFTextTest;
import br.com.medeirosecia.analyzemail.infra.pdf.PdfTools;

public class PdfUtils {

    public String setUp(String fileName) {
        try (FileInputStream fis = new FileInputStream(getPath(fileName))){

            EmailAttachmentDAO emailAttachment = new EmailAttachmentDAO(fileName, IOUtils.toByteArray(fis));

            ByteArrayInputStream inputStream = new ByteArrayInputStream(emailAttachment.getData());
            String pdfText = new PdfTools().getTextFromPdf(inputStream);
            return pdfText.toLowerCase();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getPath(String url){
        try {
            URL resource = AnalyzePDFTextTest.class.getResource("/pdfTest/"+url);
            return Paths.get(resource.toURI()).toString();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public String getFinalDate(String[] date){
        String finalDate = date[0]+"/"+date[1]+"/"+date[2];
        return finalDate;
    }

}
