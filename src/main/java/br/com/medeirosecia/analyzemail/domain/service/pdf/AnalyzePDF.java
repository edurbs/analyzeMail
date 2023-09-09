package br.com.medeirosecia.analyzemail.domain.service.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import br.com.medeirosecia.analyzemail.domain.repository.Attachment;
import net.rationalminds.LocalDateModel;
import net.rationalminds.Parser;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class AnalyzePDF {
    private PDDocument pdfDocument;
    private String pdfText;
    private int keywordsForNF = 0;
    private int keywordsForBoleto = 0;
    private String[] nfKeywords = { "nota fiscal", "serviços eletrônica", "emissão", "tomador de",
            "prestador de", "rps", "iss", "nfs-e", "autenticidade", "danfe", "documento auxiliar", "controle do fisco",
            "chave de acesso", "natureza da operação", "protocolo de autorização", "destinatário", "emitente",
            "dados dos produtos", "serviços" };
    private String[] boletoKeywords = { "vencimento", "cedente", "referência", "pagador", "beneficiário",
            "nosso número", "valor do documento", "data do processamento", "mora", "multa", "juros", "carteira",
            "título", "pagável", "sacado", "débito automático", "total a pagar", "mês de referência", "serviços contratados",
            "autenticação mecânica", "período de apuração", "número do documento", "pagar este documento até",
            "documento de arrecadação", "pagar até", "pague com o pix"
         };

    public AnalyzePDF(Attachment attachment) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(attachment.getData());
            this.processPDF(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    public AnalyzePDF(String filePath){
        try {
            FileInputStream fis = new FileInputStream(filePath);            
            ByteArrayInputStream inputStream = new ByteArrayInputStream(IOUtils.toByteArray(fis));
            this.processPDF(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void processPDF(ByteArrayInputStream inputStream){
        try {
            this.pdfDocument = PDDocument.load(CloseShieldInputStream.wrap(inputStream));
            this.checkKeyWords();
            this.pdfDocument.close();
        } catch (IOException e) {            
            e.printStackTrace();
        }
    }
    
    public boolean isNF(){
        if(keywordsForNF > 5 && keywordsForNF>keywordsForBoleto){
            return true;
        }        
        return false;
    }

    public boolean isBoleto(){
        if(keywordsForBoleto > 5 && keywordsForBoleto>keywordsForNF){
            return true;
        }        
        return false;
    }

    public String[] getBoletoDate(){        
        Parser parser = new Parser();
        int vencimento = this.pdfText.indexOf("vencimento");
        String text = this.pdfText.substring(vencimento, this.pdfText.length());
        
        List<LocalDateModel> dates = parser.parse(text);

        String date="00/00/0000";   

        // search for a date with /
        for (LocalDateModel localDateModel : dates) {
            if (localDateModel.getOriginalText().contains("/")) {
                
                // check if date is of this year, next year or last year
                int year = Integer.parseInt(localDateModel.getOriginalText().split("/")[2]);
                int thisYear = java.time.LocalDate.now().getYear();
                if (year == thisYear + 1
                        || year == thisYear - 1
                        || year == thisYear) {
                    date = localDateModel.getOriginalText();
                    return date.split("/");
                }

            }
        }

        return date.split("/");

    }


    private void checkKeyWords() {
        try {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            this.pdfText = pdfTextStripper.getText(this.pdfDocument).toLowerCase();

            // if PDF has no text, works with OCR
            if(this.pdfText.length()<10){
                getOCR();
            }
            
            for (String keyword : nfKeywords) {
                if (pdfText.contains(keyword)) {
                    keywordsForNF++;
                }
            }
            
            for (String keyword : boletoKeywords) {
                if (pdfText.contains(keyword)) {
                    keywordsForBoleto++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 

    }

    private void getOCR() throws IOException, URISyntaxException, TesseractException {
        PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300, ImageType.GRAY);

        Tesseract tesseract = new Tesseract();

        URL url = AnalyzePDF.class.getResource("/tesseract/");
        String tessractDataPath = Paths.get(url.toURI()).toString();
        tesseract.setDatapath(tessractDataPath);

        tesseract.setLanguage("por");
        tesseract.setPageSegMode(1); // Automatic Page Segmentation with OSD
        
        String result = tesseract.doOCR(bufferedImage);
        this.pdfText = result.toLowerCase();
    }

}
