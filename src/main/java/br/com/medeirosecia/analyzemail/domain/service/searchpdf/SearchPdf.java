package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

public interface SearchPdf {

    public String[] date();
    public String cnpjPayer();
    public String cnpjSupplier();
    public void setText(String textToSearchIn);
    public Double value();
    public String accessKey();
}