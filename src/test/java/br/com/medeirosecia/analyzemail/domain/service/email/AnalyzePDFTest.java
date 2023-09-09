package br.com.medeirosecia.analyzemail.domain.service.email;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnalyzePDFTest {
    @Test
    void testGetBoletoSicredi() {
        AnalyzePDF analyzePDF = new AnalyzePDF(this.getPath("boletoSicredi.pdf"));
        String finalDate = this.getFinalDate(analyzePDF.getBoletoDate());
        Assertions.assertEquals("11/09/2023", finalDate);
        Assertions.assertTrue(analyzePDF.isBoleto());
    }

    @Test
    void testGetBoletoSicredi2() {
        AnalyzePDF analyzePDF = new AnalyzePDF(this.getPath("boletoSicredi2.pdf"));
        String finalDate = this.getFinalDate(analyzePDF.getBoletoDate());
        Assertions.assertEquals("10/10/2023", finalDate);
        Assertions.assertTrue(analyzePDF.isBoleto());
    }

    @Test
    void testGetBoletoVivo() {
        AnalyzePDF analyzePDF = new AnalyzePDF(this.getPath("boletoVivo.pdf"));
        String finalDate = this.getFinalDate(analyzePDF.getBoletoDate());
        Assertions.assertEquals("25/09/2023", finalDate);
        Assertions.assertTrue(analyzePDF.isBoleto());
    }

    @Test
    public void testGetBoletoSafra(){        
        AnalyzePDF analyzePDF = new AnalyzePDF(this.getPath("boletoSafra.pdf"));
        String finalDate = this.getFinalDate(analyzePDF.getBoletoDate());
        Assertions.assertEquals("21/08/2023", finalDate);
        Assertions.assertTrue(analyzePDF.isBoleto());
    }

    @Test
    public void testGetBoletoBB2(){
        AnalyzePDF analyzePDF = new AnalyzePDF(this.getPath("boletoBB2.pdf"));
        String finalDate = this.getFinalDate(analyzePDF.getBoletoDate());
        Assertions.assertEquals("10/09/2023", finalDate);
        Assertions.assertTrue(analyzePDF.isBoleto());
    }

    @Test
    public void testGetBoletoBB(){        
        AnalyzePDF analyzePDF = new AnalyzePDF(this.getPath("boletoBB.pdf"));
        String finalDate = this.getFinalDate(analyzePDF.getBoletoDate());
        Assertions.assertEquals("10/09/2023", finalDate);
        Assertions.assertTrue(analyzePDF.isBoleto());
    }

    @Test
    public void testOCRCheckIfIsNFPdfWithOnlyImage(){
        var analyzePDF = new AnalyzePDF(this.getPath("nfSoImagemNFSCuiaba.pdf"));        
        Assertions.assertTrue(analyzePDF.isNF());
    }
    

    @Test
    public void testIfIsBoletoGuiaFunrural(){        
        AnalyzePDF analyzePDF = new AnalyzePDF(this.getPath("guiaFunrural.pdf"));
        Assertions.assertTrue(analyzePDF.isBoleto());
    }

    @Test
    public void testGuiaFunrural(){        
        AnalyzePDF analyzePDF = new AnalyzePDF(this.getPath("guiaFunrural.pdf"));
        String finalDate = this.getFinalDate(analyzePDF.getBoletoDate());
        Assertions.assertEquals("20/09/2023", finalDate);
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
