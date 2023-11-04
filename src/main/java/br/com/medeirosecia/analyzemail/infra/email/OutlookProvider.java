package br.com.medeirosecia.analyzemail.infra.email;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

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
import com.microsoft.graph.requests.MailFolderCollectionRequestBuilder;
import com.microsoft.graph.requests.MessageCollectionPage;
import com.microsoft.graph.requests.MessageCollectionRequestBuilder;

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

    private boolean hasMoreMessages = true;
    private boolean getMoreMessages = true;


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
            e.printStackTrace();
        }

    }

    @Override
    public EmailLabelDAO getEmailLabel() {
        return new EmailLabelDAO(ANALYZED_MAIL, ANALYZED_MAIL);
    }

    private void getFoldersList( @Nonnull String filter, List<EmailMessageDAO> listEmailMessagesDAO){

        int pageSize = 999;
        int skip = 0;


        if(sharedMailboxId == null || sharedMailboxId.isEmpty()){
            return;
        }

        MailFolderCollectionPage mailFolderCollectionPage = graphClient
                .users(sharedMailboxId)
                .mailFolders()
                .buildRequest()
                .top(pageSize)
                .skip(skip)
                .get();

        while(mailFolderCollectionPage != null){
            final List<MailFolder> mailFoldersPage = mailFolderCollectionPage.getCurrentPage();

            for(MailFolder mailFolder : mailFoldersPage){

                addToMessageList(mailFolder, filter, pageSize, listEmailMessagesDAO);
            }

            final MailFolderCollectionRequestBuilder nextMailFolderCollectionPage = mailFolderCollectionPage.getNextPage();

            if(nextMailFolderCollectionPage == null){
                break;
            }else{
                skip += pageSize;
                mailFolderCollectionPage = nextMailFolderCollectionPage
                    .buildRequest()
                    .get();

            }
        }

        this.hasMoreMessages = false;
    }

    private void addToMessageList(MailFolder mailFolder, @Nonnull String filter, int pageSize, List<EmailMessageDAO> listEmailMessagesDAO){
        int skip = 0;


        MessageCollectionPage messageCollectionPage = graphClient
                .users(sharedMailboxId)
                .mailFolders(mailFolder.id)
                .messages()
                .buildRequest()
                .filter(filter)
                .top(pageSize)
                .skip(skip)
                .get();

        while (messageCollectionPage != null) {

            final List<Message> listMessagesCurrentPage = messageCollectionPage.getCurrentPage();

            for (Message message: listMessagesCurrentPage) {

                while (!getMoreMessages){ // wait for next request to get more messages
                    waitToGetMoreMessages();
                }

                EmailMessageDAO emailMessageDAO = new EmailMessageDAO(message.id, message.parentFolderId);
                listEmailMessagesDAO.add(emailMessageDAO);

            }

            final MessageCollectionRequestBuilder nextMessageCollectionPage = messageCollectionPage.getNextPage();
            if(nextMessageCollectionPage == null){
                break;
            }else{
                skip += pageSize;
                messageCollectionPage = nextMessageCollectionPage
                    .buildRequest()
                    .get();
            }
        }
    }

    private void waitToGetMoreMessages() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void getMessagesWithoutLabel(List<EmailMessageDAO> listEmailMessagesDAO) {
        String filter = "categories/any(c:c ne '" + ANALYZED_MAIL + "')";
        Thread thread = new Thread(() -> getFoldersList(filter, listEmailMessagesDAO));
        thread.start();

    }

    @Override
    public void getAllMessages(List<EmailMessageDAO> listEmailMessagesDAO) {
        String filter = "";
        Thread thread = new Thread(() -> getFoldersList(filter, listEmailMessagesDAO));
        thread.start();
    }

    @Override
    public boolean hasMoreMessages(){
        return this.hasMoreMessages;
    }

    @Override
    public void loadMoreMessages(boolean loadMore){
        this.getMoreMessages = loadMore;
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
