package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import br.com.medeirosecia.analyzemail.domain.service.cnpj.CnpjSearch;
import br.com.medeirosecia.analyzemail.domain.service.date.DateSearch;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.boleto.BoletoBarCodeTool;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.boleto.BoletoType;
import br.com.medeirosecia.analyzemail.infra.filesystem.ReadCnpjFile;

public class ReadPdfBoleto extends ReadPdfAbstract {

    private static final String LOCAL_DATE_FORMAT = "dd/MM/yyyy";

    private String[] date;
    private String accessKey;
    private BoletoType boletoType;
    private List<String> allCnpjInPdf = new ArrayList<>();

    public ReadPdfBoleto(String textToSearchIn) {
        super(textToSearchIn);
        setType();
        accessKey();
    }



    private void setType() {

        boletoType = BoletoType.COMUM;

        Map<BoletoType, Integer> keywordCountMap = new EnumMap<>(BoletoType.class);

        for (BoletoType type : BoletoType.values()) {
            String[] keywords = type.getBoletoKeyWords();
            Set<String> keywordsSet = new HashSet<>(Arrays.asList(keywords));

            int keywordsFound = 0;
            for (String keyword : keywordsSet) {
                if(textToSearchIn.contains(keyword.toLowerCase())){
                    keywordsFound++;
                }
            }
            keywordCountMap.put(type, keywordsFound);
        }

        int maxKeywords = 0;
        for (Map.Entry<BoletoType, Integer> entry : keywordCountMap.entrySet()) {
            if (entry.getValue() > maxKeywords) {
                maxKeywords = entry.getValue();
                boletoType = entry.getKey();
            }
        }


    }

    @Override
    public String[] date() {
        if (date == null) {
            date = new String[3];
            BoletoBarCodeTool accessKeyTool = new BoletoBarCodeTool(accessKey, boletoType);
            date = accessKeyTool.getDueDate();
        }

        if(date[2].equals("0000")){
            date = dateInText();
        }
        return date;

    }


    private String[] dateInText() {

        DateSearch dateSearch = new DateSearch(textToSearchIn);
        List<String[]> allDates = dateSearch.allDates();
        List<String> stringDates = formatDates(allDates);
        stringDates.sort(this::compareDates);
        if(stringDates.isEmpty()){
            return new String[]{"00", "00", "0000"};
        }else{
            return stringDates.get(0).split("/");
        }


    }

    private List<String> formatDates(List<String[]> allDates) {
        return allDates.stream()
                .map(tempDate -> tempDate[0] + "/" + tempDate[1] + "/" + tempDate[2])
                .collect(Collectors.toList());
    }

