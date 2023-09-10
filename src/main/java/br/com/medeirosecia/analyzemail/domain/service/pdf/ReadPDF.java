package br.com.medeirosecia.analyzemail.domain.service.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import br.com.medeirosecia.analyzemail.console.LocalConsole;
import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachment;
import net.sourceforge.tess4j.Tesseract;

public class ReadPDF {
    private PDDocument pdfDocument;
    private String pdfText;
    private LocalConsole console = new LocalConsole();

    public ReadPDF(EmailAttachment attachment) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(attachment.getData());
            this.processPDF(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    public ReadPDF(String filePath){
        try {
            FileInputStream fis = new FileInputStream(filePath);            
            ByteArrayInputStream inputStream = new ByteArrayInputStream(IOUtils.toByteArray(fis));
            this.processPDF(inputStream);
            inputStream.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getPDFText() {
        return this.pdfText;
    }
    
    private void setPDFText(String pdfText) {
        this.pdfText = pdfText;
    }

    private void processPDF(InputStream inputStream) {
        
        try {
            this.pdfDocument = PDDocument.load(CloseShieldInputStream.wrap(inputStream));
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            this.setPDFText(pdfTextStripper.getText(this.pdfDocument).toLowerCase());

            // if PDF has no text, works with OCR
            if(this.getPDFText().length()<10){
                String text = this.getOCR();
                this.setPDFText(text);
            }
            
            this.pdfDocument.close();
        } catch (IOException  e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        

        
    }

    private String getOCR()  {
        console.msgToUser("OCR...");
        try {
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300, ImageType.GRAY);
    
            Tesseract tesseract = new Tesseract();
    
            URL url = AnalyzePDFText.class.getResource("/tesseract/");
            String tessractDataPath = Paths.get(url.toURI()).toString();
            tesseract.setDatapath(tessractDataPath);
    
            tesseract.setLanguage("por");
            tesseract.setPageSegMode(1); // Automatic Page Segmentation with OSD
            
            String result = tesseract.doOCR(bufferedImage);
            return result.toLowerCase();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return "";
    }

}
