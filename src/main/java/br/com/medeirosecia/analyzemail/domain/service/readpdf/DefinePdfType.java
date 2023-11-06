package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.medeirosecia.analyzemail.domain.service.readpdf.boleto.BoletoType;

public class DefinePdfType {
    private String pdfText;
    private BoletoType boletoType;

    private Map<PdfType, Integer> keywordPdfCountMap = new EnumMap<>(PdfType.class);

    public DefinePdfType(String pdfText){
        this.pdfText = pdfText;
    }


    public PdfType getPdfType(){
        countPdfKeywords();

        // first check NFS-e
        if(isPdfNfServico()){
            return PdfType.NF_SERVICO;
        }

        // second check NF-e
        if(isPdfNfProduto()){
            return PdfType.NF_PRODUTO;
        }

        if(isPdfBoleto()){
            setBoletoType();
            return PdfType.BOLETO;
        }

        return PdfType.OUTRO;
    }

    public BoletoType getBoletoType(){
        if(boletoType == null){
            setBoletoType();
        }
        return this.boletoType;
    }

    private void setBoletoType(){
        boletoType = BoletoType.COMUM;

        Map<BoletoType, Integer> keywordBoletoCountMap = new EnumMap<>(BoletoType.class);

        for (BoletoType type : BoletoType.values()) {
            String[] keywords = type.getBoletoKeyWords();
            Set<String> keywordsSet = new HashSet<>(Arrays.asList(keywords));

            int keywordsFound = 0;
            for (String keyword : keywordsSet) {
                if(pdfText.contains(keyword.toLowerCase())){
                    keywordsFound++;
                }
            }
            keywordBoletoCountMap.put(type, keywordsFound);
        }

        int maxKeywords = 0;
        for (Map.Entry<BoletoType, Integer> entry : keywordBoletoCountMap.entrySet()) {
            if (entry.getValue() > maxKeywords) {
                maxKeywords = entry.getValue();
                boletoType = entry.getKey();
            }
        }

    }

    private void countPdfKeywords() {

        // read the enum BoletoType, and add all the values to one string[]
        List<String> boletoTypeKeywords = new ArrayList<>();
        for(BoletoType boletoTypeValue : BoletoType.values()) {
            boletoTypeKeywords.addAll(Arrays.asList(boletoTypeValue.getBoletoKeyWords()));
        }

        for (PdfType pdfType : PdfType.values()) {

            String[] keywords = pdfType.getKeyWords();
            Set<String> keywordsSet = new HashSet<>(Arrays.asList(keywords));

            if(pdfType.equals(PdfType.BOLETO)) {
                keywordsSet.addAll(boletoTypeKeywords);
            }

            int keywordsFound = 0;

            // get the total of times this same keyword is found, not just one time
            keywordsFound = keywordsSet.stream()
                .map(key -> pdfText.toLowerCase().split(key.toLowerCase()).length - 1)
                .reduce(0, Integer::sum);

            ReadPdfInterface readPdf = pdfType.getReadPdf();
            readPdf.setText(pdfText);
            String accessKey = readPdf.accessKey();
            if(accessKey!=null && !accessKey.isBlank()){
                keywordsFound += 100;
            }

            keywordPdfCountMap.put(pdfType, keywordsFound);
        }

    }

    private boolean isPdfNfProduto() {
        int keywordsNfProduto = keywordPdfCountMap.get(PdfType.NF_PRODUTO);
        int keywordsBoleto = keywordPdfCountMap.get(PdfType.BOLETO);

        return keywordsNfProduto > 6
                && keywordsNfProduto > keywordsBoleto;
    }

    private boolean isPdfNfServico() {
        int keywordsBoleto = keywordPdfCountMap.get(PdfType.BOLETO);
        int keywordsNfServico = keywordPdfCountMap.get(PdfType.NF_SERVICO);
        int keywordsNfProduto = keywordPdfCountMap.get(PdfType.NF_PRODUTO);

        return keywordsNfServico > 5
                && keywordsNfServico > keywordsBoleto
                && keywordsNfServico > keywordsNfProduto;
    }

    private boolean isPdfBoleto() {
        int keywordsBoleto = keywordPdfCountMap.get(PdfType.BOLETO);
        int keywordsNfServico = keywordPdfCountMap.get(PdfType.NF_SERVICO);
        int keywordsNfProduto = keywordPdfCountMap.get(PdfType.NF_PRODUTO);



        return keywordsBoleto > 5
                && keywordsBoleto > keywordsNfProduto
                && keywordsBoleto > keywordsNfServico;
    }

}
