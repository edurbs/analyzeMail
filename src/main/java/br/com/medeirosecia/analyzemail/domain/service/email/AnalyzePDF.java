package br.com.medeirosecia.analyzemail.domain.service.email;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import br.com.medeirosecia.analyzemail.domain.repository.Attachment;
import net.rationalminds.LocalDateModel;
import net.rationalminds.Parser;

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
            "autenticação mecânica"
         };

    public AnalyzePDF(Attachment attachment) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(attachment.getData());
            this.pdfDocument = PDDocument.load(CloseShieldInputStream.wrap(inputStream));
            this.checkKeyWords();
            this.pdfDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }

    
    public boolean isNF(){
        if(keywordsForNF > 5 && (keywordsForNF>keywordsForBoleto)){
            return true;
        }        
        return false;
    }

    public boolean isBoleto(){
        if(keywordsForBoleto > 5 && (keywordsForBoleto>keywordsForNF)){
            return true;
        }        
        return false;
    }
    
    public AnalyzePDF(){
        
    }

    public String[] getBoletoDate(){        
        Parser parser = new Parser();
        int vencimento = this.pdfText.indexOf("vencimento");
        String text = this.pdfText.substring(vencimento, this.pdfText.length());
        
        List<LocalDateModel> dates = parser.parse(text);

        var date = dates.get(0).getOriginalText();

        return date.split("/");

    }

    public void setPdfText(String pdfText) {
        this.pdfText = pdfText.toLowerCase();
    }
     

    private void checkKeyWords() {
        try {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            this.pdfText = pdfTextStripper.getText(pdfDocument).toLowerCase();
            
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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
