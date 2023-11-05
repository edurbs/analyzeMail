package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.IOUtils;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.infra.pdf.PdfTools;

public class PdfTestUtils {

    public String getFirstPage(String fileName) {
       var allPages = getAllPages(fileName);
       if(allPages!=null){
           return allPages.get(0).toLowerCase();
       }else{
           return "";
       }
    }

    public List<String> getAllPages(String filename){
        try (FileInputStream fis = new FileInputStream(getPath(filename))){
            EmailAttachmentDAO emailAttachment = new EmailAttachmentDAO(filename, IOUtils.toByteArray(fis));
            ByteArrayInputStream inputStream = new ByteArrayInputStream(emailAttachment.getData());
            return new PdfTools(inputStream).getTextFromPdf();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getPath(String url){
        try {
            URL resource = getClass().getResource("/pdfTest/"+url);
            return Paths.get(resource.toURI()).toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getFinalDate(String[] date){
        String finalDate = date[0]+"/"+date[1]+"/"+date[2];
        return finalDate;
    }

}
