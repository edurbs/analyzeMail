package br.com.medeirosecia.analyzemail.domain.service.email;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import br.com.medeirosecia.analyzemail.domain.service.pdf.AnalyzePDF;

public class AnalyzePDFTest {

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
        AnalyzePDF analyzePDF = new AnalyzePDF(this.getPath(fileName));
        Assertions.assertEquals(date, this.getFinalDate(analyzePDF.getBoletoDate()));
        Assertions.assertTrue(analyzePDF.isBoleto());
    }
        
    

    @Test
    public void testOCRCheckIfIsNFPdfWithOnlyImage(){
        var analyzePDF = new AnalyzePDF(this.getPath("nfSoImagemNFSCuiaba.pdf"));        
        Assertions.assertTrue(analyzePDF.isNF());
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
