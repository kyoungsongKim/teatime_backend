
package castis.domain.assistance.controller;

import castis.domain.user.entity.UserDetails;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import castis.domain.assistance.constant.AppliedAssistanceStatus;
import castis.domain.assistance.dto.AssistanceDto;
import castis.domain.assistance.dto.CreateAssistanceSuggestionBody;
import castis.domain.assistance.entity.Assistance;
import castis.domain.assistance.entity.AssistanceApply;
import castis.domain.assistance.entity.AssistanceSuggestion;
import castis.domain.assistance.service.AssistanceApplyService;
import castis.domain.assistance.service.AssistanceService;
import castis.domain.assistance.service.AssistanceSuggestionService;
import castis.domain.filesystem.service.FileSystemService;
import castis.domain.security.jwt.AuthProvider;
import castis.domain.user.CustomUserDetails;
import castis.domain.user.dto.UserDto;
import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import castis.util.email.EmailService;
import castis.util.kakao.KakaoMessagingService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
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

    private final FileSystemService fsService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<AssistanceDto>> getAssistanceList(HttpServletRequest request) {
        List<AssistanceDto> result = assistanceService.getAssistanceList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/suggestion", method = RequestMethod.POST)
    public ResponseEntity assistanceSuggestion(HttpServletRequest request,
            @RequestBody CreateAssistanceSuggestionBody body) throws UnsupportedEncodingException {
        String token = request.getHeader("Authorization");
        CustomUserDetails user = (CustomUserDetails) authProvider.getAuthentication(token).getPrincipal();
        AssistanceSuggestion suggestion = new AssistanceSuggestion();
        suggestion.setContent(body.getContent());
        suggestion.setSuggester(userService.getUser(user.getUserId()));
        assistanceSuggestionService.createAssistanceSuggestion(suggestion);
        List<UserDto> admins = userService.getAdminList();

        InternetAddress from = new InternetAddress("kskim@castis.com",
                MimeUtility.encodeText("사람사업부 전산시스템", "UTF-8", "B"));

        String content = "<font style=\"font-family: 맑은 고딕; font-size:10pt\">" + suggestion.getContent() +
                "<br><br>이 메일은 IMS(Issue Management System)에서 자동으로 발송한 메일입니다.<br>차 한잔의 여유가 세상을 바꿉니다.(http://teatime.castis.net/)</font>";

        admins.forEach(admin -> {
            try {
                emailService.sendEmail("[이런 서비스도 만들어주세요!]", content, from, InternetAddress.parse(admin.getEmail()));
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
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart String content, @PathVariable Integer assistanceId)
            throws IllegalStateException, IOException {
        String token = request.getHeader("Authorization");
        CustomUserDetails user = (CustomUserDetails) authProvider.getAuthentication(token).getPrincipal();

        Assistance targetAssistance = assistanceService.getAssistance(assistanceId);
        User applier = userService.getUser(user.getUserId());
        AssistanceApply assistanceApply = new AssistanceApply();
        assistanceApply.setApplier(applier);
        assistanceApply.setContent(content);
        if (files != null) {
            assistanceApply
                    .setFiles(files.stream().map(file -> {
                        try {
                            return fsService.saveFile(file);
                        } catch (IllegalStateException | IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }).collect(Collectors.toList()));
        }
        assistanceApply.setStatus(AppliedAssistanceStatus.WAITING);
        assistanceApply.setAssistance(targetAssistance);
        assistanceApplyService.applyAssistance(assistanceApply);

        User messageTargetAdmin = targetAssistance.getMessageTargetAdmin();

        UserDetails userDetails = messageTargetAdmin.getUserDetails();
        if (userDetails != null &&  userDetails.getCellphone() != null && !userDetails.getCellphone().isEmpty()) {
            kakaoMessagingService.sendAssistanceApplyMessage(userDetails.getCellphone(),
                    applier.getRealName(), targetAssistance.getName());
        }
        List<UserDto> admins = userService.getAdminList();

        InternetAddress from = new InternetAddress("kskim@castis.com",
                MimeUtility.encodeText("사람사업부 전산시스템", "UTF-8", "B"));

        String mailContent = "<font style=\"font-family: 맑은 고딕; font-size:10pt\">(" + user.getUsername() + ")님께서<br>("
                + targetAssistance.getName() + ")를 신청하셨습니다." +
                "<br><br>이 메일은 IMS(Issue Management System)에서 자동으로 발송한 메일입니다.<br>차 한잔의 여유가 세상을 바꿉니다.(http://teatime.castis.net/)</font>";
        admins.forEach(admin -> {
            try {
                emailService.sendEmail("[비서서비스 요청알림]", mailContent, from, InternetAddress.parse(admin.getEmail()));
            } catch (UnsupportedEncodingException | MessagingException e) {
                log.error("{}", e.getMessage());
                e.printStackTrace();
            }
        });

        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

}
