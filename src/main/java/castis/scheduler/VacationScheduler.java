package castis.scheduler;

import castis.domain.user.dto.UserDto;
import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import castis.domain.vacation.dto.VacationHistoryDto;
import castis.domain.vacation.service.VacationHistoryService;
import castis.util.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class VacationScheduler {

    private final VacationHistoryService vacationHistoryService;
    private final UserService userService;
    private final EmailService emailService;

    private static final int SUMMARY_START_TIME = 9; // from 09:00 ~

    @Scheduled(cron = "${scheduler.vacation.email-send-cron}")
    public void scheduleFixedRateTaskAsync() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(SUMMARY_START_TIME, 0));
        LocalDateTime endTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(23, 59, 59));
        List<VacationHistoryDto> vacationHistoryList = vacationHistoryService.getVacationHistoryList(startTime,
                endTime, false);
        vacationHistoryList.forEach(vacationHistory -> {
            log.info("vacation ticket toDayString:{} size:{}", now, vacationHistoryList.size());
            sendEmail(vacationHistory);
        });

    }

    private boolean sendEmail(VacationHistoryDto vacationHistory) {
        String sendUserName = vacationHistory.getUserId();
        Optional<User> userTemp = userService.findById(sendUserName);
        User user = null;
        if (userTemp.isPresent() == false) {
            log.error("get user info fail [sendUserName={}]", sendUserName);
            return false;
        } else {
            user = userTemp.get();
        }
        String title = "[휴가 일정 공유] " + user.getRealName() + "님은 오늘 휴가입니다.";

        String dateTimeFormat = "yyyy년 MM월 dd일 HH시";
        DateTimeFormatter formater = DateTimeFormatter.ofPattern(dateTimeFormat);
        String formattedEventStartDateString = vacationHistory.getEventStartDate().format(formater);
        String formattedEventEndDateString = vacationHistory.getEventEndDate().format(formater);

        StringBuilder builder = new StringBuilder();
        builder.append("<font style=\"font-family: 맑은 고딕; font-size:10pt\">안녕하세요.<br>")
                .append(user.getTeamName()).append(" 에이전시 [")
                .append(user.getRealName()).append(" 님] 휴가 일정 공유 메일입니다.<br>")
                .append("<br>")
                .append("<br>")
                .append(user.getRealName()).append(" 님은 금일 ").append(formattedEventStartDateString).append("부터<br>")
                .append(formattedEventEndDateString).append("까지").append("휴가입니다.<br>")
                .append("자세한 내용은 아래를 참고 바랍니다.<br>")
                .append("----<br>")
                .append(vacationHistory.getReason().replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;"))
                .append("<br>----<br>")
                .append(
                        "<br><br>이 메일은 IMS(Issue Management System)에서 자동으로 발송한 메일입니다.(http://teatime.castis.net/)</font>");

        String vacationEmails = user.getVacationReportList();
        List<UserDto> admins = userService.getAdminList();
        String adminEmails = admins.stream().map(admin -> admin.getEmail())
                .reduce((emails, admin) -> emails + "," + admin).get();
        vacationEmails = vacationEmails.length() == 0 ? adminEmails : vacationEmails + "," + adminEmails;
        try {
            InternetAddress from = new InternetAddress("kskim@castis.com",
                    MimeUtility.encodeText("사람사업부 전산시스템", "UTF-8", "B"));
            InternetAddress[] addresses = InternetAddress.parse(vacationEmails);
            emailService.sendEmail(title, builder.toString(), from, addresses,
                    new InternetAddress(user.getEmail()));
            log.info("send mail {} to {} success", sendUserName, vacationEmails);
        } catch (MessagingException | UnsupportedEncodingException mex) {
            log.error("{}", mex.getMessage());
            mex.printStackTrace();
        }
        return true;
    }
}
