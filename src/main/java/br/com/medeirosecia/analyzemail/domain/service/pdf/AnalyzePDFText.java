package br.com.medeirosecia.analyzemail.domain.service.pdf;

import java.util.List;

import net.rationalminds.LocalDateModel;
import net.rationalminds.Parser;

public class AnalyzePDFText {
    private String pdfText;
    private int keywordsForNF = 0;
    private int keywordsForBoleto = 0;
    private String[] nfKeywords = { "nota fiscal", "serviços eletrônica", "emissão", "tomador de",
            "prestador de", "rps", "iss", "nfs-e", "autenticidade", "danfe", "documento auxiliar", "controle do fisco",
            "chave de acesso", "natureza da operação", "protocolo de autorização", "destinatário", "emitente",
            "dados dos produtos", "serviços" };
    private String[] nfseKeywords = {"tomador", "serviços", "prestador", "nfs-e", "rps", "iss", "prefeitura",
            "municipal", "issqn"};
    private String[] boletoKeywords = { "vencimento", "cedente", "referência", "pagador", "beneficiário",
            "nosso número", "valor do documento", "data do processamento", "mora", "multa", "juros", "carteira",
            "título", "pagável", "sacado", "débito automático", "total a pagar", "mês de referência", "serviços contratados",
            "autenticação mecânica", "período de apuração", "número do documento", "pagar este documento até",
            "documento de arrecadação", "pagar até", "pague com o pix"
         };

    public AnalyzePDFText(String pdfText){
        this.pdfText = pdfText;
        this.checkKeyWords();
    }
    
    public boolean isNF(){
        if(keywordsForNF > 5 && keywordsForNF>keywordsForBoleto){
            return true;
        }        
        return false;
    }

    public boolean isNfse(){
        if(nfseKeywords.length>8 && this.isNF()){
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
        
        for (String keyword : this.nfKeywords) {
            if (this.pdfText.contains(keyword)) {
                this.keywordsForNF++;
            }
        }
        
        for (String keyword : this.boletoKeywords) {
            if (this.pdfText.contains(keyword)) {
                this.keywordsForBoleto++;
            }
        }

    }


}
