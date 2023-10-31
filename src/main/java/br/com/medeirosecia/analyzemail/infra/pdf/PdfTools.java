package br.com.medeirosecia.analyzemail.infra.pdf;

import java.io.ByteArrayInputStream;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import br.com.medeirosecia.analyzemail.infra.ocr.OcrTools;

public class PdfTools {
    public String getTextFromPdf(ByteArrayInputStream inputStream) {

        String pdfText = "";
        PDDocument pdfDocument;

        try {

            pdfDocument = PDDocument.load(CloseShieldInputStream.wrap(inputStream));

            var pdfTextStripper = new PDFTextStripper();
            pdfText = pdfTextStripper.getText(pdfDocument);

            if (pdfText.length() < 10) {
                pdfText = new OcrTools().getTextFromPdf(pdfDocument);
            }

            pdfDocument.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pdfText;
    }
}
