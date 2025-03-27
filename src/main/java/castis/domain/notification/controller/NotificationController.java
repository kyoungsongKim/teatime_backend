package castis.domain.notification.controller;

import castis.domain.agreement.entity.IUserAgreementInfo;
import castis.domain.filesystem.service.FileSystemService;
import castis.domain.notification.dto.NotificationRequest;
import castis.domain.notification.dto.NotificationResponse;
import castis.domain.notification.dto.UserNotificationResponse;
import castis.domain.notification.service.NotificationService;
import castis.domain.security.jwt.AuthProvider;
import castis.domain.user.CustomUserDetails;
import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final AuthProvider authProvider;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> createNotification(HttpServletRequest httpServletRequest, @RequestBody NotificationRequest request) {
        boolean isAdmin = authProvider.isAdmin(httpServletRequest);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        CustomUserDetails userDetails = authProvider.getUserDetails(httpServletRequest);
        if (userDetails.getRoles().contains("ROLE_ADMIN")) {
            User user = userService.getUser(userDetails.getUserId());
            notificationService.createNotification(request, user.getTeamName());
        } else if (userDetails.getRoles().contains("ROLE_SUPER_ADMIN")) {
            notificationService.createNotification(request, null);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        List<NotificationResponse> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<UserNotificationResponse>> getUserNotifications(@PathVariable Long id) {
        List<UserNotificationResponse> notifications = notificationService.getUserNotifications(id);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserNotificationResponse>> getAllNotificationsByUser(@PathVariable String userId) {
        List<UserNotificationResponse> notifications = notificationService.getAllNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/readAll/{userId}")
    public ResponseEntity<Void> readAllNotifications(@PathVariable String userId) {
        notificationService.readAllNotifications(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/readUser/{id}")
    public ResponseEntity<Void> readNotifications(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        String reply = requestBody.get("reply");
        notificationService.readNotification(id, reply);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}