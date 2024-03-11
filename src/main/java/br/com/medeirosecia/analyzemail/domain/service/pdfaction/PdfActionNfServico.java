package br.com.medeirosecia.analyzemail.domain.service.pdfaction;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdfInterface;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.ReadPdfNfServico;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;
import br.com.medeirosecia.analyzemail.infra.filesystem.ConfigFile;
import br.com.medeirosecia.analyzemail.infra.filesystem.CsvFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfActionNfServico extends PdfActionAbstract {


    @Override
    public void save(EmailAttachmentDAO attachment, String pdfText) {

        ReadPdfInterface readPdfNfServico = new ReadPdfNfServico();
        readPdfNfServico.setText(pdfText);

        String[] date = readPdfNfServico.date();
        String stringDate = date[0] + "/" + date[1] + "/" + date[2];
        String cnpj = readPdfNfServico.cnpjSupplier();
        String accessKey = readPdfNfServico.accessKey();
        String filename = new BaseFolders().savePdfNfServico(attachment, date);

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

        String csvFilePath =  new ConfigFile().getBaseFolder()+File.separator+"PlanilhaNFS-AnalyzedMail.csv";
        var csvFile = new CsvFile(csvFilePath, header);

        csvFile.addRow(row);

    }

}
