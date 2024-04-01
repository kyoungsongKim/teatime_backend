package castis.domain.vacation.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "vacation_history")
public class VacationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userId;

    @Column
    private LocalDateTime eventStartDate;

    @Column
    private LocalDateTime eventEndDate;

    @Column
    private float amount;

    @Column
    private String type;

    @Column
    private String reason;

    @Column
    private String adminMemo;

    @CreatedDate
    @Column
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedDate;

}
