package castis.scheduler;

import castis.domain.point.entity.PointHistory;
import castis.domain.point.service.PointService;
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
public class NoticeScheduler {

    private final VacationHistoryService vacationHistoryService;
    private final UserService userService;

    private final PointService pointService;

    // after 1 minute, do 1 minute
    @Scheduled(fixedRate=10000, initialDelay = 10000)
    public void findLastReflectMeetTime() {
        List<User> userList = userService.getUserList();
        for ( User user: userList) {
            if ( user.getTeamName().equalsIgnoreCase("Teatime")) {
                log.info("[findLastReflectMeetTime] user.ID[{}], name[{}], teamName[{}]", user.getId(), user.getUserName(), user.getTeamName());
                PointHistory ph = pointService.getLastReceivePointHistory(user.getId());
                if ( ph == null ) {
                    log.info("[findLastReflectMeetTime] ph == null");
                } else {
                    log.info("[findLastReflectMeetTime] ph.date[{}], ph.code[{}]", ph.getCreateDate(), ph.getCode());
                }
            }
        }
    }
}
