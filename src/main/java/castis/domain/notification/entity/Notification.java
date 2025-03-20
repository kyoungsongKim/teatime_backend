package castis.domain.notification.entity;

import castis.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "notification")
@Entity
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @OneToOne
    @JoinColumn(name = "create_user_id")
    private User user;

    @Column(name = "is_global")
    private Boolean isGlobal;

    @Column(name = "notification_type")
    private String notificationType;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
