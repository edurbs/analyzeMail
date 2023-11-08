package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import java.util.List;

public abstract class PdfActionAbstract implements PdfActionInterface {

    protected List<String> cnpjListPayers;

    public void setCnpjPayers(List<String> cnpjListPayers) {
        this.cnpjListPayers = cnpjListPayers;
    }
}
