package castis.scheduler;

import castis.domain.point.entity.PointHistory;
import castis.domain.point.service.PointService;
import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import castis.scheduler.sms.SMSHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.model.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
@Slf4j
@RequiredArgsConstructor
public class NoticeScheduler {
    final DefaultMessageService messageService = NurigoApp.INSTANCE.initialize("NCSWVSZSVEPHMDYP", "PVI4TYAMCT3GPPQDKRARO7O7Z3MBT9L3", "https://api.coolsms.co.kr");
    private final UserService userService;
    private final PointService pointService;
    private final SMSHistoryService smsHistoryService;

    // mon - fri, 13:00
    @Scheduled(cron = "0 00 13 * * MON-FRI")
    public void findLastReflectMeetTime() {
        List<User> userList = userService.getUserList();
        for ( User user: userList) {
            if ( user.getTeamName().equalsIgnoreCase("Teatime")) {
                PointHistory ph = pointService.getLastReceivePointHistory(user.getId());
                if ( ph == null ) {
                    //first time
                    log.debug("[findLastReflectMeetTime] ph == null");
                } else {
                    //not first time
                    if ( user.getCellphone() != null && user.getCellphone().isEmpty() == false ) {
                        if(user.getCbankId().equalsIgnoreCase("teatime.coffee")) {
                            long intervalDateCount = DAYS.between(ph.getCreateDate(), LocalDateTime.now());
                            if (intervalDateCount > user.getEnabled()) {
                                String dateStr = ph.getCreateDate().toString().substring(0, ph.getCreateDate().toString().indexOf("T"));
                                if (dateStr.indexOf("T") > 0) {
                                    dateStr = dateStr.substring(0, ph.getCreateDate().toString().indexOf("T"));
                                }
                                StringBuffer sb = new StringBuffer();
                                sb.append("안녕하세요. ").append(user.getRealName()).append("님!").append("\n");
                                if (user.getCbankId().equalsIgnoreCase("teatime.coffee")) {
                                    sb.append("퇴근후티타임 소속 에이전트 김경송입니다.");
                                }
                                sb.append("마지막 서비스 이용일은 [").append(dateStr).append("]로 ");
                                sb.append(intervalDateCount).append("일이 지났습니다!\n");
                                sb.append("많이 바쁘시겠지만 에이전트 서비스 이용 홍보차 연락드렸습니다. 많은 이용 부탁드리겠습니다.\n 감사합니다!\n");
                                if (user.getCbankId().equalsIgnoreCase("teatime.coffee")) {
                                    sb.append("연락처:010-7164-9777\n");
                                    sb.append("서비스예약:https://coffee.castis.net\n");
                                }
                                sb.append("(본 문자의 수신 거부를 희망하실 경우 회신 주시면 바로 반영하도록 하겠습니다!)");
                                if (user.getCbankId().equalsIgnoreCase("teatime.coffee")) {
                                    sendSMS("01071649777", user.getCellphone(), sb.toString());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void sendSMS(String fromWho, String toWho, String body) {
        Message message = new Message();
        message.setFrom(fromWho.replace("-","").trim());
        message.setTo(toWho.replace("-","").trim());
        message.setText(body);
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        if ( response.getStatusCode().equalsIgnoreCase("2000") == false) {
            log.error("sendSMS response:[{}]", response);
        }
        smsHistoryService.saveSMSHistory(fromWho,toWho, body, response.getStatusCode());

    }
}
