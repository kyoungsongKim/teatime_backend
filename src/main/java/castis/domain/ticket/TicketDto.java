package castis.domain.ticket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class TicketDto {
    private Long id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String project;
    private String site;
    private String username;
    private float nmd;
    private float emd;
    private boolean allDay;
    private String bgcolor;
    private String contents;

    public TicketDto(Ticket ticket) {
        this.id = ticket.getNo();
        this.title = ticket.getTitle();
        this.start = ticket.getStartTime();
        this.end = ticket.getEndTime();
        this.project = ticket.getProjectName();
        //this.site = ticket.get

        this.username = ticket.getUserName();
        this.nmd = ticket.getNmd();
        this.emd = ticket.getEmd();
        this.allDay = false;
        //this.bgcolor

        this.contents = ticket.getContent();
    }
}
