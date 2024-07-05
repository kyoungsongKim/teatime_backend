package castis.domain.agreement.entity;

import castis.domain.agreement.util.AgreementType;
import castis.domain.agreement.util.AgreementTypeConverter;
import castis.domain.filesystem.entity.FileMeta;
import castis.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Table(name = "agreement")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Agreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileMeta file;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @Convert(converter = AgreementTypeConverter.class)
    private AgreementType type;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "amount")
    private Integer amount;

    @CreatedDate
    @Column(name = "create_date")
    private LocalDate createDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDate updatedDate;

    @Builder
    public Agreement(User user, FileMeta file, AgreementType type, LocalDate startDate, LocalDate endDate, Integer amount) {
        this.user = user;
        this.file = file;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
    }
}
