package br.com.medeirosecia.analyzemail.infra.email;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.requests.AttachmentCollectionPage;
import com.microsoft.graph.requests.AttachmentRequest;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MailFolderCollectionPage;
import com.microsoft.graph.requests.MessageCollectionPage;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;
import okhttp3.Request;

public class OutlookProvider implements EmailProvider {
   

    private List<String> scopes = Arrays.asList("Mail.ReadWrite.Shared", "Mail.ReadWrite", "MailboxSettings.ReadWrite" );
    
    private static final String ANALYZED_MAIL = "analyzedMail";

    private String clientId;
    private String tenantId;
    private String username;
    private String password;
    private String sharedMailboxId;

    private GraphServiceClient<Request> graphClient;
    
   
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

            this.graphClient = getServiceClient();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public EmailLabelDAO getEmailLabel() {
        return new EmailLabelDAO(ANALYZED_MAIL, ANALYZED_MAIL);
    }

    @Override
    public List<EmailMessageDAO> getNotAnalyzedMessages() {
        List<EmailMessageDAO> list = new ArrayList<>();
        
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
                .messages(messageId)
                .buildRequest()
                .patch(message);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    

    }


    
} 
    

