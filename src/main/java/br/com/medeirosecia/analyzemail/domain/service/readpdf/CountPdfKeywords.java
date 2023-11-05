package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import br.com.medeirosecia.analyzemail.domain.service.readpdf.boleto.BoletoType;

public class CountPdfKeywords {

    private Integer keywordsNfProduto = 0;
    private Integer keywordsNfServico = 0;
    // private Integer keywordsEnergisa = 0;
    private Integer keywordsBoleto = 0;
    private Integer keywordsOther = 0;

    private String pdfText;
    private Map<PdfType, Integer> keywordCountMap = new EnumMap<>(PdfType.class);

    public CountPdfKeywords(String pdfText) {
        this.pdfText = pdfText;

        // read the enum BoletoType, and add all the values to one string[]
        List<String> boletoTypeKeywords = new ArrayList<>();
        for(BoletoType boletoType : BoletoType.values()) {
            boletoTypeKeywords.addAll(Arrays.asList(boletoType.getBoletoKeyWords()));
        }

        for (PdfType pdfType : PdfType.values()) {

            String[] keywords = pdfType.getKeyWords();
            Set<String> keywordsSet = new HashSet<>(Arrays.asList(keywords));

            if(pdfType.equals(PdfType.BOLETO)) {
                keywordsSet.addAll(boletoTypeKeywords);
            }

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
        // keywordsEnergisa = keywordCountMap.get(PdfType.ENERGISA);
        keywordsBoleto = keywordCountMap.get(PdfType.BOLETO);
        keywordsOther = keywordCountMap.get(PdfType.OUTRO);



    }

    public int getSumAllKeywords() {
        // if all keywords is zero, the PDF font is criptographic
        int sumKeywords = 0;
        for (Entry<PdfType, Integer> entry : keywordCountMap.entrySet()) {
            sumKeywords += entry.getValue();
        }
        return sumKeywords;
    }



    public boolean isPdfNfProduto() {
        ReadPdfNfProduto readPdfNfProduto = new ReadPdfNfProduto(pdfText);
        String accessKey = readPdfNfProduto.accessKey();
        if(accessKey!=null){
            keywordsNfProduto += 7;
        }
        return keywordsNfProduto > 6
                && keywordsNfProduto > keywordsBoleto;
    }

    public boolean isPdfNfServico() {
        return keywordsNfServico > 6
                && keywordsNfServico > keywordsBoleto;
    }

    public boolean isPdfBoleto() {
        ReadPdfBoleto readPdfBoleto = new ReadPdfBoleto(pdfText);
        String linhaDigitavel = readPdfBoleto.linhaDigitavel();
        if(linhaDigitavel!=null){
            keywordsBoleto += 6;
        }

        return keywordsBoleto > 5
                && keywordsBoleto > keywordsNfProduto
                && keywordsBoleto > keywordsNfServico;
    }

    /* public boolean isPdfEnergisa(){
        return isPdfBoleto() && keywordsEnergisa > 4;
    } */

    public boolean isPdfOther(){
        return (!isPdfNfProduto() && !isPdfNfServico() && !isPdfBoleto()) || keywordsOther>5;
    }
}
