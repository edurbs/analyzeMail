package br.com.medeirosecia.analyzemail.infra.filesystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadCnpjFile {
    private List<String> cnpjListPayers = new ArrayList<>();

    public ReadCnpjFile(){
        var configFile = new ConfigFile();
        String pathCnpjPayers = configFile.getPathCnpjPayersPath();

        try (BufferedReader reader = new BufferedReader(new FileReader(pathCnpjPayers))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("[\\D]", "");
                cnpjListPayers.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<String> getCnpjListPayers(){
        return this.cnpjListPayers;
    }
}
