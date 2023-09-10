package br.com.medeirosecia.analyzemail.domain.service.email;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import br.com.medeirosecia.analyzemail.domain.service.pdf.AnalyzePDFText;
import br.com.medeirosecia.analyzemail.domain.service.pdf.ReadPDF;

public class AnalyzePDFTest {

  
    AnalyzePDFText analyzePDF;
    ReadPDF readPDF;
    
    void setUp(String fileName) {
        this.readPDF = new ReadPDF(this.getPath(fileName));
        this.analyzePDF = new AnalyzePDFText(this.readPDF.getPDFText());
    }

    @ParameterizedTest
    @CsvSource({ "boletoSicredi.pdf, 11/09/2023", 
            "boletoSicredi2.pdf, 10/10/2023", 
            "boletoVivo.pdf, 25/09/2023", 
            "boletoSafra.pdf, 21/08/2023", 
            "boletoBB2.pdf, 10/09/2023",
            "boletoBB.pdf, 10/09/2023",
            "guiaFunrural.pdf, 20/09/2023"
        })
    void testBoletosAndDate(String fileName, String date) {
        this.setUp(fileName);
        Assertions.assertEquals(date, this.getFinalDate(this.analyzePDF.getBoletoDate()));
        Assertions.assertTrue(this.analyzePDF.isBoleto());
        Assertions.assertFalse(this.analyzePDF.isNF());
    }

    @ParameterizedTest
    @ValueSource(strings = {"nfse1.pdf", "nfse2.pdf", "nfSoImagemNFSCuiaba.pdf"})
    void testIfIsNfse(String fileName) {
        this.setUp(fileName);
        Assertions.assertTrue(this.analyzePDF.isNfse());
        Assertions.assertFalse(this.analyzePDF.isBoleto());        
    }    
    
    @Test
    public void testOCRCheckIfIsNFPdfWithOnlyImage(){
        this.setUp("nfSoImagemNFSCuiaba.pdf");        
        Assertions.assertTrue(this.analyzePDF.isNF());
        Assertions.assertFalse(this.analyzePDF.isBoleto());        
    }

    @ParameterizedTest
    @ValueSource(strings = {"nfe1.pdf", "nfe2.pdf", "nfe3.pdf"})
    void testIfIsNf(String fileName) {
        this.setUp(fileName);
        Assertions.assertTrue(this.analyzePDF.isNF());
        Assertions.assertFalse(this.analyzePDF.isNfse());
        Assertions.assertFalse(this.analyzePDF.isBoleto());
    }

    private String getFinalDate(String[] date){
        String finalDate = date[0]+"/"+date[1]+"/"+date[2];
        return finalDate;
    }

    private String getPath(String url){
        try {
            URL resource = AnalyzePDFTest.class.getResource("/pdfTest/"+url);
            return Paths.get(resource.toURI()).toString();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";    
    }
}
