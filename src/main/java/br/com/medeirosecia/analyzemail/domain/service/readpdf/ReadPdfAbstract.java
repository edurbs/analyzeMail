package br.com.medeirosecia.analyzemail.domain.service.readpdf;

public abstract class ReadPdfAbstract implements ReadPdf {
    protected String textToSearchIn;
    protected ReadPdfAbstract(String textToSearchIn) {
        this.textToSearchIn = textToSearchIn;
    }


}
