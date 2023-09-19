package br.com.medeirosecia.analyzemail.infra.email;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.io.FilenameUtils;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.OutlookCategory;
import com.microsoft.graph.requests.AttachmentCollectionPage;
import com.microsoft.graph.requests.AttachmentRequest;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MailFolderCollectionPage;
import com.microsoft.graph.requests.MessageCollectionPage;
import com.microsoft.graph.requests.OutlookCategoryCollectionPage;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;
import br.com.medeirosecia.analyzemail.infra.email.outlook.TokenCacheAspect;
import okhttp3.Request;

public class OutlookProvider implements EmailProvider {
   

    private List<String> scopes = Arrays.asList("Mail.ReadWrite.Shared", "Mail.ReadWrite", "MailboxSettings.ReadWrite" );
    //private List<String> scopes = Arrays.asList("Mail.ReadWrite", "MailboxSettings.ReadWrite" );
    private Set<String> scopes2 = new HashSet<>(Arrays.asList("Mail.ReadWrite", "MailboxSettings.ReadWrite", "Mail.ReadWrite.Shared"));

    private static final String ANALYZED_MAIL = "analyzedMail";
    private EmailLabelDAO emailLabelDAO;

    private String clientId;
    private String tenantId;
    private String username;
    private String password;
    private String sharedMailboxId;
    private String authority="https://login.microsoftonline.com/8e5013a3-d95b-4c0d-a725-225810d9f765/";
    private String redirectUrl = "https://login.microsoftonline.com/common/oauth2/nativeclient";
    private String clientSecret;
    private String scope="https://graph.microsoft.com/.default";

    private String credentialsFile;
    private GraphServiceClient<Request> graphClient;
    
    

    private GraphServiceClient<Request> getServiceClient49(){

        DeviceCodeCredential credential = new DeviceCodeCredentialBuilder()
            .clientId(clientId).tenantId(tenantId).challengeConsumer(challenge -> {
                // Display challenge to the user
                System.out.println(challenge.getMessage());
            }).build();

        if (null == scopes || null == credential) {
            System.out.println("Erro de credencial!");
            return null;
        }
        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
            scopes, credential);

