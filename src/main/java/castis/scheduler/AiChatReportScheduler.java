package castis.scheduler;


import castis.domain.aichatbot.entity.AiSupportHistory;
import castis.domain.aichatbot.service.AiSupportService;
import castis.domain.user.dto.UserDto;
import castis.domain.user.entity.User;
import castis.domain.user.entity.UserDetails;
import castis.domain.user.service.UserService;
import castis.domain.vacation.dto.VacationHistoryDto;
import castis.util.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AiChatReportScheduler {
    private final UserService userService;
    private final AiSupportService aiSupportService;
    private final EmailService emailService;

    @Value("${scheduler.notice.recvId:kskim}")
    private String recvId;

    @Scheduled(cron = "${scheduler.notice.ai-report-cron:0 0 10 * * *}")
    public void sendYesterdayAiChatReport() {
        User user = userService.findById(recvId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));
        String toEmail = user.getUserDetails().getEmail();

        List<AiSupportHistory> histories = aiSupportService.getHistoriesFromYesterday();

        if (histories.isEmpty()) {
            log.info("[AiChatReportScheduler] 어제 AI 대화 기록 없음");
            return;
        }

        String title = "[AI 챗봇 리포트] 어제의 상담 기록 (" + LocalDate.now().minusDays(1) + ")";
        StringBuilder body = new StringBuilder();

        body.append("<html><head>")
                .append("<style>")
                .append("table { border-collapse: collapse; width: 100%; }")
                .append("th, td { border: 1px solid #ccc; padding: 8px; text-align: left; vertical-align: top; }")
                .append("pre { white-space: pre-wrap; font-family: 맑은 고딕; font-size: 10pt; margin: 0; }")
                .append("</style>")
                .append("</head><body>");

        body.append("<h3>AI 챗봇 어제의 기록</h3>");

        body.append("<table>");
        body.append("<thead><tr style='background-color:#f0f0f0;'>")
                .append("<th>작성자</th>")
                .append("<th>시간</th>")
                .append("<th>질문</th>")
                .append("<th>응답</th>")
                .append("</tr></thead>");
        body.append("<tbody>");

        for (AiSupportHistory h : histories) {
            body.append("<tr>")
                    .append("<td>").append(h.getSenderId()).append("</td>")
                    .append("<td>").append(h.getCreatedAt()).append("</td>")
                    .append("<td><pre>").append(escapeHtml(h.getRequestText())).append("</pre></td>")
                    .append("<td><pre>").append(escapeHtml(h.getResponseText())).append("</pre></td>")
                    .append("</tr>");
        }

        body.append("</tbody></table>");

        body.append("</ul>");
        body.append("<p style='color:gray;'>이 메일은 IMS(Issue Management System)에서 자동으로 발송한 메일입니다.(https://saram.serviz.net/)</p>");
        body.append("</body></html>");

        try {
            InternetAddress from = new InternetAddress("kskim@castis.com",
                    MimeUtility.encodeText("사람사업부 전산시스템", "UTF-8", "B"));
            InternetAddress to = new InternetAddress(toEmail);

            emailService.sendEmail(title, body.toString(), from, new InternetAddress[]{to}, from);
            log.info("AI 챗봇 리포트 메일 전송 성공 → {}", toEmail);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("AI 챗봇 리포트 전송 실패: {}", e.getMessage(), e);
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
