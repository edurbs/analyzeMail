package br.com.medeirosecia.analyzemail.infra.pdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.input.CloseShieldInputStream;
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
        //ExecutorService executor = Executors.newFixedThreadPool(10);

        try {
            PDDocument pdfDocument = PDDocument.load(
                CloseShieldInputStream.wrap(pdfInputStream));

            int numberOfPages = pdfDocument.getNumberOfPages();

            for (int page = 1; page <= numberOfPages; page++) {
                final int pageCopy = page;
                //executor.submit(() -> {
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
                //});
            }

            //executor.shutdown();
            //executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            pdfDocument.close();

        } catch (Exception e) {
            e.printStackTrace();
            //Thread.currentThread().interrupt();
        }

        return listPdfPagesText;
    }


}
