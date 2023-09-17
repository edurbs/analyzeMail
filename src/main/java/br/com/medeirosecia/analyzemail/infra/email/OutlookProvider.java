package br.com.medeirosecia.analyzemail.infra.email;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Request;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;

// import com.microsoft.aad.msal4j.IAccount;
// import com.microsoft.aad.msal4j.IAuthenticationResult;
// import com.microsoft.aad.msal4j.MsalException;
// import com.microsoft.aad.msal4j.PublicClientApplication;
// import com.microsoft.aad.msal4j.SilentParameters;
// import com.microsoft.aad.msal4j.UserNamePasswordParameters;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;
import java.util.ArrayList;

public class OutlookProvider implements EmailProvider {
   

    private static String authority = "https://login.microsoftonline.com/organizations/";
    //private static Set<String> scope = Collections.singleton("Mail.ReadWrite");
    private List<String> scopes = Arrays.asList("Mail.ReadWrite");


    private static String clientId = ""; // App id on microsoft entra TODO save it    
    private static String tenantId = "";
    private static String username = ""; // email from gui TODO save it
    private static String password = ""; // password from gui TODO save it    
    // TODO save password cript in a file

    private GraphServiceClient<Request> graphClient;

    public OutlookProvider(){
        this.graphClient = getServiceClient();
    }
    
    private GraphServiceClient getServiceClient(){
        UsernamePasswordCredential credential = new UsernamePasswordCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .username(username)
                .password(password)
                .build();
        
        if (credential != null) {
            TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(scopes, credential);
            return GraphServiceClient.builder()
                    .authenticationProvider(authProvider)
                    .buildClient();
        }
        return null;
    }

   /*  private IAuthenticationResult getToken() {   
        
        PublicClientApplication pca=null;
        try {
            pca = PublicClientApplication.builder(clientId)
                    .authority(authority)
                    .build();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        //Get list of accounts from the application's token cache, and search them for the configured username
        //getAccounts() will be empty on this first call, as accounts are added to the cache when acquiring a token
        Set<IAccount> accountsInCache = pca.getAccounts().join();
        IAccount account = getAccountByUsername(accountsInCache, username);

        //Attempt to acquire token when user's account is not in the application's token cache
        IAuthenticationResult result = null;
        try {
            result = acquireTokenUsernamePassword(pca, scope, account, username, password);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;        }
        System.out.println("Account username: " + result.account().username());
        System.out.println("Access token:     " + result.accessToken());
        System.out.println("Id token:         " + result.idToken());
        System.out.println();

        accountsInCache = pca.getAccounts().join();
        account = getAccountByUsername(accountsInCache, username);

        //Attempt to acquire token again, now that the user's account and a token are in the application's token cache
        try {
            result = acquireTokenUsernamePassword(pca, scope, account, username, password);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Account username: " + result.account().username());
        System.out.println("Access token:     " + result.accessToken());
        System.out.println("Id token:         " + result.idToken());

        return result;
    }

    private static IAuthenticationResult acquireTokenUsernamePassword(PublicClientApplication pca,
                                                                      Set<String> scope,
                                                                      IAccount account,
                                                                      String username,
                                                                      String password) throws Exception {
        IAuthenticationResult result;
        try {
            SilentParameters silentParameters =
                    SilentParameters
                            .builder(scope)
                            .account(account)
                            .build();
            // Try to acquire token silently. This will fail on the first acquireTokenUsernamePassword() call
            // because the token cache does not have any data for the user you are trying to acquire a token for
            result = pca.acquireTokenSilently(silentParameters).join();
            System.out.println("==acquireTokenSilently call succeeded");
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {
                System.out.println("==acquireTokenSilently call failed: " + ex.getCause());
                UserNamePasswordParameters parameters =
                        UserNamePasswordParameters
                                .builder(scope, username, password.toCharArray())
                                .build();
                // Try to acquire a token via username/password. If successful, you should see
                // the token and account information printed out to console
                result = pca.acquireToken(parameters).join();
                System.out.println("==username/password flow succeeded");
            } else {
                // Handle other exceptions accordingly
                throw ex;
            }
        }
        return result;
    }

   
    private static IAccount getAccountByUsername(Set<IAccount> accounts, String username) {
        if (accounts.isEmpty()) {
            System.out.println("==No accounts in cache");
        } else {
            System.out.println("==Accounts in cache: " + accounts.size());
            for (IAccount account : accounts) {
                if (account.username().equals(username)) {
                    return account;
                }
            }
        }
        return null;
    }
*/
    @Override
    public String getUser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public void setCredentialsFile(String credentialsFile) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setCredentialsFile'");
    }

    @Override
    public EmailLabelDAO getEmailLabel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEmailLabel'");
    }

    @Override
    public List<EmailMessageDAO> getNotAnalyzedMessages() {

        MessageCollectionPage messageCollectionPage = this.graphClient
                .me()
                .messages()                
                .buildRequest()
                .top(100)
                .get();
        
        if(messageCollectionPage!=null){
            List<Message> messages = messageCollectionPage.getCurrentPage();
    
            if (!messages.isEmpty()){
                List<EmailMessageDAO> list = new ArrayList<>();
    
                for (Message message: messages) {                
                    EmailMessageDAO emailMessageDAO = new EmailMessageDAO(message.id);                
                    list.add(emailMessageDAO);
                }            
            } 

        }
                
        return Collections.emptyList();
    }

    @Override
    public List<EmailAttachmentDAO> listAttachments(String messageId, String[] extensions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listAttachments'");
    }

    @Override
    public void setMessageWithLabel(String messageId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMessageWithLabel'");
    }

    @Override
    public void setEmailLabel(EmailLabelDAO emailLabel) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setEmailLabel'");
    }

    
} 
    

