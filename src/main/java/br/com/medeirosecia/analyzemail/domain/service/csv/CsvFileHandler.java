package br.com.medeirosecia.analyzemail.domain.service.csv;

import br.com.medeirosecia.analyzemail.infra.filesystem.CsvFile;

public class CsvFileHandler {

    private CsvFile csvBoleto = new CsvFile("PlanilhaBoleto-AnalyzedMail.csv");
    private CsvFile csvNf = new CsvFile("PlanilhaNF-AnalyzedMail.csv");


    private String[] nfHeader = new String[] {
            "Dt.Emissão",
            "CNPJ Emitente",
            "Chave de acesso",
            "Nome do arquivo"
        };
    private String[] boletoHeader = new String[] {
            "CNPJ Pagador",
            "CNPJ Fornecedor",
            "Data Vencimento",
            "Valor",
            "Linha digitável",
            "Nome do arquivo"
        };

    public CsvFileHandler() {
        checkHeaderBoleto();
        checkHeaderNf();
    }

    public void checkHeaderBoleto(){
        csvBoleto.addHeader(boletoHeader);
    }

    public void checkHeaderNf(){
        csvNf.addHeader(nfHeader);
    }

    public String[] getNfHeader() {
        return nfHeader;
    }

    public String[] getBoletoHeader() {
        return boletoHeader;
    }

    public void addBoletoRow(String[] row){
        csvBoleto.addRow(row);
    }

    public void addNfRow(String[] row){
        csvNf.addRow(row);
    }

    public String[] readBoletoFirstLine(){
        String firstLine = csvBoleto.readFirstLine();
        return firstLine.split(";");
    }

    public String[] readBoletoLastLine(){
        String lastLine = csvBoleto.readLastLine();
        return lastLine.split(";");
    }

    public String[] readNfFirstLine(){
        String firstLine = csvNf.readFirstLine();
        return firstLine.split(";");
    }

    public String[] readNfLastLine(){
        String lastLine = csvNf.readLastLine();
        return lastLine.split(";");
    }


}
