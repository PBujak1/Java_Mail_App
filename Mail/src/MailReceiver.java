import javax.mail.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MailReceiver implements Runnable {
    private String username;
    private String password;
    private final String host = "ssl0.ovh.net";
    private String recipientEmail;
    private Store store;
    private Folder inbox;
    private Message[] messages;

    public MailReceiver(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    @Override
    public void run() {
        try {
            connect();
            fetchAndSaveToDatabase(recipientEmail);
            disconnect();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws MessagingException {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");

        Session session = Session.getDefaultInstance(props);
        store = session.getStore("imaps");
        store.connect(host, username, password);

        inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        messages = inbox.getMessages();
    }

    public void disconnect() {
        try {
            if (inbox != null && inbox.isOpen()) inbox.close(false);
            if (store != null && store.isConnected()) store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public List<String> getSenders() {
        List<String> senders = new ArrayList<>();
        try {
            for (Message message : messages) {
                senders.add(message.getFrom()[0].toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return senders;
    }

    public List<String> getSubjects() {
        List<String> subjects = new ArrayList<>();
        try {
            for (Message message : messages) {
                subjects.add(message.getSubject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subjects;
    }

    public List<String> getContents() {
        List<String> contents = new ArrayList<>();
        try {
            for (Message message : messages) {
                Object content = message.getContent();
                if (content instanceof String) {
                    contents.add((String) content);
                } else if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content;
                    boolean found = false;
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart part = multipart.getBodyPart(i);
                        if (part.getContentType().startsWith("text/plain")) {
                            contents.add(part.getContent().toString());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        contents.add("[Brak treści tekstowej]");
                    }
                } else {
                    contents.add("[Nieznany format]");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents;
    }

    public void fetchAndSaveToDatabase(String recipientEmail) {
        try {
            for (Message message : messages) {
                String sender = message.getFrom()[0].toString();
                String subject = message.getSubject();
                String content = "";

                Object msgContent = message.getContent();
                if (msgContent instanceof String) {
                    content = (String) msgContent;
                } else if (msgContent instanceof Multipart) {
                    Multipart multipart = (Multipart) msgContent;
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart part = multipart.getBodyPart(i);
                        if (part.getContentType().startsWith("text/plain")) {
                            content = part.getContent().toString();
                            break;
                        }
                    }
                }

                // Zapisz do bazy jeśli nie istnieje
                DatabaseConnector.saveReceivedEmailIfNotExists(sender, recipientEmail, subject, content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
