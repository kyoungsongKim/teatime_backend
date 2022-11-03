package castis.domain.ticket.dto;

import castis.domain.ticket.entity.Ticket;
import castis.util.holiday.HolidayDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private String color;

    public EventDto (Ticket ticket) {
        this.no = ticket.getNo();
        this.title = ticket.getTitle();
        this.start = ticket.getStartTime().toLocalDate();
        this.end = ticket.getEndTime().toLocalDate();
        this.description = ticket.getContent();
        //this.className = "Lorem ipsum dolor eiu idunt ut labore";
        this.className = "fc-event-danger fc-event-solid-warning";
        this.color = ticket.getProject().getBgColor();

    }

    public EventDto (HolidayDto dto) {
        this.no = 0L;
        this.title = dto.getName();
        this.start = LocalDate.parse(dto.getDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.end = LocalDate.parse(dto.getDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.className = "fc-event-danger fc-event-solid-warning";
        this.color = "#FF1100";
    }

}
