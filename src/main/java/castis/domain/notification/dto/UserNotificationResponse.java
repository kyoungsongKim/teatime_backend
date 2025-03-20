package castis.domain.notification.dto;

import castis.domain.notification.entity.Notification;
import castis.domain.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserNotificationResponse {
    private Long id;
    private Notification notification;
    private User user;
    private Boolean isRead;
    private String reply;
    private String avatarImg;
    private LocalDateTime createdAt;

    public UserNotificationResponse(Long id, Notification notification, User user, Boolean isRead, String reply, String avatarImg, LocalDateTime createdAt) {
        this.id = id;
        this.notification = notification;
        this.user = user;
        this.isRead = isRead;
        this.reply = reply;
        this.avatarImg = avatarImg;
        this.createdAt = createdAt;
    }
}
