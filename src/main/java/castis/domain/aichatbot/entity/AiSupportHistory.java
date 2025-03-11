package castis.domain.aichatbot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "ai_support_history")
@Entity
@NoArgsConstructor
public class AiSupportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderId;

    @Column(columnDefinition = "requestText")
    private String requestText;

    @Column(columnDefinition = "respobseText")
    private String responseText;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
