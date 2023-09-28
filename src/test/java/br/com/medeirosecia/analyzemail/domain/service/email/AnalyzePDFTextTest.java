package br.com.medeirosecia.analyzemail.domain.service.email;

import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;

public class AnalyzePDFTextTest {

  
    HandlePDF handlePDF;
    
    private String getFinalDate(String[] date){
        String finalDate = date[0]+"/"+date[1]+"/"+date[2];
        return finalDate;
    }

    private String getPath(String url){
        try {
            URL resource = AnalyzePDFTextTest.class.getResource("/pdfTest/"+url);
            return Paths.get(resource.toURI()).toString();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";    
    }
    
    void setUp(String fileName) {
        
        try {
            FileInputStream fis = new FileInputStream(this.getPath(fileName));            
            //ByteArrayInputStream inputStream = new ByteArrayInputStream(IOUtils.toByteArray(fis));
            EmailAttachmentDAO emailAttachment = new EmailAttachmentDAO(fileName, IOUtils.toByteArray(fis));            
            this.handlePDF = new HandlePDF();
            this.handlePDF.analyzeAttachment(emailAttachment, null);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Assertions.assertEquals(date, this.getFinalDate(this.handlePDF.getBoletoDate()));
        Assertions.assertTrue(this.handlePDF.isBoleto());
        Assertions.assertFalse(this.handlePDF.isNF());
    }

    @ParameterizedTest
    @ValueSource(strings = {"nfse1.pdf", "nfse2.pdf", "nfSoImagemNFSCuiaba.pdf"})
    void testIfIsNfse(String fileName) {
        this.setUp(fileName);
        Assertions.assertTrue(this.handlePDF.isNfse());
        Assertions.assertFalse(this.handlePDF.isBoleto());        
    }    
    
    @Test
    public void testOCRCheckIfIsNFPdfWithOnlyImage(){
        this.setUp("nfSoImagemNFSCuiaba.pdf");        
        Assertions.assertTrue(this.handlePDF.isNF());
        Assertions.assertFalse(this.handlePDF.isBoleto());        
    }

    @ParameterizedTest
    @ValueSource(strings = {"nfe1.pdf", "nfe2.pdf", "nfe3.pdf"})
    void testIfIsNf(String fileName) {
        this.setUp(fileName);
        Assertions.assertTrue(this.handlePDF.isNF());
        Assertions.assertFalse(this.handlePDF.isNfse());
        Assertions.assertFalse(this.handlePDF.isBoleto());
    }

    @ParameterizedTest
    @CsvSource({"nfe1.pdf, 5123 0900 9543 0500 0132 5500 2000 2233 5710 5277 3652", 
            "nfe2.pdf, 5123 0847 1806 2500 3595 5500 5000 0391 5017 4776 1404",
            "nfe3.pdf, 5123 0900 3198 3500 0109 5500 3000 0913 3117 7530 2942",
            "nfe4.pdf, 5123 0612 8685 9800 0114 5500 1000 0105 4014 0078 3319",
            "nfe5.pdf, 5123 0903 2744 8100 0111 5500 1000 3400 6115 6530 5205",
            "nfe6.pdf, 5123 0861 0649 2900 7343 5500 1000 0589 1212 1746 6957",
            "nfe7.pdf, 5123 0928 4881 0100 0172 5500 1000 0023 8810 0002 1644",
            "nfe8.pdf, 5121 0505 3803 2100 0778 5500 1000 0055 7412 5819 1160",
            "nfe9.pdf, 5123 0904 7337 6700 0261 5500 1000 0273 4011 1828 6076",
            "nfe10.pdf, 5123 0906 1166 0000 0104 5500 1000 0420 2515 8226 9257",
            "nfe11.pdf, 51.2309.00959825000138.55.002.000199813.1.00694763.4"
        })
    void testGetChaveDeAcesso(String fileName, String chave) {
        this.setUp(fileName);
        Assertions.assertEquals(chave, this.handlePDF.getChaveDeAcesso());
    }

    @ParameterizedTest
    @CsvSource({"nfe1.pdf, 00954305000132", 
            "nfe2.pdf, 47.180.625/0035-95",
            "nfe3.pdf, 00.319.835/0001-09",
            "nfe4.pdf, 12.868.598/0001-14",
            "nfe5.pdf, 03.274.481/0001-11",
            "nfe6.pdf, 61.064.929/0073-43",
            "nfe7.pdf, 28.488.101/0001-72",
            "nfe8.pdf, 05.380.321/0007−78",
            "nfe9.pdf, 04.733.767/0002-61",
            "nfe10.pdf, 06.116.600/0001-04",
            "nfe11.pdf, 00.959.825/0001-38"
        })
    void testGetCNPJEmitente(String fileName, String cnpj) {
        this.setUp(fileName);
        Assertions.assertEquals(cnpj, this.handlePDF.getCNPJEmitente());
    }

        @ParameterizedTest
    @CsvSource({"nfe1.pdf, 06/09/2023", 
            "nfe2.pdf, 28/08/2023",
            "nfe3.pdf, 06/09/2023",
            "nfe4.pdf, 14/06/2023",
            "nfe5.pdf, 06/09/2023",
            "nfe6.pdf, 30/08/2023",
            "nfe7.pdf, 08/09/2023",
            "nfe8.pdf, 21/05/2021",
            "nfe9.pdf, 08/09/2023",
            "nfe10.pdf, 08/09/2023",
            "nfe11.pdf, 19/09/2023"
        })
    void testGetDataEmissao(String fileName, String date) {
        this.setUp(fileName);
        Assertions.assertEquals(date, this.getFinalDate(this.handlePDF.getDataEmissao()));
    }


}
