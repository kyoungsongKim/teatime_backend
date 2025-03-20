package castis.domain.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String title;
    private String content;
    private Boolean isGlobal;
    private String notificationType;
    private LocalDateTime createdAt;

    public NotificationResponse(Long id, String title, String content, Boolean isGlobal, String notificationType, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isGlobal = isGlobal;
        this.notificationType = notificationType;
        this.createdAt = createdAt;
    }
}
