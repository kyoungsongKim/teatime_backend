package castis.domain.notification.service;

import castis.domain.agreement.entity.IUserAgreementInfo;
import castis.domain.notification.dto.NotificationRequest;
import castis.domain.notification.dto.NotificationResponse;
import castis.domain.notification.dto.UserNotificationResponse;
import castis.domain.notification.entity.Notification;
import castis.domain.notification.entity.UserNotification;
import castis.domain.notification.repository.NotificationRepository;
import castis.domain.notification.repository.UserNotificationRepository;
import castis.domain.user.CustomUserDetails;
import castis.domain.user.entity.User;
import castis.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserNotificationRepository userNotificationRepository;

    @Transactional
    public void createNotification(NotificationRequest request, String teamName) {
        User createUser = userRepository.findById(request.getCreateUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));
        Notification notification;

        if(request.getId() != null) {
            Optional<Notification> existingNotification = notificationRepository.findById(request.getId());
            if (existingNotification.isPresent()) {
                notification = existingNotification.get();
                notification.setTitle(request.getTitle());
                notification.setContent(request.getContent());
                notification.setIsGlobal(request.getIsGlobal());
                notification.setNotificationType(request.getNotificationType());
            } else {
                notification = null;
            }
        } else {
            notification = new Notification();
            notification.setUser(createUser);
            notification.setTitle(request.getTitle());
            notification.setContent(request.getContent());
            notification.setIsGlobal(request.getIsGlobal());
            notification.setNotificationType(request.getNotificationType());
        }

        if(notification == null) {
            throw new RuntimeException("알림을 찾을 수 없음");
        }

        notificationRepository.save(notification); // 변경사항 저장

        // 기존 유저 알림 삭제 후 새롭게 저장
        userNotificationRepository.deleteByNotificationId(notification.getId());

        List<User> users;
        if (request.getIsGlobal()) {
            if (teamName != null) {
                users = userRepository.findByTeamName(teamName);
            } else {
                users = userRepository.findAll();
            }
        } else {
            users = userRepository.findAllById(request.getUserIds());
        }

        users.forEach(user -> saveUserNotification(user, notification));
    }

    private void saveUserNotification(User user, Notification notification) {
        UserNotification userNotification = new UserNotification();
        userNotification.setUser(user);
        userNotification.setNotification(notification);
        userNotificationRepository.save(userNotification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getContent(),
                        notification.getIsGlobal(),
                        notification.getNotificationType(),
                        notification.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserNotificationResponse> getUserNotifications(Long id) {
        return userNotificationRepository.findByNotificationId(id)
                .stream()
                .map(userNotification -> new UserNotificationResponse(
                        userNotification.getId(),
                        userNotification.getNotification(),
                        userNotification.getUser(),
                        userNotification.getIsRead(),
                        userNotification.getReply(),
                        null,
                        userNotification.getCreatedAt()))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<UserNotificationResponse> getAllNotificationsByUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));

        return userNotificationRepository.findByUserOrderByIdDesc(user)
                .stream()
                .map(userNotification -> new UserNotificationResponse(
                        userNotification.getId(),
                        userNotification.getNotification(),
                        null,
                        userNotification.getIsRead(),
                        userNotification.getReply(),
                        userNotification.getNotification().getUser().getUserDetails().getAvatarImg(),
                        userNotification.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void readAllNotifications(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));

        userNotificationRepository.findByUserOrderByIdDesc(user)
                .forEach(userNotification -> {
                    userNotification.setIsRead(true);
                    userNotificationRepository.save(userNotification);
                });
    }

    @Transactional
    public void readNotification(Long id, String reply) {
        UserNotification userNotification = userNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자 알림을 찾을 수 없음"));

        userNotification.setIsRead(true);
        userNotification.setReply(reply);
        userNotificationRepository.save(userNotification);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}