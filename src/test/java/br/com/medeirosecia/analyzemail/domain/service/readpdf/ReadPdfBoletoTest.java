package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ReadPdfBoletoTest {
    ReadPdfBoleto searchPdfBoleto;
    PdfTestUtils pdfUtils = new PdfTestUtils();

    void setUpOnePage(String fileName) {
        String pdfText = pdfUtils.getFirstPage(fileName);
        searchPdfBoleto = new ReadPdfBoleto();
        searchPdfBoleto.setText(pdfText);
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
                boletoSicredi.pdf,  74891123130004133950810193541041594700002339675
                boletoVivo.pdf,     846900000015099800550016104349435198092332309258
                boletoSafra.pdf,    42297145080001013946700019520121594490000252067
                boletoSafra2.pdf,   42297145080001013946700011713724294580002820000
                boletoBB2.pdf,      00190000090299506800000009436171594690000015255
                boletoBB.pdf,       00190000090358105800900000046177194690000888889
                guiaFunrural.pdf,   858000000127850003852323630716232510763395456617
                boletoFesaMT.pdf,   00190000090295884800001536621178295040000036280
                guiaDARF2.pdf,      858300000033066403852237220701223070832651300338
                guiaGRF.pdf,        858200000287178501792212107670050842624784300017
                guiaGRRF.pdf,       858500000720821302392029309202563378657426224126
                darMT.pdf,          858300000017147501232020309121317032279769402060
                darMT2.pdf,         858000000526844801232026310147241037281575294433
                darMT3.pdf,         858200000350040801232022312155924039281671828613
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
            guiaFunrural.pdf,
            boletoSafra2.pdf, 00.319.835/0001-09
            boletoFesaMT.pdf, 11.460.743/0001-60
            guiaDARF2.pdf,
            guiaGRF.pdf,
            guiaGRRF.pdf,
            darMT2.pdf,
            darMT3.pdf,
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
            guiaFunrural.pdf, 44.262.241/0001-84
            boletoSicredi2.pdf, 44262241000184
            boletoFesaMT.pdf, 44262241000184
            guiaDARF2.pdf, 46.247.843/0001-98
            guiaGRF.pdf, 46.247.843/0001-98
            guiaGRRF.pdf, 44.262.241/0001-84
            darMT2.pdf, 44.262.241/0001-84
            darMT3.pdf, 03.477.007/0001-97
    """)
    void testCnpjPayer(String filename, String cnpj){
        setUpOnePage(filename);
        Assertions.assertEquals(cleanCnpj(cnpj), searchPdfBoleto.cnpjPayer());
    }

    @ParameterizedTest
    @CsvSource( textBlock = """
            boletoSicredi.pdf, 11/09/2023
            boletoVivo.pdf, 25/09/2023
            boletoSafra.pdf, 21/08/2023
            boletoSafra2.pdf, 30/08/2023
            boletoBB2.pdf, 10/09/2023
            boletoBB.pdf, 10/09/2023
            guiaFunrural.pdf, 20/09/2023
            boletoFesaMT.pdf, 15/10/2023
            guiaDARF2.pdf, 18/11/2022
            guiaDARF3.pdf, 29/09/2023
            guiaDARF4.pdf, 20/12/2022
            guiaGRF.pdf, 07/11/2022
            guiaGRRF.pdf, 20/09/2023
            darMT2.pdf, 14/10/2023
            darMT3.pdf, 15/12/2023
    """)
    void testDate(String fileName, String date) {
        setUpOnePage(fileName);
        Assertions.assertEquals(date, pdfUtils.getFinalDate(searchPdfBoleto.date()));



    }

    @ParameterizedTest
    @CsvSource(textBlock = """
                    boletoSicredi.pdf, 23396.75
                    boletoVivo.pdf, 109.98
                    boletoSafra.pdf, 2520.67
                    boletoSafra2.pdf, 28200.0
                    boletoBB2.pdf, 152.55
                    boletoBB.pdf, 8888.89
                    guiaFunrural.pdf, 1285.0
                    boletoFesaMT.pdf, 362.8
                    guiaDARF2.pdf, 306.64
                    guiaDARF3.pdf, 336721.53
                    guiaDARF4.pdf, 83.37
                    guiaGRF.pdf, 2817.85
                    guiaGRRF.pdf, 7282.13
                    darMT.pdf, 114.75
                    darMT2.pdf, 5284.48
                    darMT3.pdf, 3504.08
            """)
    void testValue(String fileName, String value) {
        setUpOnePage(fileName);
        Assertions.assertEquals(value, searchPdfBoleto.value().toString());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            gru.pdf, 207.29, 30/09/2023, 03477007000197
            gru2.pdf, 118.73, 30/09/2022, 42.130.122/0001-42
        """)
    void testGru(String fileName, String value, String date, String cnpjPayer){
        setUpOnePage(fileName);
        Assertions.assertEquals(value, searchPdfBoleto.value().toString());
        Assertions.assertEquals(date, pdfUtils.getFinalDate(searchPdfBoleto.date()));
        Assertions.assertEquals(cleanCnpj(cnpjPayer), searchPdfBoleto.cnpjPayer());
        Assertions.assertEquals("", searchPdfBoleto.cnpjSupplier());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            energisa.pdf, 1301.88, 13/07/2023, 27.406.174/0001-05, 03.467.321/0001-99
            energisa3.pdf, 294.13, 11/08/2023, , 03.467.321/0001-99
        """)

    void testEnergisa(String fileName, String value, String date, String cnpjPayer, String cnpjSuppier){
        setUpOnePage(fileName);
        Assertions.assertEquals(value, searchPdfBoleto.value().toString());
        Assertions.assertEquals(date, pdfUtils.getFinalDate(searchPdfBoleto.date()));
        Assertions.assertEquals(cleanCnpj(cnpjPayer), searchPdfBoleto.cnpjPayer());
        Assertions.assertEquals(cleanCnpj(cnpjSuppier), searchPdfBoleto.cnpjSupplier());
    }

}
