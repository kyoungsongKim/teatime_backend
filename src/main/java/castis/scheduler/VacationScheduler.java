package castis.scheduler;

import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import castis.domain.vacation.entity.VacationHistory;
import castis.domain.vacation.service.VacationHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class VacationScheduler {

    private final VacationHistoryService vacationHistoryService;
    private final UserService userService;

    private static final int SUMMARY_START_TIME = 9; // from 09:00 ~

    @Scheduled(fixedRateString = "${scheduler.vacation.fixedRate: 300000}")
    public void scheduleFixedRateTaskAsync() {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.HOUR_OF_DAY) == SUMMARY_START_TIME) {
            // this time is 09:00:00~09:59:00
            List<VacationHistory> vacationHistoryList = vacationHistoryService.getVacationHistoryBySendDateToday();
            if (vacationHistoryList != null) {
                for (VacationHistory vh : vacationHistoryList) {
                    if (vh.getStatus() != null && vh.getStatus().equalsIgnoreCase(VacationHistory.STATUS_READY)) {
                        //need to send email
                        log.info("vacation ticket toDayString:{} size:{}", now, vacationHistoryList.size());
                        boolean sendSuccess = sendEmail(vh);
                        if (sendSuccess) {
                            vh.setStatus(VacationHistory.STATUS_DONE);
                            vacationHistoryService.update(vh);
                        }
                    }
                }
            }
        }
    }

    private boolean sendEmail(VacationHistory vh) {
        String sendUserName = vh.getUserId();
        Optional<User> userTemp = userService.findById(sendUserName);
        User user = null;
        if (userTemp.isPresent() == false) {
            log.error("get user info fail [sendUserName={}]", sendUserName);
            return false;
        } else {
            user = userTemp.get();
        }
        String title = "[휴가 일정 공유] " + user.getRealName() + "님은 오늘 휴가입니다.";

        String dateTimeFormat = "yyyy-MM-dd";
        SimpleDateFormat transFormat = new SimpleDateFormat(dateTimeFormat);
        String todayString = transFormat.format(vh.getSendDate());
        StringBuilder builder = new StringBuilder();
        builder.append("<font style=\"font-family: 맑은 고딕; font-size:10pt\">안녕하세요.<br>");
        builder.append(user.getTeamName()).append(" 에이전시 [").append(user.getRealName()).append(" ").append("님] 휴가 일정 공유 메일입니다.").append("<br><br>");
        builder.append("<br>").append(user.getRealName()).append(" ").append("님은 오늘(").append(todayString).append(")").append("휴가입니다. 자세한 내용은 아래를 참고 바랍니다.");
        builder.append("<br>----<br>");
        builder.append(vh.getMemo().replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;"));
        builder.append("<br>----<br>");
        builder.append("<br><br>이 메일은 IMS(Issue Management System)에서 자동으로 발송한 메일입니다.(http://teatime.castis.net/)</font>");

        boolean sessionDebug = false;
        Properties props = System.getProperties();
        props.put("mail.host", "mail.castis.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.transport.protocol", "smtp");
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(sessionDebug);
        String vacationEmails = user.getVacationReportList();
        String[] emailList = vacationEmails.split(",");
        for (String mailto : emailList) {
            try {
                // Multipart
                Multipart multipart = new MimeMultipart();
                BodyPart messageBodyPart = new MimeBodyPart();
                // Now set the actual message
                messageBodyPart.setContent(builder.toString(), "text/html; charset=utf-8");
                multipart.addBodyPart(messageBodyPart);
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(user.getEmail()));
                InternetAddress[] address = InternetAddress.parse(mailto, false);
                msg.setRecipients(Message.RecipientType.TO, address);
                msg.setRecipient(Message.RecipientType.CC, new InternetAddress(user.getEmail()));
                msg.setSubject(title);
                msg.setSentDate(new Date());
                msg.setContent(multipart);

                Transport.send(msg);
                log.info("send mail {} to {} success", sendUserName, mailto);
                //100ms sleep
                Thread.sleep(100);
            } catch (MessagingException | InterruptedException mex) {
                log.error("{}", mex.getMessage());
                mex.printStackTrace();
            }
        }
        return true;
    }
}
