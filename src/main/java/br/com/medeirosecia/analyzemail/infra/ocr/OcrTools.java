package br.com.medeirosecia.analyzemail.infra.ocr;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import net.sourceforge.tess4j.Tesseract;

public class OcrTools {
    private PDDocument pdfDocument;

    public OcrTools(PDDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    private String getTextFromImage(BufferedImage bufferedImage) {
        try {
            Tesseract tesseract = new Tesseract();

            String tessractDataPath = getTesseractDataPath();

            tesseract.setDatapath(tessractDataPath);
            tesseract.setLanguage("por");
            tesseract.setPageSegMode(1); // Automatic Page Segmentation with OSD
            tesseract.setVariable("debug_file", "/dev/null");

            return tesseract.doOCR(bufferedImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getTesseractDataPath() {
        try {
            URL url = OcrTools.class.getProtectionDomain().getCodeSource().getLocation();
            Path path = Paths.get(url.toURI()).getParent().resolve("Tesseract-OCR");
            return path.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getTextFromPage(int page) {
        if (page > pdfDocument.getNumberOfPages()) {
            return "";
        }

        try {
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);

            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page - 1, 300, ImageType.GRAY);
            return getTextFromImage(bufferedImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
