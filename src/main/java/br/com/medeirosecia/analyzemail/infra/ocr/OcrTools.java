package br.com.medeirosecia.analyzemail.infra.ocr;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.file.Paths;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import br.com.medeirosecia.analyzemail.domain.service.email.HandlePDF;
import net.sourceforge.tess4j.Tesseract;

public class OcrTools {


    private String getTextFromImage(BufferedImage bufferedImage){
        try {
            Tesseract tesseract = new Tesseract();
            URL url = HandlePDF.class.getResource("/tesseract/fast/");
            String tessractDataPath = Paths.get(url.toURI()).toString();
            tesseract.setDatapath(tessractDataPath);
            tesseract.setLanguage("por");
            tesseract.setPageSegMode(1); // Automatic Page Segmentation with OSD

            String pageResult = tesseract.doOCR(bufferedImage);
            return pageResult.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getTextFromPdf(PDDocument pdfDocument){
        try {
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
            int numPages = pdfDocument.getNumberOfPages();

            StringBuilder resultBuilder = new StringBuilder();

            for (int page = 0; page < numPages; page++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300, ImageType.GRAY);
                String pageResult = new OcrTools().getTextFromImage(bufferedImage);
                resultBuilder.append(pageResult);
            }

            return resultBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
