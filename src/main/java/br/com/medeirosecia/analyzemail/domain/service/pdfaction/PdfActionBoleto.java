package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.csv.CsvFileHandler;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdf;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdfBoleto;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;

public class PdfActionBoleto extends PdfActionAbstract {


    @Override
    public void save(EmailAttachmentDAO attachment, String pdfText) {



        ReadPdf searchPdf = new ReadPdfBoleto(pdfText);
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

        new CsvFileHandler().addBoletoRow(row);



    }


}
