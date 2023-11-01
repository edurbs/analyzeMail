package br.com.medeirosecia.analyzemail.domain.service.excel;

import java.io.IOException;

import br.com.medeirosecia.analyzemail.infra.excel.MyExcel;

public class ExcelFile {
    private MyExcel myExcelBoleto;
    private MyExcel myExcelNf;

    public void openFiles(){
        newExcelBoleto();
        newExcelNf();
    }

    public void checkExcelFiles() throws IOException {
        var tempBoleto = new MyExcel("PlanilhaBoleto-AnalyzedMail.xlsx");
        tempBoleto.justOpen();
        tempBoleto.saveAndCloseWorkbook();

        var tempNf = new MyExcel("PlanilhaNF-AnalyzedMail.xlsx");
        tempNf.justOpen();
        tempNf.saveAndCloseWorkbook();


    }

    private void newExcelBoleto(){
        this.myExcelBoleto = new MyExcel("PlanilhaBoleto-AnalyzedMail.xlsx");

        String[] header = new String[] {
            "CNPJ Pagador",
            "CNPJ Fornecedor",
            "Data Vencimento",
            "Valor",
            "Linha digitável",
            "Nome do arquivo"
        };
        myExcelBoleto.openWorkbook(header);
    }

    private void newExcelNf(){
        this.myExcelNf = new MyExcel("PlanilhaNF-AnalyzedMail.xlsx");
        String[] header = new String[] {
            "Dt.Emissão",
            "CNPJ Emitente",
            "Chave de acesso",
            "Nome do arquivo"
        };

        myExcelNf.openWorkbook(header);

    }

    public void addBoletoRow(String [] row) {
        myExcelBoleto.addRow(row);
    }

    public void addNfRow(String [] row) {
        myExcelNf.addRow(row);
    }

    public void saveAllAndClose(){
        try {
            myExcelBoleto.saveAndCloseWorkbook();
            myExcelNf.saveAndCloseWorkbook();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void save(){
        try {
            myExcelBoleto.saveWorkbook();
            myExcelNf.saveWorkbook();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
