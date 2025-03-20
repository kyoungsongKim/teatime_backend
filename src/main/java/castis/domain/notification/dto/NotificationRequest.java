package castis.domain.notification.dto;

import lombok.Data;

import java.util.List;

@Data
public class NotificationRequest {
    private Long id;
    private String createUserId;
    private String title;
    private String content;
    private Boolean isGlobal;
    private String notificationType;
    private List<String> userIds;
}
