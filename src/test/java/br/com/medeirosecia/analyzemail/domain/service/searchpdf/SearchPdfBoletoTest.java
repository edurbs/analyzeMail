package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdfBoleto;

class SearchPdfBoletoTest {
    ReadPdfBoleto searchPdfBoleto;
    PdfTestUtils pdfUtils = new PdfTestUtils();


    void setUpOnePage(String fileName) {
        String pdfText = pdfUtils.getFirstPage(fileName);
        searchPdfBoleto = new ReadPdfBoleto(pdfText);
    }

    List<String> setUpMultiplePage(String fileName){
        return pdfUtils.getAllPages(fileName);
    }

    String cleanCnpj(String cnpj){
        if(cnpj!=null){
            cnpj = cnpj.replaceAll("[\\D]", "");
        }else{
            cnpj = "";
        }
        return cnpj;
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
                boletoSicredi.pdf,           74891123130004133950810193541041594700002339675
                boletoVivo.pdf,              846900000015099800550016104349435198092332309258
                boletoSafra.pdf,             42297145080001013946700019520121594490000252067
                boletoSafra2.pdf,            42297145080001013946700011713724294580002820000
                boletoBB2.pdf,               00190000090299506800000009436171594690000015255
                boletoBB.pdf,                00190000090358105800900000046177194690000888889
                guiaFunrural.pdf,            858000000127850003852323630716232510763395456617
                gru2.pdf,                    858800000024072903631072880471400008585758822309
                grf.pdf,                   858500001211359501792316007681050842426224100016
                grf2.pdf,                   858800000253847701792318007681050842330806400014
                mt1.pdf,                     858700000090220001232026310146190030281575114044
                mt2.pdf,                     858000000526844801232026310147241037281571666263
            """)
    void testAccessKey(String fileName, String accessKey) {
        setUpOnePage(fileName);
        Assertions.assertEquals(accessKey, searchPdfBoleto.accessKey());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            boletoBB.pdf, 30.713.395/0001-30
            boletoSafra.pdf, 00.319.835/0001-09
            boletoBB2.pdf, 07.652.413/0001-08
            boletoSicredi.pdf, 25.274.456/0001-70
            boletoSicredi2.pdf, 03.274.481/0001-11
            boletoVivo.pdf, 02.558.157/0001-62
            boletoSicredi3.pdf, 26.538.777/0002-89
            boletoBB3.pdf, 11.460.743/0001-60
            guiaFunrural.pdf,
            gru2.pdf,
            grf.pdf,
            grf2.pdf,
            mt1.pdf,
            mt2.pdf,
            energisa.pdf, 03.467.321/0001-99
            boletoSicredi4.pdf, 2405895000170
    """)
    void testCnpjSupplier(String fileName, String cnpj) {
        setUpOnePage(fileName);
        Assertions.assertEquals(cleanCnpj(cnpj), searchPdfBoleto.cnpjSupplier());

    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            boletoBB.pdf, 44.120.365/0001-25
            boletoSafra.pdf, 44.262.241/0001-84
            boletoSafra2.pdf, 44.262.241/0001-84
            boletoBB2.pdf, 44.262.241/0001-84
            boletoSicredi.pdf, 44.262.241/0001-84
            boletoVivo.pdf,
            boletoSicredi3.pdf, 44.262.241/0001-84
            boletoBB3.pdf, 44262241000184
            guiaFunrural.pdf, 44.262.241/0001-84
            gru2.pdf, 03.477.007/0001-97
            grf.pdf, 44.262.241/0001-84
            grf2.pdf, 43.308.064/0001-67
            mt1.pdf, 44.262.241/0001-84
            mt2.pdf, 44.262.241/0001-84
            energisa.pdf, 44.262.241/0001-84
            boletoSicredi4.pdf, 27406174000105
    """)
    void testCnpjPayer(String filename, String cnpj){
        setUpOnePage(filename);
        Assertions.assertEquals(cleanCnpj(cnpj), searchPdfBoleto.cnpjPayer());
    }

    @ParameterizedTest
    @CsvSource( textBlock = """
            boletoSicredi.pdf, 11/09/2023
            boletoSicredi3.pdf, 20/11/2023
            boletoVivo.pdf, 25/09/2023
            boletoSafra.pdf, 21/08/2023
            boletoSafra2.pdf, 30/08/2023
            boletoBB2.pdf, 10/09/2023
            boletoBB.pdf, 10/09/2023
            boletoBB3.pdf, 15/10/2023
            guiaFunrural.pdf, 20/09/2023
            gru2.pdf, 30/09/2023
            grf.pdf, 07/10/2023
            grf2.pdf, 07/10/2023
            mt1.pdf, 14/10/2023
            mt2.pdf, 14/10/2023
            energisa.pdf, 16/10/2023
    """)
    void testDate(String fileName, String date) {
        setUpOnePage(fileName);
        Assertions.assertEquals(date, pdfUtils.getFinalDate(searchPdfBoleto.date()));

    }

    @ParameterizedTest
    @CsvSource(textBlock = """
                    boletoSicredi.pdf, 23396.75
                    boletoSicredi3.pdf, 1037.0
                    boletoVivo.pdf, 109.98
                    boletoSafra.pdf, 2520.67
                    boletoSafra2.pdf, 28200.0
                    boletoBB2.pdf, 152.55
                    boletoBB.pdf, 8888.89
                    boletoBB3.pdf, 362.8
                    guiaFunrural.pdf, 1285.0
                    gru2.pdf, 207.29
                    grf.pdf, 12135.95
                    grf2.pdf, 2584.77
                    mt1.pdf, 922.0
                    mt2.pdf, 5284.48
                    energisa.pdf, 697.57
            """)
    void testValue(String fileName, String value) {
        setUpOnePage(fileName);
        Assertions.assertEquals(value, searchPdfBoleto.value().toString());
    }

}
