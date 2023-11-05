package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import java.io.File;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdfInterface;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdfNfProduto;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;
import br.com.medeirosecia.analyzemail.infra.filesystem.ConfigFile;
import br.com.medeirosecia.analyzemail.infra.filesystem.CsvFile;

public class PdfActionNfProduto extends PdfActionAbstract {



    @Override
    public void save(EmailAttachmentDAO attachment, String pdfText) {

        ReadPdfInterface nfProdutoSearch = new ReadPdfNfProduto();
        nfProdutoSearch.setText(pdfText);

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
        String[] header = new String[] {
            "Dt.Emissão",
            "CNPJ Emitente",
            "Chave de acesso",
            "Nome do arquivo"
        };

        String csvFilePath =  new ConfigFile().getBaseFolder()+File.pathSeparator+"PlanilhaNF-AnalyzedMail.csv";

        var csvFile = new CsvFile(csvFilePath, header);

        csvFile.addRow(row);


    }


}
