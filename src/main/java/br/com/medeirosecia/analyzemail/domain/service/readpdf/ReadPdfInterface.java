package br.com.medeirosecia.analyzemail.domain.service.readpdf;

public interface ReadPdfInterface {

    public String[] date();
    public String cnpjPayer();
    public String cnpjSupplier();
    public void setText(String textToSearchIn);
    public Double value();
    public String accessKey();
}