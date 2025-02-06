package castis.domain.usernavigationhistory.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_navigation_history")
public class UserNavigationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "menu_name", nullable = false, length = 50)
    private String menuName;

    @Column(name = "accessed_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime accessedAt;

    @Column(name = "user_agent", length = 50)
    private String userAgent;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;
}
