package br.com.medeirosecia.analyzemail.infra.email;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.OutlookCategory;
import com.microsoft.graph.models.Request;
import com.microsoft.graph.requests.AttachmentCollectionPage;
import com.microsoft.graph.requests.AttachmentRequest;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import com.microsoft.graph.requests.OutlookCategoryCollectionPage;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;

public class OutlookProvider implements EmailProvider {
   

    private List<String> scopes = Arrays.asList("Mail.ReadWrite", "MailboxSettings.ReadWrite");

    private static final String ANALYZED_MAIL = "analyzedMail";
    private EmailLabelDAO emailLabelDAO;

    private String clientId;
    private String tenantId ;
    private String username ;
    private String password;

    private String credentialsFile;
    

    private GraphServiceClient<Request> graphClient;    


    
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
            this.graphClient = getServiceClient();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public EmailLabelDAO getEmailLabel() {
        OutlookCategoryCollectionPage masterCategories = graphClient
            .me()
            .outlook()
            .masterCategories()
            .buildRequest()
            .get();
        
        if(masterCategories!=null){            
            List<OutlookCategory> labels = masterCategories.getCurrentPage();
            for (OutlookCategory label : labels) {
                if(label.displayName!=null && label.displayName.equals(ANALYZED_MAIL)){
                    this.emailLabelDAO = new EmailLabelDAO(label.id, label.displayName);
                    return this.emailLabelDAO;
                }
            }
        }
        return null;
    }

    @Override
    public List<EmailMessageDAO> getNotAnalyzedMessages() {

        // $filter categories/any(c:c eq 'Yellow')
        String filter = "categories/any(c:c ne '" + ANALYZED_MAIL + "')";
        
        MessageCollectionPage messageCollectionPage = this.graphClient
                .me()
                .messages()                
                .buildRequest()
                .filter(filter)
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
                return list;            
            } 

        }
                
        return Collections.emptyList();
    }

    @Override
    public List<EmailAttachmentDAO> listAttachments(String messageId, String[] extensions) {
        if (messageId == null || extensions == null || extensions.length == 0) {
            return Collections.emptyList();
        }
    
        AttachmentCollectionPage attachmentCollectionPage = graphClient.me()
                .messages(messageId)
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
            String filename = attachment.name;
            String extension = FilenameUtils.getExtension(filename);
            if (Arrays.asList(extensions).contains(extension)) {
                AttachmentRequest attachmentRequest = graphClient.me()
                        .messages(messageId)
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
        return emailAttachmentsDAO;
    }
    
    
    
    
    @Override
    public void setMessageWithThisLabel(String messageId) {
        
        if(messageId==null) return;
        
        List<String> categories = Collections.singletonList(ANALYZED_MAIL);

        Message message = new Message();
            
        message.categories = categories;

        graphClient.me()
            .messages(messageId)
            .buildRequest()
            .patch(message);
    

    }


    
} 
    

