package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SearchPdfBoletoTest {
    SearchPdfBoleto searchPdfBoleto;
    PdfUtils pdfUtils = new PdfUtils();


    void setUp(String fileName) {
        String pdfText = pdfUtils.setUp(fileName);
        searchPdfBoleto = new SearchPdfBoleto(pdfText);



    }

    @ParameterizedTest
    @CsvSource(textBlock = """
                boletoSicredi.pdf,           74891123130004133950810193541041594700002339675
                boletoSicredi2.pdf,          74891123219138700804712539461074994990000090000
                boletoSicredi3.pdf,          74891123548865070805507201741001195090000036215
                boletoBradescoImagem.pdf,    23790831049000000056390000916602794900000005028
                boletoBradescoImagem2.pdf,   23792374035000034135954000303302195040001266967
                boletoVivo.pdf,              846900000015099800550016104349435198092332309258
                boletoSafra.pdf,             42297145080001013946700019520121594490000252067
                boletoSafra2.pdf,            42297145080001013946700011713724294580002820000
                boletoBB2.pdf,               00190000090299506800000009436171594690000015255
                boletoBB.pdf,                00190000090358105800900000046177194690000888889
                boletoBB3.pdf,               00190000090209470900901856510175494890000958488
                boletoBB4.pdf,               00190000090292433300400007355175694990000040000
                boletoBB5.pdf,               00190000090295884800001536624172195040000036280
                guiaFunrural.pdf,            858000000127850003852323630716232510763395456617
                guiaITR.pdf,                 858800002191160303852321720701232713010364800108
                guiInss.pdf,                 858000000003581703852327930716232765703529952431
                gru1.pdf,                    858600000012257403631072880471400008566638952306
                gru2.pdf,                    858800000024072903631072880471400008585758822309
                fgts1.pdf,                   858900000140497702392020309252613395451644802227
                fgts2.pdf,                   858500001211359501792316007681050842426224100016
                fgts3.pdf,                   858800000253847701792318007681050842330806400014
                mt1.pdf,                     858600000098220001232026310146190030281571639533
                mt2.pdf,                     858800000520844801232026310147241037281571538001
                mt3.pdf,                     858000000097199501232023309282817037280640589716
                boletoSantander.pdf,         03399699255870003141033532101012995540000243012
                boletoItau.pdf,              34191090248510015293480501490009195160000259727
            """)
    void testAccessKey(String fileName, String accessKey) {
        setUp(fileName);
        Assertions.assertEquals(accessKey, searchPdfBoleto.accessKey());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            boletoBB.pdf, 30.713.395/0001-30
            boletoSafra.pdf, 00.319.835/0001-09
            boletoSafra2.pdf, 00.319.835/0001-09
            boletoBB2.pdf, 07.652.413/0001-08
            boletoBB5.pdf, 11.460.743/0001-60
            boletoSicredi.pdf, 25.274.456/0001-70
            boletoSicredi2.pdf, 03.274.481/0001-11
            boletoVivo.pdf, 02.558.157/0001-62
            boletoSicredi3.pdf, 26.538.777/0001-06
            boletoBB3.pdf, 02.286.479/0001-08
            boletoBB4.pdf, 17.513.186/0001-05
            boletoBB5.pdf, 11.460.743/0001-60
            guiaFunrural.pdf,
            guiaITR.pdf,
            guiInss.pdf,
            boletoBradescoImagem.pdf, 00.552.477/0001-80
            boletoBradescoImagem2.pdf, 61.573.796/0001-66
            gru1.pdf,
            gru2.pdf,
            fgts1.pdf,
            fgts2.pdf,
            fgts3.pdf,
            mt1.pdf,
            mt2.pdf,
            mt3.pdf,
            boletoSantander.pdf, 40.563.641/0001-79
            boletoItau.pdf, 61.064.838/0001-33
    """)
    void testCnpjSupplier(String fileName, String cnpj) {
        setUp(fileName);
        cnpj = cnpj.replaceAll("[\\D]", "");
        Assertions.assertEquals(cnpj, searchPdfBoleto.cnpjSupplier());

    }

    @ParameterizedTest
    @CsvSource( textBlock = """
            boletoSicredi.pdf, 11/09/2023
            boletoSicredi2.pdf, 10/10/2023
            boletoSicredi3.pdf, 20/10/2023
            boletoVivo.pdf, 25/09/2023
            boletoSafra.pdf, 21/08/2023
            boletoSafra2.pdf, 30/08/2023
            boletoBB2.pdf, 10/09/2023
            boletoBB.pdf, 10/09/2023
            boletoBB3.pdf, 30/09/2023
            boletoBB4.pdf, 10/10/2023
            boletoBB5.pdf, 15/10/2023
            guiaFunrural.pdf, 20/09/2023
            guiaITR.pdf, 29/09/2023
            guiInss.pdf, 20/10/2023
            boletoBradescoImagem.pdf, 01/10/2023
            boletoBradescoImagem2.pdf, 15/10/2023
            gru1.pdf, 31/08/2023
            gru2.pdf, 30/09/2023
            fgts1.pdf, 25/09/2023
            fgts2.pdf, 07/10/2023
            fgts3.pdf, 07/10/2023
            mt1.pdf, 14/10/2023
            mt2.pdf, 14/10/2023
            mt3.pdf, 28/09/2023
            boletoSantander.pdf, 04/12/2023
            boletoItau.pdf, 27/10/2023
    """)
    void testDate(String fileName, String date) {
        setUp(fileName);
        Assertions.assertEquals(date, pdfUtils.getFinalDate(searchPdfBoleto.date()));

    }

    @ParameterizedTest
    @CsvSource(textBlock = """
                    boletoSicredi.pdf, 23396.75
                    boletoSicredi2.pdf, 900.0
                    boletoSicredi3.pdf, 362.15
                    boletoBradescoImagem.pdf, 50.28
                    boletoBradescoImagem2.pdf, 12669.67
                    boletoVivo.pdf, 109.98
                    boletoSafra.pdf, 2520.67
                    boletoSafra2.pdf, 28200.0
                    boletoBB2.pdf, 152.55
                    boletoBB.pdf, 8888.89
                    boletoBB3.pdf, 9584.88
                    boletoBB4.pdf, 400.0
                    boletoBB5.pdf, 362.8
                    guiaFunrural.pdf, 1285.0
                    guiaITR.pdf, 21916.03
                    guiInss.pdf, 58.17
                    gru1.pdf, 125.74
                    gru2.pdf, 207.29
                    fgts1.pdf, 1449.77
                    fgts2.pdf, 12135.95
                    fgts3.pdf, 2584.77
                    mt1.pdf, 922.0
                    mt2.pdf, 5284.48
                    mt3.pdf, 919.95
                    boletoSantander.pdf, 2430.12
                    boletoItau.pdf, 2597.27
            """)
    void testValue(String fileName, String value) {
        setUp(fileName);
        Assertions.assertEquals(value, searchPdfBoleto.value().toString());
    }
}
