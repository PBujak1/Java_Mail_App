import com.sendgrid.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class SendgridMailer {
    private static final String API_KEY = "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";

    public static void sendEmail(String from, String to, String subject, String body, String filePath) throws IOException {
        Email fromEmail = new Email(from);
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        String attachmentName = "Brak";

        if (filePath != null && !filePath.trim().isEmpty()) {
            try {
                byte[] fileData = Files.readAllBytes(Paths.get(filePath));
                String encodedFile = Base64.getEncoder().encodeToString(fileData);

                Attachments attachment = new Attachments();
                attachment.setContent(encodedFile);
                attachment.setType(Files.probeContentType(Paths.get(filePath)));
                attachment.setFilename(Paths.get(filePath).getFileName().toString());
                attachment.setDisposition("attachment");

                mail.addAttachments(attachment);
                attachmentName = Paths.get(filePath).getFileName().toString();
            } catch (IOException e) {
                System.err.println("Nie udało się wczytać załącznika: " + e.getMessage());
            }
        }

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            System.out.println("Status: " + response.getStatusCode());

            // Tworzenie i zapis raportu
            Raport raport = new Raport(from, to, subject, body, attachmentName);
            RaportZapis.zapiszRaport(raport, "raport.ser");

        } catch (IOException ex) {
            throw ex;
        }
    }
}
