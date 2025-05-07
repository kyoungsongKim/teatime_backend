package castis.scheduler;

import castis.domain.notification.dto.NotificationRequest;
import castis.domain.notification.service.NotificationService;
import castis.domain.point.entity.PointHistory;
import castis.domain.point.service.PointService;
import castis.domain.user.entity.User;
import castis.domain.user.entity.UserDetails;
import castis.domain.user.service.UserService;
import castis.domain.usersmsinfo.entity.UserSMSInfoService;
import castis.domain.usersmsinfo.entity.UserSmsInfo;
import castis.scheduler.sms.SMSHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
@Slf4j
@RequiredArgsConstructor
public class NoticeScheduler {
    final DefaultMessageService messageService = NurigoApp.INSTANCE.initialize("NCSWVSZSVEPHMDYP", "PVI4TYAMCT3GPPQDKRARO7O7Z3MBT9L3", "https://api.coolsms.co.kr");
    private final UserService userService;
    private final PointService pointService;
    private final SMSHistoryService smsHistoryService;
    private final UserSMSInfoService userSMSInfoService;
    private final NotificationService notificationService;

    @Value("${scheduler.notice.enable:true}")
    private boolean schedulerNoticeEnable;

    @Value("${scheduler.notice.smsNumber:01071649777}")
    private String smsNumber;

    @Value("${scheduler.notice.recvId:kskim}")
    private String recvId;

    // mon - fri, 13:00
    @Scheduled(cron = "${scheduler.notice.notice-send-cron:0 00 13 * * MON,WED,FRI}")
    public void findLastReflectMeetTime() {
        List<User> userList = userService.getUserList();
        for (User user: userList) {
            if (user.getTeamName().equalsIgnoreCase("saram")) {
                PointHistory ph = pointService.getLastReceivePointHistory(user.getId());
                if ( ph == null ) {
                    //first time
                    log.debug("[findLastReflectMeetTime] ph == null");
                } else {
                    UserDetails userDetails = user.getUserDetails();
                    if (userDetails != null){
                        //not first time
                        if ( userDetails.getCellphone() != null && !userDetails.getCellphone().isEmpty()) {
                            if (userDetails.getCbankId().equalsIgnoreCase("teatime.coffee")) {
                                long intervalDateCount = DAYS.between(ph.getCreateDate(), LocalDateTime.now());
                                UserSmsInfo usi = userSMSInfoService.getUSerSMSInfo(user.getId());
                                if (usi != null && usi.isSendmsg() && usi.getSendday() > 0 && intervalDateCount > usi.getSendday()) {
                                    String dateStr = ph.getCreateDate().toString().substring(0, ph.getCreateDate().toString().indexOf("T"));
                                    if (dateStr.indexOf("T") > 0) {
                                        dateStr = dateStr.substring(0, ph.getCreateDate().toString().indexOf("T"));
                                    }
                                    StringBuffer sb = new StringBuffer();
                                    sb.append(user.getRealName()).append(" 고객님!").append("\n");
                                    sb.append("가장 최근 서비스 이용일은 [").append(dateStr).append("]로 ");
                                    sb.append(intervalDateCount).append("일이 지났습니다!\n");
                                    if(schedulerNoticeEnable) {
                                        if (userDetails.getCbankId().equalsIgnoreCase("teatime.coffee")) {
                                            NotificationRequest request = new NotificationRequest();
                                            List<String> userIds = new java.util.ArrayList<>(Collections.emptyList());
                                            userIds.add(recvId);
                                            request.setTitle("[사람] 에이전트 서비스 이용 안내");
                                            request.setCreateUserId(recvId);
                                            request.setUserIds(userIds);
                                            request.setIsGlobal(false);
                                            request.setNotificationType("None");
                                            request.setContent(sb.toString());
                                            notificationService.createNotification(request, null);
                                        }
                                    } else {
                                        log.info("[findLastReflectMeetTime] schedulerNoticeEnable is false");
                                    }
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
        if (!response.getStatusCode().equalsIgnoreCase("2000")) {
            log.error("sendSMS response:[{}]", response);
        }
        smsHistoryService.saveSMSHistory(fromWho,toWho, body, response.getStatusCode());

    }
}
