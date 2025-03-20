package castis.domain.notification.repository;

import castis.domain.notification.entity.UserNotification;
import castis.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findByNotificationId(Long notificationId);
    void deleteByNotificationId(Long notificationId);
    List<UserNotification> findByUserOrderByCreatedAtDesc(User user);
}