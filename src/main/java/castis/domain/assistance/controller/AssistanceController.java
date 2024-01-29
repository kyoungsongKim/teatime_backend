
package castis.domain.assistance.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import castis.domain.assistance.constant.AppliedAssistanceStatus;
import castis.domain.assistance.dto.ApplyAssistanceBody;
import castis.domain.assistance.dto.AssistanceApplyDto;
import castis.domain.assistance.dto.AssistanceDto;
import castis.domain.assistance.dto.CreateAssistanceReviewBody;
import castis.domain.assistance.dto.CreateAssistanceSuggestionBody;
import castis.domain.assistance.dto.UpdateAssistanceApplyStatusBody;
import castis.domain.assistance.entity.Assistance;
import castis.domain.assistance.entity.AssistanceApply;
import castis.domain.assistance.entity.AssistanceReview;
import castis.domain.assistance.entity.AssistanceSuggestion;
import castis.domain.assistance.service.AssistanceApplyService;
import castis.domain.assistance.service.AssistanceService;
import castis.domain.assistance.service.AssistanceSuggestionService;
import castis.domain.security.jwt.AuthProvider;
import castis.domain.user.CustomUserDetails;
import castis.domain.user.dto.UserDto;
import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import castis.enums.UserRole;
import castis.util.email.EmailSender;
import castis.util.email.EmailService;
import castis.util.kakao.KakaoMessagingService;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/assistance")
public class AssistanceController {
    private final UserService userService;
    private final AssistanceService assistanceService;
    private final AssistanceApplyService assistanceApplyService;
    private final AssistanceSuggestionService assistanceSuggestionService;

    private final KakaoMessagingService kakaoMessagingService;
    private final EmailService emailService;
    private final AuthProvider authProvider;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<AssistanceDto>> getAssistanceList(HttpServletRequest request) {
        List<AssistanceDto> result = assistanceService.getAssistanceList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/suggestion", method = RequestMethod.POST)
    public ResponseEntity assistanceSuggestion(HttpServletRequest request,
            @RequestBody CreateAssistanceSuggestionBody body) {
        String token = request.getHeader("Authorization");
        CustomUserDetails user = (CustomUserDetails) authProvider.getAuthentication(token).getPrincipal();
        AssistanceSuggestion suggestion = new AssistanceSuggestion();
        suggestion.setContent(body.getContent());
        suggestion.setSuggester(userService.getUser(user.getUserId()));
        assistanceSuggestionService.createAssistanceSuggestion(suggestion);
        List<UserDto> admins = userService.getAdminList();

        EmailSender from = new EmailSender("kskim@castis.com", "사람사업부 전산시스템");

        StringBuilder builder = new StringBuilder();
        builder.append("<font style=\"font-family: 맑은 고딕; font-size:10pt\">" + suggestion.getContent());
        builder.append(
                "<br><br>이 메일은 IMS(Issue Management System)에서 자동으로 발송한 메일입니다.<br>차 한잔의 여유가 세상을 바꿉니다.(http://teatime.castis.net/)</font>");
        String content = builder.toString();

        admins.forEach(admin -> {
            try {
                emailService.sendEmail("[이런 서비스도 만들어주세요!]", content, from, admin.getEmail());
            } catch (UnsupportedEncodingException | MessagingException e) {
                log.error("{}", e.getMessage());
                e.printStackTrace();
            }
        });

        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{assistanceId}", method = RequestMethod.GET)
    public ResponseEntity<AssistanceDto> getAssistance(HttpServletRequest request,
            @PathVariable Integer assistanceId) {
        AssistanceDto result = new AssistanceDto(assistanceService.getAssistance(assistanceId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/{assistanceId}/apply")
    @Transactional
    public ResponseEntity applyAssistance(HttpServletRequest request,
            @RequestBody ApplyAssistanceBody applyAssistanceBody, @PathVariable Integer assistanceId) {
        String token = request.getHeader("Authorization");
        CustomUserDetails user = (CustomUserDetails) authProvider.getAuthentication(token).getPrincipal();

        Assistance targetAssistance = assistanceService.getAssistance(assistanceId);

        AssistanceApply assistanceApply = new AssistanceApply();
        assistanceApply.setApplier(userService.getUser(user.getUserId()));
        assistanceApply.setContent(applyAssistanceBody.getContent());
        assistanceApply.setStatus(AppliedAssistanceStatus.WAITING);
        assistanceApply.setAssistance(targetAssistance);
        assistanceApplyService.applyAssistance(assistanceApply);

        User messageTargetAdmin = targetAssistance.getMessageTargetAdmin();
        if (messageTargetAdmin.getCellphone() != null && messageTargetAdmin.getCellphone().length() > 0) {
            kakaoMessagingService.sendAssistanceApplyMessage(messageTargetAdmin.getCellphone(),
                    messageTargetAdmin.getUserName(), targetAssistance.getName());
        }
        List<UserDto> admins = userService.getAdminList();

        EmailSender from = new EmailSender("kskim@castis.com", "사람사업부 전산시스템");

        StringBuilder builder = new StringBuilder();
        builder.append("<font style=\"font-family: 맑은 고딕; font-size:10pt\">(" + user.getUsername() + ")님께서<br>("
                + targetAssistance.getName() + ")를 신청하셨습니다.");
        builder.append(
                "<br><br>이 메일은 IMS(Issue Management System)에서 자동으로 발송한 메일입니다.<br>차 한잔의 여유가 세상을 바꿉니다.(http://teatime.castis.net/)</font>");

        String content = builder.toString();

        admins.forEach(admin -> {
            try {
                emailService.sendEmail("[비서서비스 요청알림]", content, from, admin.getEmail());
            } catch (UnsupportedEncodingException | MessagingException e) {
                log.error("{}", e.getMessage());
                e.printStackTrace();
            }
        });

        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

}
