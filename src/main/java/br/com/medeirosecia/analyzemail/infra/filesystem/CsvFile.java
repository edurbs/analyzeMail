package br.com.medeirosecia.analyzemail.infra.filesystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CsvFile {

    private String filePath;
    private String[] header;

    public CsvFile(String filePath, String[] header){
        this.filePath = filePath;
        this.header = header;
    }

    public CsvFile(String filePath){
        this.filePath = filePath;
    }

    private void checkHeader(){

        File file = new File(filePath);

        if (file.length() == 0) {
            try (FileWriter writer = new FileWriter(file)) {
                // Write the header to the file
                writer.write(String.join(";", header) + System.lineSeparator());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addRow(String[] row) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            checkHeader();
            writer.append(String.join(";", row) + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getAllLines(){
        List<String[]> listLines = new ArrayList<>();

        try  {
            List<String> allCsvLines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            for (String line : allCsvLines) {
                String[] row = line.split(";");
                listLines.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return listLines;
    }


}
