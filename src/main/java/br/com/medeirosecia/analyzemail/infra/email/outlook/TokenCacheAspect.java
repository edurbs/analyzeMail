package br.com.medeirosecia.analyzemail.infra.email.outlook;

import com.microsoft.aad.msal4j.ITokenCacheAccessAspect;
import com.microsoft.aad.msal4j.ITokenCacheAccessContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class TokenCacheAspect implements ITokenCacheAccessAspect {

    private String data;

    public TokenCacheAspect(String fileName) {
        this.data = readDataFromFile(fileName);
    }

    @Override
    public void beforeCacheAccess(ITokenCacheAccessContext iTokenCacheAccessContext) {
        iTokenCacheAccessContext.tokenCache().deserialize(data);
    }

    @Override
    public void afterCacheAccess(ITokenCacheAccessContext iTokenCacheAccessContext) {
        data = iTokenCacheAccessContext.tokenCache().serialize();
        // you could implement logic here to write changes to file
        // TODO
        try {
            FileWriter fileWriter = new FileWriter("C:\\temp\\sample_cache.json");
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String readDataFromFile(String resource) {
        try {
            //Determine if sample running from IDE (resource URI starts with 'file') or from a .jar (resource URI starts with 'jar'),
            //  so that sample_cache.json is read properly
            File file = new File("C:\\temp\\sample_cache.json");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            return stringBuilder.toString();
            
        } catch (Exception ex){
            System.out.println("Error reading data from file: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
