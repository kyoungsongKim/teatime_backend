package castis.scheduler;

import castis.domain.agreement.dto.AgreementDto;
import castis.domain.agreement.service.AgreementService;
import castis.domain.agreement.util.AgreementType;
import castis.domain.notification.dto.NotificationRequest;
import castis.domain.notification.service.NotificationService;
import castis.domain.point.entity.PointHistory;
import castis.domain.point.service.PointService;
import castis.domain.sign.service.SignService;
import castis.domain.team.dto.TeamDto;
import castis.domain.team.service.TeamService;
import castis.domain.user.dto.UserDto;
import castis.domain.user.entity.User;
import castis.domain.user.entity.UserDetails;
import castis.domain.user.service.UserService;
import castis.domain.usersmsinfo.entity.UserSMSInfoService;
import castis.domain.usersmsinfo.entity.UserSmsInfo;
import castis.enums.UserRole;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final AgreementService agreementService;
    private final TeamService teamService;
    private final SignService signService;

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

    @Scheduled(cron = "${scheduler.notice.guarantee-expire-notice-cron:0 0 10 * * *}")
    public void notifyGuaranteeContractExpiringSoon() {
        List<TeamDto> teamDtoList = teamService.findAllTeam();

        for (TeamDto team : teamDtoList) {
            String teamName = team.getTeamName();
            List<UserDto> teamUsers = userService.getUserTeamDtoList(teamName);

            // 해당 팀의 관리자 이상 사용자 필터링
            List<UserDto> teamAdmins = teamUsers.stream()
                    .filter(user -> isAdminOrHigher(signService.checkIsAdmin(user.getId())))
                    .collect(Collectors.toList());

            if (teamAdmins.isEmpty()) {
                log.info("[보장 계약 만료 알림] 팀 '{}'에 관리자 이상 유저 없음. 스킵", teamName);
                continue;
            }

            for (UserDto member : teamUsers) {
                List<AgreementDto> agreements = agreementService.getAgreementListByUser(member.getId());

                for (AgreementDto agreement : agreements) {
                    if (!agreement.getType().equals(AgreementType.GUARANTEE)) continue;

                    LocalDate endDate = agreement.getEndDate();
                    long daysUntilEnd = DAYS.between(LocalDate.now(), endDate);

                    if (daysUntilEnd <= 30) {
                        String statusMessage;

                        if (daysUntilEnd < 0) {
                            statusMessage = member.getRealName() + " 고객님의 보장 계약이 "
                                    + endDate + "에\n**이미 만료되었습니다**.\n"
                                    + "보장 계약을 만료 처리 하세요.";
                        } else if (daysUntilEnd == 0) {
                            statusMessage = member.getRealName() + " 고객님의 보장 계약이\n**오늘(" + endDate + ") 만료됩니다**.\n"
                                    + "계약 연장을 원하시면 확인해 신청해 주세요.";
                        } else {
                            statusMessage = member.getRealName() + " 고객님의 보장 계약이 "
                                    + endDate + "에 만료됩니다.\n"
                                    + "(D-" + daysUntilEnd + ") 계약 연장을 원하시면 미리 확인해 주세요.";
                        }

                        if (schedulerNoticeEnable) {
                            NotificationRequest request = new NotificationRequest();
                            request.setTitle("[보장 계약 만료 알림]");
                            request.setContent(statusMessage);
                            request.setCreateUserId(recvId);
                            request.setIsGlobal(false);
                            request.setNotificationType("None");

                            List<String> adminIds = teamAdmins.stream()
                                    .map(UserDto::getId)
                                    .collect(Collectors.toList());

                            request.setUserIds(adminIds);

                            notificationService.createNotification(request, null);
                            log.info("[보장 계약 만료 알림] 팀 '{}', 대상자 '{}', 관리자에게 알림 전송 완료", teamName, member.getRealName());
                        } else {
                            log.info("[보장 계약 만료 알림] schedulerNoticeEnable == false, 알림 미전송");
                        }
                    }
                }
            }
        }
    }

    private boolean isAdminOrHigher(UserRole role) {
        return role == UserRole.ROLE_ADMIN || role == UserRole.ROLE_SUPER_ADMIN;
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
