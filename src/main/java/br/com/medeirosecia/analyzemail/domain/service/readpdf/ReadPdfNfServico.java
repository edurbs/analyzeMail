package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.medeirosecia.analyzemail.domain.service.cnpj.CnpjSearch;
import br.com.medeirosecia.analyzemail.domain.service.cnpj.CnpjValidator;
import br.com.medeirosecia.analyzemail.domain.service.date.DateSearch;
import br.com.medeirosecia.analyzemail.infra.filesystem.ReadCnpjFile;

public class ReadPdfNfServico implements ReadPdfInterface {

    private String accesskey = "";
    private String textToSearchIn;
    private List<String> allCnpjInPdf = new ArrayList<>();


    @Override
    public String[] date() {

        if(textToSearchIn ==null || textToSearchIn.isBlank()){
            return new String[3];
        }
        String[] date = { "01", "01", "0001" };
        String[] findStrings = { "Data e Hora da emissão da NFS-e",
                "Data da Emissão da Nota",
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
        // TODO pagador de CNPJ para NFS-e
        return "";
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
    public String cnpjSupplier() {
        String cnpj = "";
        if(!accesskey.isBlank() && accesskey.length() > 33){
            cnpj = accesskey.substring(10,23);
            var cnpjValidator = new CnpjValidator(cnpj);
            if (cnpjValidator.isValid()) {
                return cnpj;
            }
        }
        if (textToSearchIn == null || textToSearchIn.isBlank()) {
            return "";
        }
        List<String> cnpjListPayers = new ReadCnpjFile().getCnpjListPayers();
        allCnpjInPdf = new CnpjSearch(textToSearchIn).all();
        cnpj = listNotContains(cnpjListPayers);
        if (cnpj.isBlank()) {
            cnpj = listNotContains(new CnpjSearch(textToSearchIn).withoutSeparator());
        }
        var cnpjValidator = new CnpjValidator(cnpj);
        if (!cnpjValidator.isValid()) {
            return "";
        }
        return cnpj;
    }

    @Override
    public void setText(String textToSearchIn) {
        this.textToSearchIn = textToSearchIn;
        accessKey();
    }

    @Override
    public Double value() {
        String[] lines = this.textToSearchIn.split("\\r?\\n");
        boolean foundValorLiquido = false;
        for (int i = 0; i < lines.length; i++) {
            if (foundValorLiquido) {
                String valorLiquido = lines[i];
                valorLiquido = valorLiquido.replaceAll("[^0-9]", "");
                valorLiquido = valorLiquido.replace(".", "");
                valorLiquido = valorLiquido.replace(",", ".");
                return Double.valueOf(valorLiquido);
            }
            if (lines[i].toLowerCase().contains("valor líquido")) {
                foundValorLiquido = true;
            }
        }
        return 0d;
    }

    @Override
    public String accessKey() {
        if(accesskey.isBlank() && textToSearchIn!=null && !textToSearchIn.isBlank()){
            // DANFSe v1.0
            String nfseRegex = "\\d{50}";
            Pattern pattern = Pattern.compile(nfseRegex);
            Matcher matcher = pattern.matcher(textToSearchIn);
            if (matcher.find()) {
                this.accesskey = matcher.group();
                return this.accesskey;
            }
        }
        return "";
    }

}
