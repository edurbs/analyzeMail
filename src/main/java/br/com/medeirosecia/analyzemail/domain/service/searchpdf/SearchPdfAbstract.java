package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

public abstract class SearchPdfAbstract implements SearchPdf {
    protected String textToSearchIn;
    protected SearchPdfAbstract(String textToSearchIn) {
        this.textToSearchIn = textToSearchIn;
    }


}
