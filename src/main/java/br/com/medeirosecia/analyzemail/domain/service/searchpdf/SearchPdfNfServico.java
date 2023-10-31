package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.medeirosecia.analyzemail.domain.service.date.DateSearch;

public class SearchPdfNfServico extends SearchPdfAbstract {


    public SearchPdfNfServico(String textToSearchIn) {
        super(textToSearchIn);

    }

    @Override
    public int keywords(){
        String[] nfServicoKeywords = { "tomador", "serviço", "prestador", "nfs-e", "rps", "iss", "prefeitura",
            "municipal", "issqn", "serviços eletrônica", "nota fiscal eletrônica de serviços", "Nota fiscal de serviço",
            "Nota fiscal avulsa" };
        Set<String> nfServicoKeywordsSet = new HashSet<>(Arrays.asList(nfServicoKeywords));
        int keywordsFound=0;
        for (String keyword : nfServicoKeywordsSet) {
            if (textToSearchIn.contains(keyword.toLowerCase())) {
                keywordsFound++;
            }
        }
        return keywordsFound;
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
        // TODO
        return "";
    }

    @Override
    public String cnpjSupplier() {
        // TODO
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
