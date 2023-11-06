package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import br.com.medeirosecia.analyzemail.domain.service.cnpj.CnpjSearch;
import br.com.medeirosecia.analyzemail.domain.service.date.DateSearch;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.boleto.BoletoBarCodeTool;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.boleto.BoletoType;
import br.com.medeirosecia.analyzemail.infra.filesystem.ReadCnpjFile;

public class ReadPdfBoleto implements ReadPdfInterface {

    private static final String LOCAL_DATE_FORMAT = "dd/MM/yyyy";

    private String[] date;
    private String accessKey;
    private String textToSearchIn;
    private BoletoType boletoType;
    private List<String> allCnpjInPdf = new ArrayList<>();

    @Override
    public String[] date() {
        if (date == null) {
            date = new String[3];
            BoletoBarCodeTool accessKeyTool = new BoletoBarCodeTool(accessKey, boletoType);
            date = accessKeyTool.getDueDate();
        }

        if (date[2].equals("0000")) {
            date = dateInText();
        }
        return date;

    }

    private String[] dateInText() {

        if (textToSearchIn == null || textToSearchIn.isBlank()) {
            return new String[3];
        }

        DateSearch dateSearch = new DateSearch(textToSearchIn);
        List<String[]> allDates = dateSearch.allDates();
        List<String> stringDates = formatDates(allDates);
        stringDates.sort(this::compareDates);
        if (stringDates.isEmpty()) {
            return new String[] { "00", "00", "0000" };
        } else {
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

    private List<String> searchAllCnpjInPdf() {
        if (allCnpjInPdf.isEmpty() && textToSearchIn != null && !textToSearchIn.isBlank()) {
            allCnpjInPdf = new CnpjSearch(textToSearchIn).all();
        }
        return allCnpjInPdf;
    }

    private List<String> searchAllCnpjWithoutSeparator() {
        return new CnpjSearch(textToSearchIn).withoutSeparator();
    }

    @Override
    public String cnpjPayer() {

        List<String> cnpjListPayers = new ReadCnpjFile().getCnpjListPayers();

        String cnpjPayer = "";

        allCnpjInPdf = searchAllCnpjInPdf();

        cnpjPayer = listContains(allCnpjInPdf, cnpjListPayers);

        if (cnpjPayer.isBlank()) {
            var listCnpjWithOutSeparator = searchAllCnpjWithoutSeparator();
            cnpjPayer = listContains(listCnpjWithOutSeparator, cnpjListPayers);
        }

        if (!isValidCnpj(cnpjPayer)) {
            cnpjPayer = "";
        }

        return cnpjPayer;

    }

    /**
     * Check if this CNPJ is valid
     *
     * @param cnpj String - 99.999.999/9999-99 or 99999999999999 format
     * @return boolean
     */
    private boolean isValidCnpj(String cnpj) {
        cnpj = cnpj.replace(".", "");
        cnpj = cnpj.replace("-", "");
        cnpj = cnpj.replace("/", "");
        cnpj = cnpj.replace(" ", "");

        try {
            Long.parseLong(cnpj);
        } catch (NumberFormatException e) {
            return false;
        }

        for (int i = 0; i <= 9; i++) {
            String number = String.valueOf(i);
            String invalidCnpj = number.repeat(14);
            if(cnpj.equals(invalidCnpj)) {
                return false;
            }
        }

        if(cnpj.length() != 14) {
            return false;
        }


        try {
            char dig13 = getCnpjDig13(cnpj);
            char dig14 = getCnpjDig14(cnpj);

            if (dig13 == cnpj.charAt(12) && dig14 == cnpj.charAt(13)) {
                return true;
            }
        } catch (InputMismatchException erro) {
            return false;
        }
        return false;
    }

    private char getCnpjDig14(String cnpj) throws InputMismatchException {
        char dig14;
        int sm;
        int i;
        int r;
        int num;
        int peso;
        sm = 0;
        peso = 2;
        for (i = 12; i >= 0; i--) {
            num = (int) (cnpj.charAt(i) - 48);
            sm = sm + (num * peso);
            peso = peso + 1;
            if (peso == 10){
                peso = 2;
            }
        }

        r = sm % 11;
        if (r == 0 || r == 1) {
            dig14 = '0';
        } else {
            dig14 = (char) ((11 - r) + 48);
        }
        return dig14;
    }

    private char getCnpjDig13(String cnpj) throws InputMismatchException{
        char dig13;
        int sm;
        int i;
        int r;
        int num;
        int peso;
        sm = 0;
        peso = 2;

        for (i = 11; i >= 0; i--) {
            num = (int) (cnpj.charAt(i) - 48);
            sm = sm + (num * peso);
            peso = peso + 1;
            if (peso == 10) {
                peso = 2;
            }
        }

        r = sm % 11;
        if (r == 0 || r == 1) {
            dig13 = '0';
        } else {
            dig13 = (char) ((11 - r) + 48);
        }

        return dig13;
    }

    private String listContains(List<String> cnpjListPayers, List<String> cnpjListToSearch) {
        for (String cnpj : cnpjListPayers) {
            if (cnpjListToSearch.contains(cnpj)) { // is on the list of Payers
                return cnpj;
            }
        }
        return "";
    }

    @Override
    public String cnpjSupplier() {

        List<String> cnpjListPayers = new ReadCnpjFile().getCnpjListPayers();

        String cnpjSupplier = "";

        allCnpjInPdf = searchAllCnpjInPdf();

        cnpjSupplier = listNotContains(cnpjListPayers);

        if (cnpjSupplier.isBlank()) {
            var listCnpjWithOutSeparator = searchAllCnpjWithoutSeparator();
            allCnpjInPdf = listCnpjWithOutSeparator;
            cnpjSupplier = listNotContains(cnpjListPayers);
        }

        if (!isValidCnpj(cnpjSupplier)) {
            cnpjSupplier = "";
        }

        return cnpjSupplier;

    }

    private String listNotContains(List<String> cnpjListPayers) {
        for (String cnpj : allCnpjInPdf) {
            if (!cnpjListPayers.contains(cnpj)) { // not in list of Payers
                return cnpj;
            }
        }
        return "";
    }

    @Override
    public Double value() {

        BoletoBarCodeTool accessKeyTool = new BoletoBarCodeTool(accessKey, boletoType);
        Double value = accessKeyTool.getValue();

        if (value == 0d) {
            value = valueFromText();
        }

        return value;

    }

    public Double valueFromText() {

        if (textToSearchIn == null || textToSearchIn.isBlank()) {
            return 0d;
        }

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
        boletoType = new DefinePdfType(textToSearchIn).getBoletoType();
        accessKey();
    }

    @Override
    public String accessKey() {
        if (accessKey == null && textToSearchIn != null && !textToSearchIn.isBlank()) {
            // código do boleto
            final String[] regex = {
                    "\\d{5}\\.\\d{5} \\d{5}\\.\\d{6} \\d{5}\\.\\d{6} \\d{1} \\d{14}", // normal boleto
                    "\\d{11} \\d \\d{11} \\d \\d{11} \\d \\d{11} \\d", // guia Receita Federal
                    "\\d{5}\\.\\d{5}\\s\\s+\\d{5}\\.\\d{6}\\s\\s+\\d{5}\\.\\d{6}\\s\\s+\\d{1}\\s+\\d{14}", // safra
                    "\\d{12}\\s+\\d{12}\\s\\d{12}\\s+\\d{12}", // boleto vivo
                    "\\d{5}\\.\\d{5}\\W\\d{5}\\.\\d{6}\\W\\d{5}\\.\\d{6}\\W\\d{1,}\\W{1,6}\\d{14}", // caracteres
                    "(\\d{5}\\.\\d{5}\\W\\W\\d{5}\\.\\d{6}\\W\\W\\d{5}\\.\\d{6}\\W\\W\\d{1}\\W\\W\\d{14})", // sicoob
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

    public String linhaDigitavel() {
        return accessKey();
    }

}
