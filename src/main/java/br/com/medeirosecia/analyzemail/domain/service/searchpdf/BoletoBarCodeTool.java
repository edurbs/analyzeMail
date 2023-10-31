package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

import java.text.SimpleDateFormat;
import java.util.Calendar;



public class BoletoBarCodeTool {
    private String accessKey;
    private BoletoType boletoType;

    private static final String[] ZERO_DATE = {"00", "00", "0000"};
    private static final String LOCAL_DATE_FORMAT = "dd/MM/yyyy";


    public BoletoBarCodeTool(String accessKey, BoletoType boletoType){
        this.accessKey = accessKey;
        this.boletoType = boletoType;
    }

    public String[] getDueDate(){
        String[] dueDate;

        switch (boletoType) {
            case COMUM -> dueDate = getDateFromComum();
            case CONSUMO_VIVO -> dueDate = getDateFromConsumoVivo();
            case DARF -> dueDate = getDateFromDarf();
            case GRRF -> dueDate = getDateFromGrrf();
            case GRF -> dueDate = getDateFromGrf();
            case GRU_SIMPLES -> dueDate = getDateFromGruSimples();
            case SEFAZ_MT -> dueDate = getDateFromSefazMt();
            default -> dueDate = ZERO_DATE;
        }


        if (accessKey.length() == 47) {
            dueDate = getDateFromComum();

        } else if (accessKey.length() == 48) {
            getDateFromConsumo();

        }
        return dueDate;
    }

    private String[] getDateFromGruSimples(){
        // GRU não tem a data de vencimento no código de barras
        return ZERO_DATE;
    }

    private String[] getDateFromSefazMt(){
        String[] date = new String[3];
        String sub1 = accessKey.substring(20, 23);
        String sub2 = accessKey.substring(24, 30);
        String sub = sub1+sub2;

        date[0] = sub.substring(6, 8); // day
        date[1] = sub.substring(4, 6); // month
        date[2] = sub.substring(0, 4); // year with 4 digits

        return date;
    }

    private String[] getDateFromGrrf(){
        String[] date = new String[3];
        String sub1 = accessKey.substring(22, 23);
        String sub2 = accessKey.substring(24, 29);
        String sub = sub1+sub2;

        date[0] = sub.substring(4, 6); // day
        date[1] = sub.substring(2, 4); // month
        date[2] = "20" + sub.substring(0, 2); // year with 2 digits

        return date;
    }

    private String[] getDateFromGrf(){
        String[] date = new String[3];
        String sub1 = accessKey.substring(20, 23);
        String sub2 = accessKey.substring(24, 27);
        String sub = sub1+sub2;

        date[0] = sub.substring(4, 6); // day
        date[1] = sub.substring(2, 4); // month
        date[2] = "20" + sub.substring(0, 2); // year with 2 digits

        return date;
    }

    private String[] getDateFromDarf() {

        String sub1 = accessKey.substring(21, 23);
        String sub2 = accessKey.substring(24, 26);
        int days = Integer.parseInt(sub1+sub2);

        return sumDateDarf(days);

    }

    private String[] getDateFromConsumoVivo() {
        String[] date = new String[3];
        String sub = accessKey.substring(41, 47);

        date[0] = sub.substring(4, 6); // day
        date[1] = sub.substring(2, 4); // month
        date[2] = "20" + sub.substring(0, 2); // year with 2 digits

        return date;
    }

    private String[] getDateFromConsumo() {
        String[] date = new String[3];
        String sub = accessKey.substring(23, 29);

        date[0] = sub.substring(0, 2); // day
        date[1] = sub.substring(2, 4); // month
        date[2] = sub.substring(4, 6); // year with 4 digits
        return date;
    }

    private String[] getDateFromComum() {

        String sub = accessKey.substring(33, 37);
        int days = Integer.parseInt(sub);

        return sumDateFebraban(days);
    }

    private String[] sumDateFebraban(int days) {
        Calendar startDateFebraban = Calendar.getInstance();
        startDateFebraban.set(1997, 9, 7); // 7/10/1997

        return sumDate(startDateFebraban, days);

    }

    private String[] sumDateDarf(int days) {
        Calendar startDateFebraban = Calendar.getInstance();
        startDateFebraban.set(2014, 9, 14); // 14/10/2014

        return sumDate(startDateFebraban, days);
    }

    private String[] sumDate(Calendar startDate, int days){

        Calendar finalDate = (Calendar) startDate.clone();
        finalDate.add(Calendar.DAY_OF_MONTH, days);

        SimpleDateFormat dateFormat = new SimpleDateFormat(LOCAL_DATE_FORMAT);
        String formattedDate = dateFormat.format(finalDate.getTime());

        return formattedDate.split("/");

    }

    public Double getValue(){
        Double value;
        switch (boletoType) {
            case COMUM -> value = valueFromComum();
            case CONSUMO_VIVO, DARF, GRRF, GRF, GRU_SIMPLES, SEFAZ_MT -> value = valueFromConsumo();
            case OTHER -> value = 0d;
            default -> value = 0d;
        }
        return value;

    }
    private Double valueFromConsumo() {
        // iiiiNNNNNNN NNCC                         AAMMDD
        // 846900000015099800550016104349435198092332309258

        String substring1 = accessKey.substring(5, 11);
        String substring2 = accessKey.substring(12, 16);
        String concatenatedSubstrings = substring1 + substring2;
        return Double.parseDouble(concatenatedSubstrings) / 100.0;

    }

    private Double valueFromComum() {
        //                                     VVVVVVVVVVV
        // 74891123130004133950810193541041594700002339675
        //
        String last11Digits = accessKey.substring(accessKey.length() - 10);
        return Double.parseDouble(last11Digits) / 100.0;
    }
}
