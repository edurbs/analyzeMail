package br.com.medeirosecia.analyzemail.infra.email;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

/* class to demonstrate use of Gmail list labels API */
public class MyGmail {

  Gmail service = null;
  private String user = "me"; 
  /**
   * Application name.
   */
  private static final String APPLICATION_NAME = "AnalyzeMail";

  /**
   * Global instance of the JSON factory.
   */
  private GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

  /**
   * Directory to store authorization tokens for this application.
   */
  private String tokensFolder;

  /**
   * Global instance of the scopes required by this quickstart.
   * If modifying these scopes, delete your previously saved tokens/ folder.
   */
  private final List<String> scopes = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);

  private String credentialsFile;

  public MyGmail(String credentialsFilePath) {
    this.credentialsFile = credentialsFilePath;
    this.tokensFolder = System.getProperty("user.home") + "\\.tokens";

    this.connect();
  }

  /**
   * Creates an authorized Credential object.
   *
   * @param httpTransport The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private Credential getCredentials(final NetHttpTransport httpTransport)
      throws IOException {
    // Load client secrets.
    InputStream in = new java.io.FileInputStream(credentialsFile);

    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, jsonFactory, clientSecrets, scopes)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensFolder)))
        .setAccessType("offline")
        .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  public Gmail connect() {
    // Build a new authorized API client service.

    try {
      final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      this.service = new Gmail.Builder(HTTP_TRANSPORT, jsonFactory, getCredentials(HTTP_TRANSPORT))
          .setApplicationName(APPLICATION_NAME)
          .build();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.service;
  }

  public void disconnect() {
    this.service = null;
  }

  public Gmail getConnection() {
    return this.service;
  }

  public String getUser(){
    return this.user;
  }
}