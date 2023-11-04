package br.com.medeirosecia.analyzemail.infra.email;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.ModifyMessageRequest;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailLabelDAO;
import br.com.medeirosecia.analyzemail.domain.repository.EmailMessageDAO;

public class GmailProvider implements EmailProvider {

	/**
	 *
	 */
	private static final String ANALYZED_MAIL = "analyzedmail";
	Gmail service = null;
	private String user = "me";
	private static final String APPLICATION_NAME = "AnalyzeMail";
	private GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	private String tokensFolder;
	private final List<String> scopes = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
	private EmailLabelDAO emailLabel;

	private String credentialsFile;

	public void setCredentialsFile(String credentialsFile) {
		this.credentialsFile = credentialsFile;
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

	private void connect() {
		// Build a new authorized API client service.

		try {
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			this.service = new Gmail.Builder(HTTP_TRANSPORT, jsonFactory, getCredentials(HTTP_TRANSPORT))
					.setApplicationName(APPLICATION_NAME)
					.build();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Message getMessage(String messageId) {
		Message msg = null;
		try {
			msg = this.service.users().messages().get(user, messageId).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}

	public EmailLabelDAO getEmailLabel() {
		List<EmailLabelDAO> emailLabels = listLabels();
		for (EmailLabelDAO emailLabelDTO : emailLabels) {
			if (emailLabelDTO.getName().toLowerCase().contains(ANALYZED_MAIL.toLowerCase())) {
				this.emailLabel = emailLabelDTO;
				return emailLabelDTO;
			}
		}
		return null;
		// FEAT criar label automaticamente no gmail
	}

	private List<EmailLabelDAO> listLabels() {
		List<EmailLabelDAO> emailLabels = new ArrayList<>();

		ListLabelsResponse listResponse;
		try {
			listResponse = service.users().labels().list(user).execute();

			List<Label> labels = listResponse.getLabels();

			if (!labels.isEmpty()) {
				for (Label label : labels) {
					emailLabels.add(new EmailLabelDAO(label.getId(), label.getName()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return emailLabels;
	}

	public void getMessagesWithoutLabel(List<EmailMessageDAO> listEmailMessagesDAO) {
		List<Message> messages = new ArrayList<>();
		if (this.service != null) {
			try {
				ListMessagesResponse listMessageResponse = this.service.users().messages().list(user)
						.setQ("!label:" + ANALYZED_MAIL)
						.execute();
				messages = listMessageResponse.getMessages();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (messages != null && !messages.isEmpty()) {
			for (Message message : messages) {
				var emailMessage = new EmailMessageDAO(message.getId());
				listEmailMessagesDAO.add(emailMessage);
			}
		}
		return;
	}

	public void getAllMessages(List<EmailMessageDAO> listEmailMessagesDAO) {
		// TODO get all messages of the inbox, not only 500
		List<Message> messages = new ArrayList<>();
		if (this.service != null) {
			try {
				ListMessagesResponse listMessageResponse = this.service.users().messages().list(user)
						.setQ("")
						.setMaxResults(500L)
						.execute();
				messages = listMessageResponse.getMessages();

				// Retrieve all messages from the inbox
				while (listMessageResponse.getNextPageToken() != null) {
					listMessageResponse = this.service.users().messages().list(user)
							.setQ("")
							.setMaxResults(500L)
							.setPageToken(listMessageResponse.getNextPageToken())
							.execute();
					messages.addAll(listMessageResponse.getMessages());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (messages != null && !messages.isEmpty()) {
			for (Message message : messages) {
				var emailMessage = new EmailMessageDAO(message.getId());
				listEmailMessagesDAO.add(emailMessage);
			}
		}
		return;
	}

	private byte[] downloadAttachment(MessagePart part, String messageId) {
		String attId = part.getBody().getAttachmentId();
		MessagePartBody attachPart;
		try {
			attachPart = service.users().messages().attachments().get(user, messageId, attId).execute();
			return Base64.decodeBase64(attachPart.getData());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	public List<EmailAttachmentDAO> listAttachments(EmailMessageDAO emailMessageDAO, String[] extensions) {
		List<EmailAttachmentDAO> emailAttachments = new ArrayList<>();
		Message fullMessage = getMessage(emailMessageDAO.getId());

		if (fullMessage != null) {
			List<MessagePart> parts = fullMessage.getPayload().getParts();

			if (parts != null && !parts.isEmpty()) {
				for (MessagePart part : parts) {
					addMatchingAttachments(part, extensions, emailMessageDAO.getId(), emailAttachments);
				}
			}
		}

		return emailAttachments;
	}

	private void addMatchingAttachments(MessagePart part, String[] extensions, String messageId,
			List<EmailAttachmentDAO> emailAttachments) {
		if (part != null && part.getFilename() != null && part.getFilename().length() > 0) {
			for (String ext : extensions) {
				if (isExtensionMatch(part.getFilename(), ext)) {
					byte[] fileByteArray = downloadAttachment(part, messageId);
					String filename = part.getFilename().replaceAll("[\\\\/:*?\"<>|]", "_");
					EmailAttachmentDAO attachment = new EmailAttachmentDAO(filename, fileByteArray);
					emailAttachments.add(attachment);
				}
			}
		}
	}

	private boolean isExtensionMatch(String filename, String extension) {
		return filename.toLowerCase().endsWith(extension.toLowerCase());
	}

	public void setMessageWithThisLabel(String messageId) {
		List<String> listLabelsAnalyzedMail = Collections.singletonList(this.emailLabel.getId());
		ModifyMessageRequest modify = new ModifyMessageRequest().setAddLabelIds(listLabelsAnalyzedMail);
		try {
			this.service.users().messages().modify(user, messageId, modify).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadMoreMessages(boolean loadMore) {
		// TODO implementar lógica de aguardar para carregar mais mensagens
		throw new UnsupportedOperationException("Unimplemented method 'loadMoreMessages'");
	}

	@Override
	public boolean hasMoreMessages() {
		// TODO implementar lógica de aguardar para carregar mais mensagens
		throw new UnsupportedOperationException("Unimplemented method 'hasMoreMessages'");
	}

}
