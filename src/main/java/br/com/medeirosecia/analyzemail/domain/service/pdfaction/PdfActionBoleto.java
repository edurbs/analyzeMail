package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.searchpdf.SearchPdf;
import br.com.medeirosecia.analyzemail.domain.service.searchpdf.SearchPdfBoleto;
import br.com.medeirosecia.analyzemail.infra.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class PdfActionBoleto extends PdfActionAbstract {


    @Override
    public void save(EmailAttachmentDAO attachment, String pdfText) {



        SearchPdf searchPdf = new SearchPdfBoleto(pdfText);
        String[] date = searchPdf.date();

        String filename = new BaseFolders().savePdfBoleto(attachment, date);

        String stringDate = date[0] + "/" + date[1] + "/" + date[2];

        String value = searchPdf.value().toString();

        String cnpjPayer = searchPdf.cnpjPayer();
        String cnpjSupplier = searchPdf.cnpjSupplier();
        String barCode = searchPdf.accessKey();

        String[] row = new String[] {
                cnpjPayer,
                cnpjSupplier,
                stringDate,
                value,
                barCode,
                filename
        };

        String[] header = new String[] {
                "CNPJ Pagador",
                "CNPJ Fornecedor",
                "Data Vencimento",
                "Valor",
                "Linha digitável",
                "Nome do arquivo"
            };
        var myExcel = new MyExcel("PlanilhaBoleto-AnalyzedMail.xlsx");
        myExcel.openWorkbook(header);
        myExcel.addRow(row);
        myExcel.saveAndCloseWorkbook();


    }


}
