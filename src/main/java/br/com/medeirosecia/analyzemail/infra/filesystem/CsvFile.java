package br.com.medeirosecia.analyzemail.infra.filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CsvFile {

    private String filePath;
    public CsvFile(String fileName){
        this.filePath = new ConfigFile().getBaseFolder()+"\\"+fileName;
    }

    public void addHeader(String[] header){

        // Create a new file
        File file = new File(filePath);

        // Check if the file is empty
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
            writer.append(String.join(";", row) + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFirstLine() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath),
                        StandardCharsets.UTF_8))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String readLastLine() {
        Path path = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            return lines.reduce((first, second) -> second).orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
