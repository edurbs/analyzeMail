package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.csv.CsvFileHandler;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdf;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdfNfProduto;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class PdfActionNfProduto extends PdfActionAbstract {



    @Override
    public void save(EmailAttachmentDAO attachment, String pdfText) {

        ReadPdf nfProdutoSearch = new ReadPdfNfProduto(pdfText);

        String[] date = nfProdutoSearch.date();
        String stringDate = date[0] + "/" + date[1] + "/" + date[2];

        String filename = new BaseFolders().savePdfNfProduto(attachment, date);

        String cnpj = nfProdutoSearch.cnpjSupplier();

        String accessKey = nfProdutoSearch.accessKey();

        String[] row = new String[] {
                stringDate,
                cnpj,
                accessKey,
                filename
        };

        new CsvFileHandler().addNfRow(row);


    }


}
