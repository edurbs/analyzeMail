package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.medeirosecia.analyzemail.domain.service.date.DateSearch;

public class ReadPdfNfServico implements ReadPdfInterface {

    private String accesskey = "";
    private String textToSearchIn;


    @Override
    public String[] date() {

        if(textToSearchIn ==null || textToSearchIn.isBlank()){
            return new String[3];
        }

        String[] date = { "01", "01", "0001" };
        String[] findStrings = { "Data e Hora da emissão da NFS-e",
                "Data e Hora de emiss",
                "Emissão da nota",
                "Data emissão",
                "Data"
        };

        var dateSearch = new DateSearch(textToSearchIn);

        for (String string : findStrings) {
            var dateNear = dateSearch.near(string);
            if (!dateNear[2].equals("0001")) {
                date = dateNear;
                break;
            }
        }

        return date;
    }

    @Override
    public String cnpjPayer() {
        // FEAT pagador de CNPJ para NFS-e
        return "";
    }

    @Override
    public String cnpjSupplier() {
        // TODO test fornecedora de CNPJ para NFS-e
        return accesskey.substring(10,23);
    }

    @Override
    public void setText(String textToSearchIn) {
        this.textToSearchIn = textToSearchIn;
        accessKey();
    }

    @Override
    public Double value() {
        // não tem na chave de acesso
        return 0d;
    }

    @Override
    public String accessKey() {
        if(accesskey.isBlank() && textToSearchIn!=null && !textToSearchIn.isBlank()){
            String nfseRegex = "^\\d{50}$";

            Pattern pattern = Pattern.compile(nfseRegex);
            Matcher matcher = pattern.matcher(textToSearchIn);

            if (matcher.find()) {
                this.accesskey = matcher.group();
            }
        }

        return accesskey;
    }

}
