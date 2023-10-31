package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.medeirosecia.analyzemail.domain.service.cnpj.CnpjSearch;
import br.com.medeirosecia.analyzemail.domain.service.date.DateSearch;

public class SearchPdfNfProduto extends SearchPdfAbstract {

    public SearchPdfNfProduto(String textToSearchIn) {
        super(textToSearchIn);
    }

    @Override
    public String accessKey(){

        String[] regexPatterns = {
            "\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}", // 11 blocks of 4 digits with two or mores spaces as separators
            "\\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4}", // 11 blocks of 4 digits with one space as the separator
            "\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}", // 11 blocks with dot as separator
            "\\d{2}\\.\\d{4}\\.\\d{12}\\.\\d{2}\\.\\d{3}\\.\\d{9}\\.\\d{1}\\.\\d{8}\\.\\d{1}\\.\\d{8}" // números com outro padrão de blocos com ponto como separador
        };

        for (String regex : regexPatterns) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(textToSearchIn);
            String found;
            if(matcher.find()){
                found = matcher.group();
                found = found.replaceAll("\\.", " ");
                found = found.replaceAll("\\\s+", " ");
                return found;
            }
        }
        return "";
    }

    public int keywords(){
        String[] keywords = { "nota fiscal", "emissão",
            "autenticidade", "danfe", "documento auxiliar", "controle do fisco",
            "chave de acesso", "natureza da operação", "protocolo de autorização", "destinatário", "emitente",
            "dados dos produtos" };
        Set<String> keywordsSet = new HashSet<>(Arrays.asList(keywords));
        int keywordsFound = 0;
        for (String keyword : keywordsSet) {
            if (textToSearchIn.contains(keyword.toLowerCase())) {
                keywordsFound++;
            }
        }
        return keywordsFound;
    }

    public String[] date(){
        return new DateSearch(textToSearchIn).near("autorização");
    }

    public String cnpjPayer(){
        // TODO
        return "";
    }

    public String cnpjSupplier(){
        // TODO
        return new CnpjSearch(textToSearchIn).nextTo("chave de acesso");
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
