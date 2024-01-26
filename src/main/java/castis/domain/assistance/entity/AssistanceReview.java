package castis.domain.assistance.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import castis.domain.user.entity.User;

@Getter
@Setter
@Table(name = "assistance_review")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class AssistanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = true)
    private String content;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", referencedColumnName = "userid")
    private User reviewer;

}
