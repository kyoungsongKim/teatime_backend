package castis.scheduler;

import castis.domain.model.Users;
import castis.domain.model.VacationHistory;
import castis.domain.user.UserDao;
import castis.domain.vacation.VacationHistoryDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
@Slf4j
@RequiredArgsConstructor
public class VacationScheduler {

    private VacationHistoryDao vacationHistoryDao;
    private UserDao userDao;

    private static final int SUMMARY_START_TIME = 9; // from 09:00 ~

    @Scheduled(fixedRateString = "${scheduler.vacation.fixedRate: 30000}")
    public void scheduleFixedRateTaskAsync() {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.HOUR_OF_DAY) == SUMMARY_START_TIME) {
            // this time is 09:00:00~09:59:00
            String toDayString = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH);
            String dateTimeFormat = "yyyy-MM-dd";
            Date peroid;
            try {
                peroid = new SimpleDateFormat(dateTimeFormat).parse(toDayString);
                List<VacationHistory> vacationHistoryList = vacationHistoryDao.getVacationHistoryBySendDate(peroid);
                if (vacationHistoryList != null) {
                    for (VacationHistory vh : vacationHistoryList) {
                        if (vh.getStatus() != null && vh.getStatus().equalsIgnoreCase(VacationHistory.STATUS_READY)) {
                            //need to send email
                            log.info("vacation ticket toDayString:{} size:{}", toDayString, vacationHistoryList.size());
                            boolean sendSuccess = sendEmail(vh);
                            if (sendSuccess) {
                                vh.setStatus(VacationHistory.STATUS_DONE);
                                vacationHistoryDao.update(vh);
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean sendEmail(VacationHistory vh) {
        String sendUserName = vh.getUserId();
        Users user = userDao.get(sendUserName);
        if (user == null) {
            log.error("get user info fail [sendUserName={}]", sendUserName);
            return false;
        }
        String title = "[휴가 일정 공유] " + user.getUsername() + "님은 오늘 휴가입니다.";

        String dateTimeFormat = "yyyy-MM-dd";
        SimpleDateFormat transFormat = new SimpleDateFormat(dateTimeFormat);
        String todayString = transFormat.format(vh.getSenddate());
        StringBuilder builder = new StringBuilder();
        builder.append("<font style=\"font-family: 맑은 고딕; font-size:10pt\">안녕하세요.<br>");
        builder.append(user.getTeamname()).append(" 에이전시 [").append(user.getRealname()).append(" ").append("님] 휴가 일정 공유 메일입니다.").append("<br><br>");
        builder.append("<br>").append(user.getRealname()).append(" ").append("님은 오늘(").append(todayString).append(")").append("휴가입니다. 자세한 내용은 아래를 참고 바랍니다.");
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
