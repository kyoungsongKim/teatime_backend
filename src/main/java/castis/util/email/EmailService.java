package castis.util.email;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@NoArgsConstructor
public class EmailService {

    @Async
    public void sendEmail(String title, String contents, EmailSender from, String to)
            throws MessagingException, UnsupportedEncodingException {

        log.info("send e-mail title:{}, from:{}, to:{}", title, from, to);

        boolean sessionDebug = false;
        Properties props = System.getProperties();
        props.put("mail.host", "gwsmtp.ktbizoffice.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.ssl.trust", "gwsmtp.ktbizoffice.com");
        props.put("mail.smtp.host", "gwsmtp.ktbizoffice.com");

        // Java 1.8
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Authenticator auth = new Authenticator() {
            // override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("kskim@castis.com", "fjqjrnsk79!");
            }
        };

        Session session = Session.getDefaultInstance(props, auth);
        session.setDebug(sessionDebug);

        Message message = new MimeMessage(session);
        // Multipart
        Multipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        // Now set the actual message
        messageBodyPart.setContent(contents, "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);

        message.setHeader("X-Mailer", "d-sdn");
        message.setHeader("MIME-Version", "1.0");
        message.setHeader("Content-Type", multipart.getContentType());

        message.setFrom(
                new InternetAddress(from.getEmailAddress(), MimeUtility.encodeText(from.getPersonal(), "UTF-8", "B")));

        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(MimeUtility.encodeText(title, "UTF-8", "B"));
        message.setSentDate(new Date());
        message.setContent(multipart);

        Transport.send(message);
        log.info("send mail {} to {} success", from, to);

    }
}
