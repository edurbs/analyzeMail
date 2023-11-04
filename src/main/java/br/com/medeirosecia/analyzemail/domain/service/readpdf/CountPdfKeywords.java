package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CountPdfKeywords {

    private Integer keywordsNfProduto = 0;
    private Integer keywordsNfServico = 0;
    private Integer keywordsEnergisa = 0;
    private Integer keywordsBoleto = 0;
    private Integer keywordsOther = 0;

    private String pdfText;

    public CountPdfKeywords(String pdfText) {
        this.pdfText = pdfText;

        Map<PdfType, Integer> keywordCountMap = new EnumMap<>(PdfType.class);

        for (PdfType pdfType : PdfType.values()) {

            String[] keywords = pdfType.getKeyWords();
            Set<String> keywordsSet = new HashSet<>(Arrays.asList(keywords));

            int keywordsFound = 0;
            for (String keyword : keywordsSet) {
                if (pdfText.contains(keyword.toLowerCase())) {
                    keywordsFound++;
                }
            }
            keywordCountMap.put(pdfType, keywordsFound);
        }

        keywordsNfProduto = keywordCountMap.get(PdfType.NF_PRODUTO);
        keywordsNfServico = keywordCountMap.get(PdfType.NF_SERVICO);
        keywordsEnergisa = keywordCountMap.get(PdfType.ENERGISA);
        keywordsBoleto = keywordCountMap.get(PdfType.BOLETO);
        keywordsOther = keywordCountMap.get(PdfType.OUTRO);
    }



    public boolean isPdfNfProduto() {
        return keywordsNfProduto > 6
                && keywordsNfProduto > keywordsBoleto;
    }

    public boolean isPdfNfServico() {
        return keywordsNfServico > 6
                && keywordsNfServico > keywordsBoleto;
    }

    public boolean isPdfBoleto() {
        ReadPdfBoleto readPdfBoleto = new ReadPdfBoleto(pdfText);
        if(!readPdfBoleto.linhaDigitavel().isBlank()){
            keywordsBoleto += 6;
        }

        return keywordsBoleto > 5
                && keywordsBoleto > keywordsNfProduto
                && keywordsBoleto > keywordsNfServico;
    }

    public boolean isPdfEnergisa(){
        return isPdfBoleto() && keywordsEnergisa > 4;
    }

    public boolean isPdfOther(){
        return (!isPdfNfProduto() && !isPdfNfServico() && !isPdfBoleto()) || keywordsOther>5;
    }
}
