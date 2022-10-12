package castis.domain.ticket;

import castis.domain.project.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public TicketDto(Ticket ticket, Project project) {
        this.id = ticket.getNo();
        this.teamName = ticket.getTeamName();
        this.userName = ticket.getUserName();
        this.title = ticket.getTitle();
        this.site = project.getSite();
        this.project = project.getProjectName();
        this.md = String.valueOf(ticket.getEmd().intValue());
        this.allDay = true;
        this.eventStartDate = ticket.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.eventEndDate = ticket.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.eventStartTime = ticket.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.eventEndTime = ticket.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.content = ticket.getContent();
    }
}
