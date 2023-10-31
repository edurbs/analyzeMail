package br.com.medeirosecia.analyzemail.domain.service.searchpdf;

import java.util.List;

public interface SearchPdf {

    public int keywords();
    public String[] date();
    public String cnpjPayer();
    public String cnpjSupplier();
    public void setText(String textToSearchIn);
    public Double value();
    public String accessKey();
}