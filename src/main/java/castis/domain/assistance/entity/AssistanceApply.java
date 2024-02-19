package castis.domain.assistance.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import castis.domain.assistance.constant.AppliedAssistanceStatus;
import castis.domain.filesystem.entity.FileMeta;
import castis.domain.user.entity.User;

@Getter
@Setter
@Table(name = "assistance_apply")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class AssistanceApply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String content;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private List<FileMeta> files;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AppliedAssistanceStatus status;

    @CreatedDate
    @Column
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name = "applier_id")
    private User applier;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "assistance_id")
    private Assistance assistance;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "review_id")
    private AssistanceReview review;

}
