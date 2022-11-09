package castis.domain.report.controller;

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
            reportUserResponseDto.setSendUserName(userInfo.get().getRealName());
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

        log.info("send mail sendServiceReqquestEmail, sendUserName:{} recvEmail:{} title:{}", sendUserName, recvEmail, title);
        Map<String, Object> response = new HashMap<String, Object>();

        StringBuilder builder = new StringBuilder();
        builder.append("<font style=\"font-family: 맑은 고딕; font-size:10pt\">안녕하세요.<br>" + sendUserName + " " + req.getTitle() + "<br><br>");
        builder.append(req.getContents().replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;"));
        builder.append("<br><br>이 메일은 IMS(Issue Management System)에서 자동으로 발송한 메일입니다.<br>차 한잔의 여유가 세상을 바꿉니다.(http://teatime.castis.net/)</font>");

        boolean sessionDebug = false;
        Properties props = System.getProperties();
        props.put("mail.host", "mail.castis.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.transport.protocol", "smtp");
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(sessionDebug);
        try {
            // Multipart
            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            // Now set the actual message
            messageBodyPart.setContent(builder.toString(), "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderEmail));
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recvEmail));
            msg.setRecipient(Message.RecipientType.CC, new InternetAddress(senderEmail));
            msg.setSubject(MimeUtility.encodeText(title, "EUC-KR", "B"));
            msg.setSubject(MimeUtility.encodeText(title, "UTF-8", "B"));
            msg.setSentDate(new Date());
            msg.setContent(multipart);

            Transport.send(msg);
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
