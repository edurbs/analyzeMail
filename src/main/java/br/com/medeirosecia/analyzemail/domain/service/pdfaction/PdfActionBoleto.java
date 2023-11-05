package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import java.io.File;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdfInterface;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdfBoleto;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;
import br.com.medeirosecia.analyzemail.infra.filesystem.ConfigFile;
import br.com.medeirosecia.analyzemail.infra.filesystem.CsvFile;

public class PdfActionBoleto extends PdfActionAbstract {


    @Override
    public void save(EmailAttachmentDAO attachment, String pdfText) {

        ReadPdfInterface searchPdf = new ReadPdfBoleto();
        searchPdf.setText(pdfText);

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


        String csvFilePath =  new ConfigFile().getBaseFolder()+File.pathSeparator+"PlanilhaBoleto-AnalyzedMail.csv";

        var csvFile = new CsvFile(csvFilePath, header);

        csvFile.addRow(row);

    }


}
