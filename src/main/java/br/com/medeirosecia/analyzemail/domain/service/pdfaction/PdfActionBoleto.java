package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.searchpdf.SearchPdf;
import br.com.medeirosecia.analyzemail.domain.service.searchpdf.SearchPdfBoleto;
import br.com.medeirosecia.analyzemail.infra.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class PdfActionBoleto extends PdfActionAbstract {


    @Override
    public void save(EmailAttachmentDAO attachment, BaseFolders baseFolders, String pdfText) {

        String filename = attachment.getFileName();

        SearchPdf pdfSearch = new SearchPdfBoleto(pdfText);


        String[] date = pdfSearch.date();
        String stringDate = date[0] + "/" + date[1] + "/" + date[2];

        String value = pdfSearch.value().toString();

        String cnpjPayer = pdfSearch.cnpjPayer();
        String cnpjSupplier = pdfSearch.cnpjSupplier();

        String[] row = new String[] {
                cnpjPayer,
                cnpjSupplier,
                stringDate,
                value,
                filename
        };

        String[] header = new String[] { "CNPJ Pagador", "CNPJ Fornecedor",
            "Data Vencimento",
            "Valores encontrados",
            "Nome do arquivo"
        };
        var myExcel = new MyExcel(baseFolders, "PlanilhaBoleto-AnalyzedMail.xlsx");
        myExcel.openWorkbook(header);
        myExcel.addRow(row);
        myExcel.saveAndCloseWorkbook();

        baseFolders.savePdfBoleto(attachment, date);
    }


}
