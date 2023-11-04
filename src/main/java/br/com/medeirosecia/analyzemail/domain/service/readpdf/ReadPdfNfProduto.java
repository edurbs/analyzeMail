package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadPdfNfProduto extends ReadPdfAbstract {

    private String accessKey;

    public ReadPdfNfProduto(String textToSearchIn) {
        super(textToSearchIn);
        this.accessKey = accessKey();
    }

    @Override
    public String accessKey(){
        if (accessKey != null && !accessKey.isBlank()) {
            return this.accessKey;
        }

        String[] regexPatterns = {
            "\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}",
            "\\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4}",
            "\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}",
            "\\d{2}\\.\\d{4}\\.\\d{12}\\.\\d{2}\\.\\d{3}\\.\\d{9}\\.\\d{1}\\.\\d{8}\\.\\d{1}\\.\\d{8}",
            "(\\d{2}\\.\\d{2}\\.\\d{2}\\.\\d{14}\\.\\d{2}\\.\\d{3}\\.\\d{9}\\.\\d{9}\\-\\d{1})"
        };

        for (String regex : regexPatterns) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(textToSearchIn);
            String found;
            if(matcher.find()){
                found = matcher.group();
                found = found.replaceAll("\\.", "");
                found = found.replaceAll("\\s+", "");
                if(found.length() == 44){
                    return found;
                }
            }
        }
        return "";
    }

    public String[] date(){
        if(accessKey == null || accessKey.isBlank() || accessKey.length() != 44){
            return new String[] {"00", "00", "0000"};
        }
        var day = "01";
        var month = accessKey.substring(4, 6);
        var year = "20" + accessKey.substring(2, 4);

        return new String[] {day, month, year};

        //return new DateSearch(textToSearchIn).near("autorização");
    }

    public String cnpjPayer(){
        // FEAT implement pagador CNPJ de NF
        return "";
    }

    public String cnpjSupplier(){
        if(accessKey == null || accessKey.isBlank()){
            return "";
        }


        return accessKey.substring(6, 20);


        //return new CnpjSearch(textToSearchIn).nextTo("chave de acesso");
    }


    @Override
    public void setText(String textToSearchIn) {
        this.textToSearchIn = textToSearchIn;
    }


    @Override
    public Double value() {
        return 0d;
    }


}
