package br.com.medeirosecia.analyzemail.domain.service.readpdf;

public class ReadPdfOutro implements ReadPdfInterface {

    @Override
    public String[] date() {
        return new String[3];
    }

    @Override
    public String cnpjPayer() {
        return "";
    }

    @Override
    public String cnpjSupplier() {
        return "";
    }

    @Override
    public void setText(String textToSearchIn) {
        // never used
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
