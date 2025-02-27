package castis.domain.report.controller;

import castis.domain.report.dto.ReportEmailRequestDto;
import castis.domain.report.dto.ReportRequestDto;
import castis.domain.report.dto.ReportUserRequestDto;
import castis.domain.report.dto.ReportUserResponseDto;
import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import castis.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final UserService userService;

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ReportUserResponseDto> getUserInfo(@RequestBody ReportUserRequestDto req) throws Exception {

        Optional<User> userInfo = userService.findById(req.getUserId());
        if (userInfo.isPresent()) {
            ReportUserResponseDto reportUserResponseDto = new ReportUserResponseDto();
            reportUserResponseDto.setSendUserName(userInfo.get().getUserName());
            reportUserResponseDto.setReceiveEmail(userInfo.get().getDailyReportList());
            reportUserResponseDto.setSenderEmail(userInfo.get().getEmail());

            return ResponseEntity.ok().body(reportUserResponseDto);
        } else {
            throw new UserNotFoundException("User Not Found");
        }
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> sendServiceRequestEmail(@RequestBody ReportRequestDto req) throws Exception {
        String sendUserName = req.getSendUserName();
        String title = "[업무보고] " + req.getTitle();
        String recvEmail = req.getReceiveEmail();
        String senderEmail = req.getSenderEmail();

        log.info("send mail sendServiceReqquestEmail, sendUserName:{} recvEmail:{} title:{}", sendUserName, recvEmail,
                title);
        Map<String, Object> response = new HashMap<String, Object>();

        StringBuilder builder = new StringBuilder();
        builder.append("<font style=\"font-family: 맑은 고딕; font-size:10pt\">안녕하세요.<br>" + sendUserName + " "
                + req.getTitle() + "<br><br>");
        builder.append(req.getContents().replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;"));
        builder.append(
                "<br><br>이 메일은 IMS(Issue Management System)에서 자동으로 발송한 메일입니다.<br>차 한잔의 여유가 세상을 바꿉니다.(http://teatime.castis.net/)</font>");

        boolean sessionDebug = false;
        Properties props = System.getProperties();
        props.put("mail.host", "gwsmtp.ktbizoffice.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth",true);
        props.put("mail.smtp.starttls.enable",true);
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
        try {
            Message message = new MimeMessage(session);
            // Multipart
            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            // Now set the actual message
            messageBodyPart.setContent(builder.toString(), "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);

            message.setHeader("X-Mailer","d-sdn");
            message.setHeader("MIME-Version", "1.0");
            message.setHeader("Content-Type", multipart.getContentType());

            message.setFrom(new InternetAddress("kskim@castis.com", MimeUtility.encodeText("사람사업부 전산시스템", "UTF-8", "B")));

            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recvEmail));
            message.setRecipient(Message.RecipientType.CC, new InternetAddress(senderEmail));
            message.setSubject(MimeUtility.encodeText(title, "UTF-8", "B"));
            message.setSentDate(new Date());
            message.setContent(multipart);

            Transport.send(message);
            response.put("isSuccess", true);
            log.info("send mail {} to {} success", sendUserName, senderEmail);
        } catch (MessagingException mex) {
            log.error("{}", mex.getMessage());
            response.put("isSuccess", false);
            response.put("errorString", mex.getMessage());
            mex.printStackTrace();
        }

        return response;
    }

    @RequestMapping(value = "/emails", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> sendServiceRequestEmails(@RequestBody ReportEmailRequestDto req) throws Exception {
        String sendUserName = req.getSendUserName();
        User user = userService.findById(sendUserName).orElseThrow(() -> new UserNotFoundException("User Not Found"));
        String title = "[업무보고] " + req.getTitle();
        List<String> recvEmail = req.getReceiveEmail();
        String senderEmail = user.getEmail();

        log.info("send mail sendServiceRequestEmail, sendUserName:{} recvEmail:{} title:{}", sendUserName, recvEmail,
                title);
        Map<String, Object> response = new HashMap<String, Object>();

        StringBuilder builder = new StringBuilder();
        builder.append("<font style=\"font-family: 맑은 고딕; font-size:10pt\">안녕하세요.<br>" + user.getRealName() + " "
                + req.getTitle() + "<br><br>");
        builder.append(req.getContents().replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;"));
        builder.append(
                "<br><br>이 메일은 IMS(Issue Management System)에서 자동으로 발송한 메일입니다.<br>차 한잔의 여유가 세상을 바꿉니다.(http://teatime.castis.net/)</font>");

        boolean sessionDebug = false;
        Properties props = System.getProperties();
        props.put("mail.host", "gwsmtp.ktbizoffice.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth",true);
        props.put("mail.smtp.starttls.enable",true);
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
        try {
            Message message = new MimeMessage(session);
            // Multipart
            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setContent(builder.toString(), "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);

            message.setHeader("X-Mailer","d-sdn");
            message.setHeader("MIME-Version", "1.0");
            message.setHeader("Content-Type", multipart.getContentType());

            message.setFrom(new InternetAddress("kskim@castis.com", MimeUtility.encodeText("사람사업부 전산시스템", "UTF-8", "B")));

            // 여러 이메일 주소 추가
            InternetAddress[] recipientAddresses = recvEmail.stream().map(email -> {
                        try {
                            return new InternetAddress(email);
                        } catch (Exception e) {
                            log.error("Invalid email address: {}", email, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toArray(InternetAddress[]::new);

            if (recipientAddresses.length == 0) {
                response.put("isSuccess", false);
                response.put("errorString", "No valid email addresses found.");
                return response;
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddresses);
            message.setRecipient(Message.RecipientType.CC, new InternetAddress(senderEmail));
            message.setSubject(MimeUtility.encodeText(title, "UTF-8", "B"));
            message.setSentDate(new Date());
            message.setContent(multipart);

            Transport.send(message);
            response.put("isSuccess", true);
            log.info("send mail {} to {} success", sendUserName, senderEmail);
        } catch (MessagingException mex) {
            log.error("{}", mex.getMessage());
            response.put("isSuccess", false);
            response.put("errorString", mex.getMessage());
            mex.printStackTrace();
        }

        return response;
    }

}
