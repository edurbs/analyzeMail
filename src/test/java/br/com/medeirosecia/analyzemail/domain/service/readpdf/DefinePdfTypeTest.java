package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import br.com.medeirosecia.analyzemail.domain.service.readpdf.boleto.BoletoType;

class DefinePdfTypeTest {

    // private CountPdfKeywords countPdfKeywords;
    private DefinePdfType definePdfType;


    void setUp(String fileName) {

        String pdfText = new PdfTestUtils().getFirstPage(fileName);
        this.definePdfType = new DefinePdfType(pdfText);
    }

    @ParameterizedTest
    @ValueSource(strings = { "pdfOutro.pdf" })
    void testIfPdfOther(String filename) {
        setUp(filename);
        Assertions.assertEquals(PdfType.OUTRO, definePdfType.getPdfType());

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
        Assertions.assertEquals(PdfType.BOLETO, definePdfType.getPdfType());

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

        Assertions.assertEquals(PdfType.NF_PRODUTO, definePdfType.getPdfType());

    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            nfse1.pdf
            nfse2.pdf
            nfse3.pdf
            nfse5.pdf
            nfse6.pdf
            nfse7.pdf
            nfseCaceres.pdf
            nfseGoiania.pdf
            nfseNacional.pdf
            nfseNX.pdf
            nfseRio.pdf
                """)
    void testIsPdfNfServico(String fileName) {

        setUp(fileName);

        Assertions.assertEquals(PdfType.NF_SERVICO, definePdfType.getPdfType());

    }

    @ParameterizedTest
    @CsvSource(textBlock = """
                energisa.pdf
                energisa2.pdf
                energisa3.pdf
            """)
    void testIsPdfEnergisa(String fileName) {
        // FIXME energisa count keywords tests
        setUp(fileName);
        Assertions.assertEquals(PdfType.BOLETO, definePdfType.getPdfType());
        Assertions.assertEquals(BoletoType.ENERGISA, definePdfType.getBoletoType());

    }

}
