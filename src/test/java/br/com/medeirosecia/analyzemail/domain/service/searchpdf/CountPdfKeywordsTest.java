package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import br.com.medeirosecia.analyzemail.domain.service.readpdf.CountPdfKeywords;

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
            boletoBB.pdf
            boletoBB2.pdf
            boletoFesaMT.pdf
            boletoSafra.pdf
            boletoSafra2.pdf
            boletoSicredi.pdf
            boletoSicredi2.pdf
            boletoVivo.pdf
            guiaDARF.pdf
            guiaDARF2.pdf
            guiaFunrural.pdf
            guiaGRF.pdf
            guiaGRRF.pdf
            darMT.pdf
            darMT2.pdf
            darMT3.pdf
            """)
    void testIsPdfBoleto(String fileName) {

        setUp(fileName);

        Assertions.assertTrue(countPdfKeywords.isPdfBoleto());
        Assertions.assertFalse(countPdfKeywords.isPdfOther());
        Assertions.assertFalse(countPdfKeywords.isPdfNfProduto());
        Assertions.assertFalse(countPdfKeywords.isPdfNfServico());

    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        nfe1.PDF
        nfe10.pdf
        nfe11.pdf
        nfe12.pdf
        nfe13.pdf
        nfe2.pdf
        nfe3.pdf
        nfe4.pdf
        nfe5.pdf
        nfe6.pdf
        nfe7.pdf
        nfe8.pdf
        nfe9.pdf
        nfeImagem1.pdf
        nfeImagem2.pdf
            """)
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
        // TODO NFS count keywords tests
        setUp(fileName);
        Assertions.assertTrue(countPdfKeywords.isPdfNfServico());
        Assertions.assertFalse(countPdfKeywords.isPdfOther());
        Assertions.assertFalse(countPdfKeywords.isPdfNfProduto());
        Assertions.assertFalse(countPdfKeywords.isPdfBoleto());

    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        energisa.pdf
        energisa2.pdf
        energisa3.pdf
        energisa4.pdf
    """)
    void testIsPdfEnergisa(String fileName) {
        // FIXME energisa count keywords tests
        setUp(fileName);
        //Assertions.assertTrue(countPdfKeywords.isPdfEnergisa());
        Assertions.assertFalse(countPdfKeywords.isPdfOther());
        Assertions.assertFalse(countPdfKeywords.isPdfNfProduto());
        Assertions.assertFalse(countPdfKeywords.isPdfNfServico());
        Assertions.assertTrue(countPdfKeywords.isPdfBoleto());

    }

}
