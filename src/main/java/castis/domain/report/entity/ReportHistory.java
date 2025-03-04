package castis.domain.report.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "report_history")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class ReportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 자동 증가 ID

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId; // 보고 작성자 ID

    @Column(name = "recv_email", nullable = false, length = 500)
    private String recvEmail; // 보고를 받는 이메일

    @Column(nullable = false, length = 200)
    private String title; // 보고 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents; // 보고 내용

    @Column(name = "is_success", nullable = false, length = 10)
    private String isSuccess; // 결과

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 보고 생성 시간

    public ReportHistory(String userId, String recvEmail, String title, String contents, String isSuccess) {
        this.userId = userId;
        this.recvEmail = recvEmail;
        this.title = title;
        this.contents = contents;
        this.isSuccess = isSuccess;
        this.createdAt = LocalDateTime.now();
    }
}