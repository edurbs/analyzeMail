package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

public class CountPdfKeywords {

    private Integer keywordsNfProduto = 0;
    private Integer keywordsNfServico = 0;
    private Integer keywordsBoleto = 0;

    public CountPdfKeywords(String pdfText) {

        // TODO remove Keyword method from SearchPdf and move to here
        // move keywords from BoletoType to a new Enum
        // create a enum with PDFType with the keyword, and do foreach in it

        var searchers = new SearchPdf[] {
            new SearchPdfNfProduto(pdfText),
            new SearchPdfNfServico(pdfText),
            new SearchPdfBoleto(pdfText)
        };

        for (SearchPdf searcher : searchers) {
            switch (searcher.getClass().getSimpleName()) {
                case "SearchPdfNfProduto" -> keywordsNfProduto = searcher.keywords();
                case "SearchPdfNfServico" -> keywordsNfServico = searcher.keywords();
                case "SearchPdfBoleto" -> keywordsBoleto = searcher.keywords();
            }
        }
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
        return keywordsBoleto > 5
                && keywordsBoleto > keywordsNfProduto
                && keywordsBoleto > keywordsNfServico;
    }

    public boolean isPdfOther(){
        return !isPdfNfProduto() && !isPdfNfServico() && !isPdfBoleto();
    }
}
