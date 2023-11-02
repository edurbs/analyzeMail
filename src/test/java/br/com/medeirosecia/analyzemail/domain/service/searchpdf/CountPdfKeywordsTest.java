package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CountPdfKeywordsTest {

    private CountPdfKeywords countPdfKeywords;

    void setUp(String fileName) {

        String pdfText = new PdfTestUtils().getFirstPage(fileName);

        countPdfKeywords = new CountPdfKeywords(pdfText);

    }

    @ParameterizedTest
    @ValueSource(strings = { "pdfOutro.pdf" })
    void testIfPdfOther(String filename) {
        setUp(filename);
        Assertions.assertTrue(countPdfKeywords.isPdfOther());
        Assertions.assertFalse(countPdfKeywords.isPdfBoleto());
        Assertions.assertFalse(countPdfKeywords.isPdfNfProduto());
        Assertions.assertFalse(countPdfKeywords.isPdfNfServico());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
                    boletoSicredi.pdf
                    boletoSicredi2.pdf
                    boletoVivo.pdf
                    boletoSafra.pdf
                    boletoBB2.pdf
                    boletoBB.pdf
                    guiaFunrural.pdf
            """)
    void testIsPdfBoleto(String fileName) {
        setUp(fileName);
        Assertions.assertTrue(countPdfKeywords.isPdfBoleto());
        Assertions.assertFalse(countPdfKeywords.isPdfOther());
        Assertions.assertFalse(countPdfKeywords.isPdfNfProduto());
        Assertions.assertFalse(countPdfKeywords.isPdfNfServico());
        Assertions.assertFalse(countPdfKeywords.isPdfEnergisa());
    }

    @ParameterizedTest
    @ValueSource(strings = { "nfe1.pdf", "nfe2.pdf", "nfe3.pdf" })
    void testIsPdfNfProduto(String fileName) {
        setUp(fileName);
        Assertions.assertTrue(countPdfKeywords.isPdfNfProduto());
        Assertions.assertFalse(countPdfKeywords.isPdfOther());
        Assertions.assertFalse(countPdfKeywords.isPdfNfServico());
        Assertions.assertFalse(countPdfKeywords.isPdfBoleto());

    }

    @ParameterizedTest
    @ValueSource(strings = { "nfse1.pdf",
            "nfse2.pdf",
            "nfse3.pdf",
            "nfse5.pdf",
            "nfse6.pdf",
            "nfse7.pdf",
            "nfSoImagemNFSCuiaba.pdf" })
    void testIsPdfNfServico(String fileName) {
        setUp(fileName);
        Assertions.assertTrue(countPdfKeywords.isPdfNfServico());
        Assertions.assertFalse(countPdfKeywords.isPdfOther());
        Assertions.assertFalse(countPdfKeywords.isPdfNfProduto());
        Assertions.assertFalse(countPdfKeywords.isPdfBoleto());

    }

    @ParameterizedTest
    @ValueSource(strings = { "energisa.pdf", "energisa2.pdf" })
    void testIsPdfEnergisa(String fileName) {
        setUp(fileName);
        Assertions.assertTrue(countPdfKeywords.isPdfEnergisa());
        Assertions.assertFalse(countPdfKeywords.isPdfOther());
        Assertions.assertFalse(countPdfKeywords.isPdfNfProduto());
        Assertions.assertFalse(countPdfKeywords.isPdfNfServico());
        Assertions.assertTrue(countPdfKeywords.isPdfBoleto());

    }

}
