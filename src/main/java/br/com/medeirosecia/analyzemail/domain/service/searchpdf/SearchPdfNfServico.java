package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

import br.com.medeirosecia.analyzemail.domain.service.date.DateSearch;

public class SearchPdfNfServico extends SearchPdfAbstract {


    public SearchPdfNfServico(String textToSearchIn) {
        super(textToSearchIn);

    }


    @Override
    public String[] date(){

        String[] date = {"01", "01", "0001"};
        String[] findStrings = {"Data e Hora da emissão da NFS-e",
                "Data e Hora de emiss",
                "Emissão da nota",
                "Data emissão",
                "Data"
        };

        var dateSearch = new DateSearch(textToSearchIn);

        for (String string : findStrings) {
            var dateNear = dateSearch.near(string);
            if(!dateNear[2].equals("0001")){
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
        // FEAT fornecedora de CNPJ para NFS-e
        return "";
    }

    @Override
    public void setText(String textToSearchIn) {
        this.textToSearchIn = textToSearchIn;
    }

    @Override
    public Double value() {
        return 0d;
    }

    @Override
    public String accessKey() {
        return "";
    }



}