    private int compareDates(String a, String b) {
        try {
            Date dateA = new SimpleDateFormat(LOCAL_DATE_FORMAT).parse(a);
            Date dateB = new SimpleDateFormat(LOCAL_DATE_FORMAT).parse(b);
            return dateB.compareTo(dateA);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void searchAllCnpjInPdf() {
        if (allCnpjInPdf.isEmpty()) {
            allCnpjInPdf = new CnpjSearch(textToSearchIn).all();
        }
    }

    private List<String> searchAllCnpjWithoutSeparator() {
        return new CnpjSearch(textToSearchIn).withoutSeparator();
    }

    @Override
    public String cnpjPayer() {

        List<String> cnpjListPayers = new ReadCnpjFile().getCnpjListPayers();

        String cnpjPayer="";

        searchAllCnpjInPdf();

        cnpjPayer = listContains(allCnpjInPdf, cnpjListPayers);

        if(cnpjPayer.isBlank()){
            var listCnpjWithOutSeparator = searchAllCnpjWithoutSeparator();
            cnpjPayer = listContains(listCnpjWithOutSeparator, cnpjListPayers);
        }

        return cnpjPayer;

    }



    private String listContains(List<String> cnpjListPayers, List<String> cnpjListToSearch) {
        for (String cnpj : cnpjListPayers) {
            if(cnpjListToSearch.contains(cnpj)){ // is on the list of Payers
                return cnpj;
            }
        }
        return "";
    }

    @Override
    public String cnpjSupplier() {

        List<String> cnpjListPayers = new ReadCnpjFile().getCnpjListPayers();

        String cnpjSupplier = "";

        searchAllCnpjInPdf();

        cnpjSupplier = listNotContains(cnpjListPayers);

        if(cnpjSupplier.isBlank()){
            var listCnpjWithOutSeparator = searchAllCnpjWithoutSeparator();
            cnpjSupplier = listNotContains(listCnpjWithOutSeparator);
        }

        return cnpjSupplier;

    }

    private String listNotContains(List<String> cnpjListPayers ){
        for (String cnpj : allCnpjInPdf) {
            if(!cnpjListPayers.contains(cnpj)){ // not in list of Payers
                return cnpj;
            }
        }
        return "";
    }

    @Override
    public Double value() {

        BoletoBarCodeTool accessKeyTool = new BoletoBarCodeTool(accessKey, boletoType);
        Double value = accessKeyTool.getValue();

        if(value == 0d){
            value = valueFromText();
        }

        return value;

    }


    public Double valueFromText() {

        final String regex = "\\b\\d{1,3}(?:\\.\\d{3})*(?:,\\d{2})\\b|\\b\\d+(?:,\\d{2})\\b";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

        // Find all matches of the pattern in the text
        Matcher matcher = pattern.matcher(textToSearchIn);

        double highestValue = 0.0;
        while (matcher.find()) {
            // Extract the amount from the match
            String amountStr = matcher.group();
            amountStr = amountStr.replace("r$", ""); // Remove r$
            amountStr = amountStr.replace(".", ""); // Remove commas
            amountStr = amountStr.replace(",", "."); // Remove commas

            // Convert the amount to double
            double amount = Double.parseDouble(amountStr);

            // Update the highest value if necessary
            if (amount > highestValue) {
                highestValue = amount;
            }
        }

        return highestValue;
    }

    @Override
    public void setText(String textToSearchIn) {
        this.textToSearchIn = textToSearchIn;
    }

    @Override
    public String accessKey() {
        if (accessKey == null) {
            // código do boleto
            final String[] regex = {
                    "\\d{5}\\.\\d{5} \\d{5}\\.\\d{6} \\d{5}\\.\\d{6} \\d{1} \\d{14}", // normal boleto
                    "\\d{11} \\d \\d{11} \\d \\d{11} \\d \\d{11} \\d", // guia Receita Federal
                    "\\d{5}\\.\\d{5}\\s\\s+\\d{5}\\.\\d{6}\\s\\s+\\d{5}\\.\\d{6}\\s\\s+\\d{1}\\s+\\d{14}", // safra
                    "\\d{12}\\s+\\d{12}\\s\\d{12}\\s+\\d{12}", // boleto vivo
                    "\\d{5}\\.\\d{5}\\W\\d{5}\\.\\d{6}\\W\\d{5}\\.\\d{6}\\W\\d{1,}\\W{1,6}\\d{14}", // caracteres
                    "(\\d{5}\\.\\d{5}\\W\\W\\d{5}\\.\\d{6}\\W\\W\\d{5}\\.\\d{6}\\W\\W\\d{1}\\W\\W\\d{14})", //sicoob
                    "((\\d{11}-\\d(\\W?)){4})", // boleto GRU simples
                    "((\\d{12}\\W){4})", // boleto fgts
                    "((\\d{12}\\W{2}){4})", // boleto fgts
                    "((\\d{11}\\W\\d(\\W?)){4})" // boleto MT
            };

            for (String string : regex) {

                Pattern patters = Pattern.compile(string, Pattern.MULTILINE);
                Matcher matcher = patters.matcher(textToSearchIn);

                if (matcher.find()) {
                    accessKey = matcher.group();
                    accessKey = accessKey.replaceAll("[\\D]", "");
                }
            }

        }

        return accessKey;
    }

}
