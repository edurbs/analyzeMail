package br.com.medeirosecia.analyzemail.infra.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PageExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.text.PDFTextStripper;

import br.com.medeirosecia.analyzemail.infra.ocr.OcrTools;

public class PdfTools {

    private ByteArrayInputStream pdfInputStream;

    public PdfTools(ByteArrayInputStream inputStream) {
        this.pdfInputStream = inputStream;
    }

    public HashMap<String, ByteArrayInputStream> getTextFromPdf() {
        HashMap<String, ByteArrayInputStream> pdfPages = new HashMap<>();
        try {
            PDDocument pdfDocument = Loader.loadPDF(pdfInputStream.readAllBytes());
            int numberOfPages = pdfDocument.getNumberOfPages();
            for (int i = 1; i <= numberOfPages; i++) {
                PageExtractor extractor = new PageExtractor(pdfDocument);
                extractor.setStartPage(i);
                extractor.setEndPage(i);
                PDDocument pageExtracted = extractor.extract();
                String pageExtractedText = getPdfTextFromPage(pageExtracted);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                pageExtracted.save(byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                pdfPages.put(pageExtractedText, new ByteArrayInputStream(byteArray));
            }
            pdfDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdfPages;
    }

    //private void getPdfTextFromPage(List<String> listPdfPagesText, PDDocument pdfDocument, int page) {
    private String getPdfTextFromPage(PDDocument pdfDocument) {
        PDFTextStripper pdfTextStripper;
        String pdfText = "";
        try {
            pdfTextStripper = new PDFTextStripper();
            pdfText = pdfTextStripper.getText(pdfDocument);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        // detect some PDF with criptographic font
        int countCommomWords = 0;
        String[] commomWords = CommomWords.PORTUGUES.getCommomWords();
        Set<String> commomWordsSet = new HashSet<>(Arrays.asList(commomWords));
        for (String word : commomWordsSet) {
            if (pdfText.toLowerCase().contains(word.toLowerCase())) {
                countCommomWords++;
            }
        }


        if (pdfText.length() < 10 || countCommomWords <= 2 ) {
            pdfText = new OcrTools(pdfDocument).getTextFromPage(1);
        }

        return pdfText;
    }

}
