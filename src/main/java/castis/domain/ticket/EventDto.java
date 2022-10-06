package castis.domain.ticket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class EventDto {
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private String className;

    public EventDto (Ticket ticket) {
        this.title = ticket.getTitle();
        this.start = ticket.getStartTime();
        this.end = ticket.getEndTime();
        this.description = ticket.getContent();
        this.className = "Lorem ipsum dolor eiu idunt ut labore";
    }
}
