package br.com.medeirosecia.analyzemail.domain.service.cnpj;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CnpjSearch {
    String textToSearchIn;


    public CnpjSearch(String textToSearchIn){
        this.textToSearchIn = textToSearchIn;
    }

    public List<String> all(){
        List<String> allCnpj = new ArrayList<>();

        String cnpjPattern = "\\d{2}(\\.?)\\d{3}(\\.?)\\d{3}/\\d{4}(-?\\.?)\\d{2}";
        Pattern pattern = Pattern.compile(cnpjPattern);
        Matcher matcher = pattern.matcher(textToSearchIn);

        while (matcher.find()) {
            String cnpj = matcher.group();
            cnpj = cnpj.replaceAll("[\\D]", "");
            allCnpj.add(cnpj);
        }

        return allCnpj;
    }

    public String nextTo(String targetWord){


        String cnpjPattern = "\\d{2}(\\.?)\\d{3}(\\.?)\\d{3}/\\d{4}(-?\\.?)\\d{2}";
        Pattern pattern1 = Pattern.compile(cnpjPattern);
        Matcher matcher = pattern1.matcher(textToSearchIn);

        String cnpj = "";
        String cnpjFound="";
        int lastHowFar = Integer.MAX_VALUE;

        while (matcher.find()) {
            cnpjFound = matcher.group();

            int indexOfTargetWord = textToSearchIn.indexOf(targetWord);
            int indexOfCnpjFound = textToSearchIn.indexOf(cnpjFound);
            int howFar = Math.abs(indexOfCnpjFound - indexOfTargetWord);

            if (howFar < lastHowFar) {
                lastHowFar = howFar;
                cnpj = cnpjFound;
            }
        }

        if(cnpj.isBlank() || cnpj.isEmpty()){
            Pattern pattern2 = Pattern.compile("\\d{14}");
            if(matcher.find()){
                cnpj = matcher.group();
            }else{
                matcher = pattern2.matcher(textToSearchIn);
                if(matcher.find()){
                    cnpj = matcher.group();
                }
            }
        }

        return cnpj;
    }
}
