package castis.domain.vacation.entity;

import castis.domain.ticket.entity.Ticket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vacation_history")
public class VacationHistory {
    public static final String STATUS_READY = "READY";
    public static final String STATUS_DONE = "DONE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long ticketNo;

    @Column
    private String userId;

    @Column
    private String memo;

    @Column
    private LocalDate createDate;

    @Column
    private LocalDate sendDate;

    @Column
    private String status;

    public VacationHistory(Ticket ticket) {
        this.ticketNo = ticket.getNo();
        this.userId = ticket.getUserName();
        this.memo = ticket.getContent();
        this.createDate = LocalDate.now();
        this.sendDate = ticket.getStartTime().toLocalDate();
        this.status = VacationHistory.STATUS_READY;
    }
}