        var graphClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider).buildClient();

        var mailbox = graphClient.users(sharedMailboxId).mailFolders().buildRequest().get();

        // Get the emails in the shared mailbox
        List<MailFolder> emailMessages = mailbox.getCurrentPage();       

        // Print the emails
        for (MailFolder emailMessage : emailMessages) {
            System.out.println(emailMessage.displayName);
        }

        return graphClient;

    }

    private GraphServiceClient<Request> getServiceClient51(){
        GraphServiceClient<Request> graphClient = null;
        // Load token cache from file and initialize token cache aspect. The token cache will have
        // dummy data, so the acquireTokenSilently call will fail.
        TokenCacheAspect tokenCacheAspect = new TokenCacheAspect("sample_cache.json");

        PublicClientApplication pca;
        try {
            pca = PublicClientApplication.builder(clientId)
                    .authority(authority)
                    .setTokenCacheAccessAspect(tokenCacheAspect)
                    .build();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        Set<IAccount> accountsInCache = pca.getAccounts().join();
        // Take first account in the cache. In a production application, you would filter
        // accountsInCache to get the right account for the user authenticating.
        IAccount account = accountsInCache.iterator().next();

        IAuthenticationResult result;
        try {
            SilentParameters silentParameters =
                    SilentParameters
                            .builder(scopes2, account)
                            .build();

            // try to acquire token silently. This call will fail since the token cache
            // does not have any data for the user you are trying to acquire a token for
            result = pca.acquireTokenSilently(silentParameters).join();
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {

                Consumer<DeviceCode> deviceCodeConsumer = (DeviceCode deviceCode) ->
                        System.out.println(deviceCode.message());
                DeviceCodeCredential credential = new DeviceCodeCredentialBuilder()
                        .clientId(clientId).tenantId(tenantId).challengeConsumer(challenge -> {
                            // Display challenge to the user
                            System.out.println(challenge.getMessage());
                        }).build();

                TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
                            scopes, credential);
                graphClient = GraphServiceClient.builder()
                            .authenticationProvider(authProvider).buildClient();

                DeviceCodeFlowParameters parameters =
                        DeviceCodeFlowParameters
                                .builder(scopes2, deviceCodeConsumer)
                                .build();

                // Try to acquire a token via device code flow. If successful, you should see
                // the token and account information printed out to console, and the sample_cache.json
                // file should have been updated with the latest tokens.
                result = pca.acquireToken(parameters).join();
             } 
        }
        return graphClient;
    }
    




    private GraphServiceClient<Request> getServiceClient(){
        UsernamePasswordCredential credential = new UsernamePasswordCredentialBuilder()
                .clientId(clientId)                
                .tenantId(tenantId)
                .username(username)
                .password(password)
                .build();
        
        if (credential != null) {
            TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(this.scopes, credential);
            return GraphServiceClient.builder()
                    .authenticationProvider(authProvider)
                    .buildClient();
        }
        return null;
    }

    @Override
    public void setCredentialsFile(String credentialsFile) {
        
        ObjectMapper objectMapper = new ObjectMapper();
        File json = new File(credentialsFile);
        try {
            JsonNode jsonNode = objectMapper.readTree(json);

            this.clientId = jsonNode.get("clientId").asText();   
            this.tenantId = jsonNode.get("tenantId").asText();
            this.username = jsonNode.get("username").asText();
            this.password = jsonNode.get("password").asText();
            this.sharedMailboxId = jsonNode.get("sharedMailboxId").asText();
            this.clientSecret = jsonNode.get("clientSecret").asText();

            this.graphClient = getServiceClient();

            MailFolderCollectionPage mailFolders = graphClient.me().mailFolders()
            .buildRequest()
            .get();
            
            return;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public EmailLabelDAO getEmailLabel() {
        // OutlookCategoryCollectionPage masterCategories;
        // try {
        //     masterCategories = graphClient
        //         .users(sharedMailboxId)
        //         //.me()
        //         .outlook()
        //         .masterCategories()
        //         .buildRequest()
        //         .get();
            
        // } catch (ClientException e) {
        //     System.out.println(e.getMessage());
        //     return null;
        // }
        
        // if(masterCategories!=null){            
        //     List<OutlookCategory> labels = masterCategories.getCurrentPage();
        //     for (OutlookCategory label : labels) {
        //         if(label.displayName!=null && label.displayName.equals(ANALYZED_MAIL)){
        //             this.emailLabelDAO = new EmailLabelDAO(label.id, label.displayName);
        //             return this.emailLabelDAO;
        //         }
        //     }
        // }
        return new EmailLabelDAO(ANALYZED_MAIL, ANALYZED_MAIL);
    }

    @Override
    public List<EmailMessageDAO> getNotAnalyzedMessages() {
        List<EmailMessageDAO> list = new ArrayList<>();
        // $filter categories/any(c:c eq 'Yellow')
        String filter = "categories/any(c:c ne '" + ANALYZED_MAIL + "')";

        MailFolderCollectionPage mailFolders = graphClient.users(sharedMailboxId).mailFolders().buildRequest().top(10000).get();
        for (MailFolder mailFolder : mailFolders.getCurrentPage()) {
            MessageCollectionPage messageCollectionPage = graphClient
                    .users(sharedMailboxId)
                    .mailFolders(mailFolder.id)
                    .messages()
                    .buildRequest()
                    .filter(filter)
                    .top(100)
                    .get();


           
            List<Message> messages = messageCollectionPage.getCurrentPage();
    
            if (!messages.isEmpty()){                    
    
                for (Message message: messages) {                
                    List<String> categories = message.categories;
                    if(!categories.contains(ANALYZED_MAIL)){
                        EmailMessageDAO emailMessageDAO = new EmailMessageDAO(message.id, message.parentFolderId);                
                        list.add(emailMessageDAO);                                
                    }     
                }
                if(!list.isEmpty()){
                    return list;    
                }
            
            } 

        
        }        
        return Collections.emptyList();
    }

    @Override
    public List<EmailAttachmentDAO> listAttachments(EmailMessageDAO emailMessageDAO, String[] extensions) {
        if (emailMessageDAO == null || extensions == null || extensions.length == 0) {
            return Collections.emptyList();
        }

        try {
            
            AttachmentCollectionPage attachmentCollectionPage = graphClient
                    .users(sharedMailboxId)
                    //.me()
                    .mailFolders(emailMessageDAO.getFolderId())
                    .messages(emailMessageDAO.getId())
                    .attachments()
                    .buildRequest()
                    .get();
        
            if (attachmentCollectionPage == null) {
                return Collections.emptyList();
            }
        
            List<Attachment> attachments = attachmentCollectionPage.getCurrentPage();
            if (attachments.isEmpty()) {
                return Collections.emptyList();
            }
        
            List<EmailAttachmentDAO> emailAttachmentsDAO = new ArrayList<>();
            for (Attachment attachment : attachments) {
                if(attachment.name!=null){
                    String filename = attachment.name.replaceAll("[\\\\/:*?\"<>|]", "_");            
                    String extension = FilenameUtils.getExtension(filename.toUpperCase());
                    if (Arrays.asList(extensions).contains(extension)) {
                        AttachmentRequest attachmentRequest = graphClient
                                .users(sharedMailboxId)
                                //.me()
                                .messages(emailMessageDAO.getId())
                                .attachments(attachment.id)
                                .buildRequest();
                        Attachment fullAttachment = attachmentRequest.get();
                        
                        if(fullAttachment instanceof FileAttachment){
                            FileAttachment fileAttachment = (FileAttachment) fullAttachment;
                            byte[] attachmentContent = fileAttachment.contentBytes;
                            EmailAttachmentDAO emailAttachmentDAO = new EmailAttachmentDAO(filename, attachmentContent);
                            emailAttachmentsDAO.add(emailAttachmentDAO);    
                        }
                }
                    
                }
            }
            return emailAttachmentsDAO;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    
    }
    
    @Override
    public void setMessageWithThisLabel(String messageId) {
        
        if(messageId==null) return;
        
        List<String> categories = Collections.singletonList(ANALYZED_MAIL);

        Message message = new Message();
            
        message.categories = categories;

        try {
            graphClient
                .users(sharedMailboxId)
                //.me()
                .messages(messageId)
                .buildRequest()
                .patch(message);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    

    }


    
} 
    

