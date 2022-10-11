package castis.domain.ticket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class TicketDto {
    private Long id;
    private String teamName;
    private String userName;
    private String title;
    private String site;
    private String project;
    private String md;
    private boolean allDay;
    private String eventStartDate;
    private String eventStartTime;
    private String eventEndDate;
    private String eventEndTime;
    private String content;
}
