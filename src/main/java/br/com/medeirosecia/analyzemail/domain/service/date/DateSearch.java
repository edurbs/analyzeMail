package br.com.medeirosecia.analyzemail.domain.service.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rationalminds.LocalDateModel;
import net.rationalminds.Parser;

public class DateSearch {

    private static final String DATE_SPLITTER = "/";
    private static final String NULL_DATE = "00/00/0000";
    String textToSearchIn;

    public DateSearch(String textToSearchIn){
        this.textToSearchIn = textToSearchIn;
    }

    public List<String[]> allDates(){
        List<String[]> allDates = new ArrayList<>();

        int thisYear = java.time.LocalDate.now().getYear();

        String regex = "((0[1-9]|[12][0-9]|3[01])[-/.\\W](0[1-9]|1[0-2])[-/.\\W]("+(thisYear-1)+"|"+thisYear+"|"+(thisYear+1)+"|"+(thisYear+2)+"))";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(textToSearchIn);

        while(matcher.find()){
            String dateFound = matcher.group();
            dateFound = dateFound.replace("-", "/");
            dateFound = dateFound.replace(".", "/");
            dateFound = dateFound.replace(" ", "/");
            allDates.add(dateFound.split(DATE_SPLITTER));
        }

        return allDates;
    }

    public String[] inBoleto(){

        String date = NULL_DATE;
        Parser parser = new Parser();
        int vencimento = textToSearchIn.indexOf("vencimento");
        String tempText = textToSearchIn;
        if(vencimento>0){
            tempText = textToSearchIn.substring(vencimento, textToSearchIn.length());
        }

        List<LocalDateModel> dates;
        try {
          dates = parser.parse(tempText);
        } catch (Exception e) {
            e.printStackTrace();
            return date.split(DATE_SPLITTER);
        }

        // search for date
        for (LocalDateModel localDateModel : dates) {
            if(isValidDate(localDateModel)){
                date = localDateModel.getOriginalText();
                return date.split(getDateSplitter(date));
            }
        }
        return date.split(DATE_SPLITTER);
    }

    private String getDateSplitter(String date){
        String[] splitters = {DATE_SPLITTER,"-","."};
        // search for splitter
        for (String splitter : splitters) {
            if(date.contains(splitter)){
                if(splitter.equals(splitters[2])){
                    return "\\.";
                }
                return splitter;
            }
        }
        return "";
    }

    private boolean isValidDate(LocalDateModel localDateModel){
        boolean isValid = false;


        String dateOriginal = localDateModel.getOriginalText();
        String dateSplitter = getDateSplitter(dateOriginal);
        String[] dateSplit = dateOriginal.split(dateSplitter);
        String day = dateSplit[0];
        if(day.length() == 1 || day.isBlank()){
            return false;
        }

        String[] date = convertDateTime(localDateModel).split(DATE_SPLITTER);
        int year;
        try {
            year = Integer.parseInt(date[2]);
        } catch (Exception e) {
            return false;
        }
        int thisYear = java.time.LocalDate.now().getYear();
        if (year >= thisYear - 4 && year <= thisYear + 4) {
            return true;
        }

        return isValid;
    }


    private String convertDateTime(LocalDateModel localDateModel){
        String date = localDateModel.getOriginalText();
        String splitter = getDateSplitter(date);
        String[] dateSplit = date.split(splitter);
        try {
            date = String.format("%02d",Integer.parseInt(dateSplit[0]))
                    +DATE_SPLITTER
                    +String.format("%02d",Integer.parseInt(dateSplit[1]))
                    +DATE_SPLITTER
                    +String.format("%04d",Integer.parseInt(dateSplit[2]));
        } catch (Exception e) {
            return NULL_DATE;
        }

        return date;
    }


    public String[] near(String targetWord){

        targetWord = targetWord.toLowerCase();

        // date with - / or . as separator
        Pattern pattern = Pattern.compile("\\d{2}[-/\\.]\\d{2}[-/\\.]\\d{4}");
        Matcher matcher = pattern.matcher(textToSearchIn);

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd.MM.yyyy");

        Date closestDate = null;
        int lastHowFar = Integer.MAX_VALUE;
        try {

            Date date = dateFormat1.parse("01-01-0001");

            while (matcher.find()) {
                String dataStr = matcher.group();


                if (dataStr.contains("-")) {
                    date = dateFormat1.parse(dataStr);
                } else if (dataStr.contains(DATE_SPLITTER)) {
                    date = dateFormat2.parse(dataStr);
                } else if (dataStr.contains(".")) {
                    date = dateFormat3.parse(dataStr);
                }

                // Encontre a posição da palavra "emissão" no texto
                int indexOfEmissao = textToSearchIn.indexOf(targetWord);

                // Encontre a posição da data atual no texto
                int indexOfDate = textToSearchIn.indexOf(dataStr);

                // Calcule a distância entre a data atual e a palavra "emissão"
                int howFar = Math.abs(indexOfDate - indexOfEmissao);

                // Verifique se esta data está mais próxima da palavra "emissão" do que a anterior
                if (howFar < lastHowFar) {
                    lastHowFar = howFar;
                    closestDate = date;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (closestDate == null) {
            return new String[]{"","",""};
        }

        LocalDate closestLocalDate = closestDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int year = closestLocalDate.getYear();

        int thisYear = java.time.LocalDate.now().getYear();
        if (year < thisYear - 5 || year > thisYear + 5){
            return new String[]{"","",""};
        }

        return new String[]{(String.format("%02d",closestLocalDate.getDayOfMonth())),
                String.format("%02d",closestLocalDate.getMonthValue()),
                String.format("%04d",year)};

    }

}
