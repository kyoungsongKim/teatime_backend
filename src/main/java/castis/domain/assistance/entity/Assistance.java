package castis.domain.assistance.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import castis.domain.user.entity.User;

@Getter
@Setter
@Table(name = "assistance")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Assistance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String form;

    @Column
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "message_target_admin_id",
            foreignKey = @ForeignKey(name = "id")
    )
    private User messageTargetAdmin;

    @CreatedDate
    @Column
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedDate;

}
