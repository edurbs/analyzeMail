package br.com.medeirosecia.analyzemail.infra.pdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import br.com.medeirosecia.analyzemail.infra.ocr.OcrTools;

public class PdfTools {

    private ByteArrayInputStream pdfInputStream;

    public PdfTools(ByteArrayInputStream inputStream) {
        this.pdfInputStream = inputStream;
    }

    public List<String> getTextFromPdf() {
        List<String> listPdfPagesText = new ArrayList<>();

        try {
            PDDocument pdfDocument = Loader.loadPDF(pdfInputStream.readAllBytes());
            // CloseShieldInputStream.wrap(pdfInputStream));

            int numberOfPages = pdfDocument.getNumberOfPages();

            for (int page = 1; page <= numberOfPages; page++) {
                final int pageCopy = page;

                PDFTextStripper pdfTextStripper;
                String pdfText = "";
                try {
                    pdfTextStripper = new PDFTextStripper();
                    pdfTextStripper.setStartPage(pageCopy);
                    pdfTextStripper.setEndPage(pageCopy);

                    pdfText = pdfTextStripper.getText(pdfDocument);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (pdfText.length() < 10) {
                    pdfText = new OcrTools(pdfDocument).getTextFromPage(pageCopy);
                }

                listPdfPagesText.add(pdfText);

            }

            pdfDocument.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

        return listPdfPagesText;
    }

}
