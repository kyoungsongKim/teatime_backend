package castis.domain.ticket.dto;

import castis.domain.ticket.entity.Ticket;
import castis.util.holiday.HolidayDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Setter
@Getter
@NoArgsConstructor
public class EventDto {
    private Long no;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private String className;
    private String color;

    public EventDto (Ticket ticket) {
        this.no = ticket.getNo();
        this.title = ticket.getProjectName();
        this.start = ticket.getStartTime();
        this.end = ticket.getEndTime();
        this.description = ticket.getContent();
        //this.className = "Lorem ipsum dolor eiu idunt ut labore";
        this.className = "fc-event-danger fc-event-solid-warning";
        this.color = ticket.getProject().getBgColor();

    }

    public EventDto (HolidayDto dto) {
        this.no = 0L;
        this.title = dto.getName();
        this.start = getLocalDateTime(dto.getDate(), 0, 0, 0);
        this.end = getLocalDateTime(dto.getDate(), 23, 59, 59);
        this.className = "fc-event-danger fc-event-solid-warning";
        this.color = "#FF1100";
    }

    private LocalDateTime getLocalDateTime(String dateStr, int hour, int min, int sec) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

        return date.atTime(hour, min, sec);
    }

}
