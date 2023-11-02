package br.com.medeirosecia.analyzemail.domain.service.csv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.medeirosecia.analyzemail.infra.filesystem.CsvFile;

class CsvFileHandlerTest {

    CsvFileHandler csvFileHandler;

    @BeforeEach
    void setup(){
        this.csvFileHandler = new CsvFileHandler();
    }
    @Test
    void testAddBoletoRow() {
        String[] row = {"11/10/2010", "10/10/2010", "10/10/2010", "10/10/2010", "10/10/2010", "10/10/2010"};
        csvFileHandler.addBoletoRow(row);
        Assertions.assertArrayEquals(row, csvFileHandler.readBoletoLastLine());

    }

    @Test
    void testAddNfRow() {
        String[] row = {"10/10/2010", "10/10/2010", "10/10/2010", "10/10/2010"};
        csvFileHandler.addNfRow(row);
        Assertions.assertArrayEquals(row, csvFileHandler.readNfLastLine());

    }

    @Test
    void testCheckHeaderBoleto() {

        csvFileHandler.checkHeaderBoleto();
        String[] boletoFirstLine = csvFileHandler.readBoletoFirstLine();
        Assertions.assertArrayEquals(csvFileHandler.getBoletoHeader(), boletoFirstLine);

    }

    @Test
    void testCheckHeaderNf() {
        csvFileHandler.checkHeaderNf();
        String[] nfFirstLine = csvFileHandler.readNfFirstLine();
        Assertions.assertArrayEquals(csvFileHandler.getNfHeader(), nfFirstLine);

    }
}
