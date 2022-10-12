package castis.domain.ticket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class EventDto {
    private Long no;
    private String title;
    private LocalDate start;
    private LocalDate end;
    private String description;
    private String className;

    public EventDto (Ticket ticket) {
        this.no = ticket.getNo();
        this.title = ticket.getTitle();
        this.start = ticket.getStartTime().toLocalDate();
        this.end = ticket.getEndTime().toLocalDate();
        this.description = ticket.getContent();
        //this.className = "Lorem ipsum dolor eiu idunt ut labore";
        this.className = "fc-event-danger fc-event-solid-warning";

    }
}
