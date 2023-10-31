package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.searchpdf.SearchPdf;
import br.com.medeirosecia.analyzemail.domain.service.searchpdf.SearchPdfNfProduto;
import br.com.medeirosecia.analyzemail.infra.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class PdfActionNfProduto extends PdfActionAbstract {



    @Override
    public void save(EmailAttachmentDAO attachment, BaseFolders baseFolders, String pdfText) {

        String fileName = attachment.getFileName();

        SearchPdf nfProdutoSearch = new SearchPdfNfProduto(pdfText);


        String[] date = nfProdutoSearch.date();
        String stringDate = date[0] + "/" + date[1] + "/" + date[2];

        String cnpj = nfProdutoSearch.cnpjPayer();

        String accessKey = nfProdutoSearch.accessKey();

        String[] row = new String[] {
                stringDate,
                cnpj,
                accessKey,
                fileName
        };

        String[] header = new String[] { "Dt.Emissão",
                "CNPJ Emitente",
                "Chave de acesso",
                "Nome do arquivo"
        };

        var myExcel = new MyExcel(baseFolders, "PlanilhaNF-AnalyzedMail.xlsx");
        myExcel.openWorkbook(header);
        myExcel.addRow(row);
        myExcel.saveAndCloseWorkbook();

        baseFolders.savePdfNfProduto(attachment, date);
    }


}
