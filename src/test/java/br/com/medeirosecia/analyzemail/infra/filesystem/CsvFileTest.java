package br.com.medeirosecia.analyzemail.infra.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CsvFileTest {

    CsvFile csvFile;
    String[] header;
    String tempFile;

    @BeforeEach
    void setup(){
        this.tempFile = System.getProperty("java.io.tmpdir")+"test.csv";
        this.header = new String[] {"teste1", "teste2", "teste3", "teste4", "teste5", "teste6"};
        this.csvFile = new CsvFile(tempFile, header);
    }

    @AfterEach
    void tearDown() {
        this.csvFile = null;

        Path tempFilePath = Path.of(tempFile);
        try {
            Files.deleteIfExists(tempFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testAddRow() {
        String[] row = {"11/10/2010", "10/10/2010", "10/10/2010", "10/10/2010", "10/10/2010", "10/10/2010"};
        csvFile.addRow(row);

        var lines = csvFile.getAllLines();
        var firstLine = lines.get(0);
        var lastLine = lines.get(lines.size()-1);

        Assertions.assertArrayEquals(row, lastLine);
        Assertions.assertArrayEquals(this.header, firstLine);
        Assertions.assertEquals(2, lines.size());

    }


}
