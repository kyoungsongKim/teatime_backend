package castis.domain.notification.controller;

import castis.domain.notification.dto.NotificationRequest;
import castis.domain.notification.dto.NotificationResponse;
import castis.domain.notification.dto.UserNotificationResponse;
import castis.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Void> createNotification(@RequestBody NotificationRequest request) {
        notificationService.createNotification(request);
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
    public ResponseEntity<Void> readNotifications(@PathVariable Long id, @RequestBody String reply) {
        notificationService.readNotification(id, reply);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}