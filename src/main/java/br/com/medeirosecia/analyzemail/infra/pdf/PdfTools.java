package br.com.medeirosecia.analyzemail.infra.pdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

            int numberOfPages = pdfDocument.getNumberOfPages();

            for (int page = 1; page <= numberOfPages; page++) {
                getPdfTextFromPage(listPdfPagesText, pdfDocument, page);
            }

            pdfDocument.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

        return listPdfPagesText;
    }

    private void getPdfTextFromPage(List<String> listPdfPagesText, PDDocument pdfDocument, int page) {

        PDFTextStripper pdfTextStripper;
        String pdfText = "";
        try {
            pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(page);
            pdfTextStripper.setEndPage(page);

            pdfText = pdfTextStripper.getText(pdfDocument);

        } catch (IOException e) {
            e.printStackTrace();
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


        if (pdfText.length() < 10 || countCommomWords == 0 ) {
            pdfText = new OcrTools(pdfDocument).getTextFromPage(page);
        }

        listPdfPagesText.add(pdfText);
    }

}
