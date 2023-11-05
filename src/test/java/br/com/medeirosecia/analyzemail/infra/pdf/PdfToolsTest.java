package br.com.medeirosecia.analyzemail.infra.pdf;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import br.com.medeirosecia.analyzemail.domain.service.readpdf.PdfTestUtils;

class PdfToolsTest {
    @Test
    void testGetTextFromPdfWithOnePage() {
        PdfTestUtils pdfUtils = new PdfTestUtils();
        List<String> listPdfPagesText = pdfUtils.getAllPages("pdf1pagina.pdf");
        Assertions.assertEquals(1, listPdfPagesText.size());
        Assertions.assertEquals("Pagina unica \r\n", listPdfPagesText.get(0).toString());
    }

    @Test
    void testGetTextFromPdfWithOnePageImage(){
        PdfTestUtils pdfUtils = new PdfTestUtils();
        List<String> listPdfPagesText = pdfUtils.getAllPages("pdf1paginaImagem.pdf");
        Assertions.assertEquals(1, listPdfPagesText.size());
        Assertions.assertEquals("Page 1\n", listPdfPagesText.get(0).toString());
    }

    @Test
    void testGetTextFromPdfWithMultiplePages(){
        PdfTestUtils pdfUtils = new PdfTestUtils();
        List<String> listPdfPagesText = pdfUtils.getAllPages("pdf5paginas.pdf");
        Assertions.assertEquals(5, listPdfPagesText.size());
        for (int i = 0; i < listPdfPagesText.size(); i++) {
            Assertions.assertTrue(listPdfPagesText.get(i).toString().contains("Page "+(i+1)));
            Assertions.assertFalse(listPdfPagesText.get(i).toString().contains("Page "+i));
        }
    }

    @Test
    void testGetTextFromPdfWithMultiplePagesImages(){
        PdfTestUtils pdfUtils = new PdfTestUtils();
        List<String> listPdfPagesText = pdfUtils.getAllPages("pdf5paginasImagem.pdf");
        Assertions.assertEquals(5, listPdfPagesText.size());
        for (int i = 0; i < listPdfPagesText.size(); i++) {
            Assertions.assertTrue(listPdfPagesText.get(i).toString().contains("Page "+(i+1)));
            Assertions.assertFalse(listPdfPagesText.get(i).toString().contains("Page "+i));
        }
    }
}
