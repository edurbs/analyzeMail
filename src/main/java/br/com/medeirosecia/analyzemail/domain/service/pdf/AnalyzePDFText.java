package br.com.medeirosecia.analyzemail.domain.service.pdf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

import net.rationalminds.LocalDateModel;
import net.rationalminds.Parser;

public class AnalyzePDFText {
    private String pdfText;
    private String dateSplitter = "";
    private int keywordsFoundToBeNF = 0;
    private int keywordsFoundToBeNfse = 0;
    private int keywordsFoundToBeBoleto = 0;
    private String[] nfKeywords = { "nota fiscal", "serviços eletrônica", "emissão", "tomador de",
            "prestador de", "rps", "iss", "nfs-e", "autenticidade", "danfe", "documento auxiliar", "controle do fisco",
            "chave de acesso", "natureza da operação", "protocolo de autorização", "destinatário", "emitente",
            "dados dos produtos", "serviços" };
    private String[] nfseKeywords = { "tomador", "serviços", "prestador", "nfs-e", "rps", "iss", "prefeitura",
            "municipal", "issqn" };
    private String[] boletoKeywords = { "vencimento", "cedente", "referência", "pagador", "beneficiário",
            "nosso número", "valor do documento", "data do processamento", "mora", "multa", "juros", "carteira",
            "título", "pagável", "sacado", "débito automático", "total a pagar", "mês de referência",
            "serviços contratados",
            "autenticação mecânica", "período de apuração", "número do documento", "pagar este documento até",
            "documento de arrecadação", "pagar até", "pague com o pix"
    };

    public AnalyzePDFText(String pdfText) {
        this.pdfText = pdfText;
        this.checkKeyWords();
    }

    public boolean isNF() {
        if (keywordsFoundToBeNF > 5 && keywordsFoundToBeNF > keywordsFoundToBeBoleto) {
            return true;
        }
        return false;
    }

    public boolean isNfse() {
        if (keywordsFoundToBeNfse > 6 && this.isNF()) {
            return true;
        }
        return false;
    }

    public boolean isBoleto() {
        if (keywordsFoundToBeBoleto > 5 && keywordsFoundToBeBoleto > keywordsFoundToBeNF) {
            return true;
        }
        return false;
    }

    
    private void checkKeyWords() {

        for (String keyword : this.nfKeywords) {
            if (this.pdfText.contains(keyword)) {
                this.keywordsFoundToBeNF++;
            }
        }

        for (String keyword : this.boletoKeywords) {
            if (this.pdfText.contains(keyword)) {
                this.keywordsFoundToBeBoleto++;
            }
        }

        for (String keyword : this.nfseKeywords) {
            if (this.pdfText.contains(keyword)) {
                this.keywordsFoundToBeNfse++;
            }
        }

    }

    public String[] getBoletoDate() {
        Parser parser = new Parser();        
        int vencimento = this.pdfText.indexOf("vencimento");
        String text = this.pdfText.substring(vencimento, this.pdfText.length());

        List<LocalDateModel> dates = parser.parse(text);

        String date = "00/00/0000";

        // search for date 
        for (LocalDateModel localDateModel : dates) {
            if(this.isValidDate(localDateModel)){
                date = localDateModel.getOriginalText();                
                return date.split(this.dateSplitter);                
            }
        }
        return date.split("/");
    }

    private void setDateSplitter(String date){
        String[] splitters = {"/","-","."};
        // search for splitter
        for (String splitter : splitters) {
            if(date.contains(splitter)){
                if(splitter.equals(splitters[2]))
                {
                    this.dateSplitter = "\\.";
                }else{
                    this.dateSplitter = splitter;
                }
                return;
            }
        }
        this.dateSplitter = "";
    }

    private boolean isValidDate(LocalDateModel localDateModel){
        boolean isValid = false;
        

        String dateOriginal = localDateModel.getOriginalText();
        this.setDateSplitter(dateOriginal);
        String[] dateSplit = dateOriginal.split(this.dateSplitter);
        String day = dateSplit[0];
        if(day.length() == 1){
            return false;
        }

        String[] date = converDateTime(localDateModel).split("/");
        int year = Integer.parseInt(date[2]);
        int thisYear = java.time.LocalDate.now().getYear();
        if (year == thisYear + 1
                    || year == thisYear - 1
                    || year == thisYear) {
            return true;
        }

        return isValid;
    }

    private String converDateTime(LocalDateModel localDateModel){
        String date = localDateModel.getOriginalText();
        this.setDateSplitter(date);
        String[] dateSplit = date.split(this.dateSplitter);
        date = String.format("%02d",Integer.parseInt(dateSplit[0]))
                +"/"
                +String.format("%02d",Integer.parseInt(dateSplit[1]))
                +"/"
                +String.format("%04d",Integer.parseInt(dateSplit[2]));
        
        return date;
    }

    public String getChaveDeAcesso(){
        return this.getNextRowByString("chave de acesso", 54, 54);
    }

    public String getCNPJEmitente(){
        return this.getNextRowByString("cnpj", 14, 18);
    }

    public String[] getDataEmissao(){
        
        String targetWord = "emissão";

        // Define um padrão de expressão regular para datas
        Pattern pattern = Pattern.compile("\\d{2}[-/\\.]\\d{2}[-/\\.]\\d{4}");
        Matcher matcher = pattern.matcher(this.pdfText);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd.MM.yyyy");

        Date closestDate = null;
        Date date;
        try {
            
            while (matcher.find()) {
                String dataStr = matcher.group();
                
             
                if (dataStr.contains("-")) {
                    date = dateFormat.parse(dataStr);
                } else if (dataStr.contains("/")) {
                    date = dateFormat2.parse(dataStr);
                } else if (dataStr.contains(".")) {
                    date = dateFormat3.parse(dataStr);
                } else{
                    date = dateFormat.parse("01-01-0000");
                }
    
                // Encontre a posição da palavra "emissão" no texto
                int indexOfEmissao = this.pdfText.indexOf(targetWord);
    
                // Encontre a posição da data atual no texto
                int indexOfDate = this.pdfText.indexOf(dataStr);
    
                // Calcule a distância entre a data atual e a palavra "emissão"
                int howFar = Math.abs(indexOfDate - indexOfEmissao);
    
                // Verifique se esta data está mais próxima da palavra "emissão" do que a anterior
                if (howFar < indexOfEmissao) {
                    closestDate = date;
                }
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

        if (closestDate == null) {
            return new String[]{"","",""};
        }

        LocalDate localDate = closestDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int year = localDate.getYear();
        
        int thisYear = java.time.LocalDate.now().getYear();
        if (year < thisYear - 1 || year > thisYear + 1){
            return new String[]{"","",""};
        }

        return new String[]{(String.format("%02d",localDate.getDayOfMonth())),
                String.format("%02d",localDate.getMonthValue()),
                String.format("%04d",year)};
    
    }

    private String getNextRowByString(String search, int min, int max){
        String[] rows = this.pdfText.split("\n");
        int rowFound = 0;
        for (int i = 0; i < rows.length; i++) {
            if(rows[i].contains(search)){
                rowFound = i;
                break;
            }
        }
        for (int i = rowFound+1; i < rows.length; i++) {
            if(rows[i].trim().length() >= min && rows[i].trim().length() <= max){
                return rows[i].trim();
            }        
        }
    
        return "";
    }

}
